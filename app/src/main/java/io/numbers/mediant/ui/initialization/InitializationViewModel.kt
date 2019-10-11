package io.numbers.mediant.ui.initialization

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.numbers.mediant.SingleLiveEvent
import io.numbers.mediant.util.safelyInvokeIfNodeOnline
import io.textile.textile.Textile
import io.textile.textile.TextileLoggingListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val TEXTILE_FOLDER_NAME = "textile"

class InitializationViewModel @Inject constructor(
    application: Application,
    private val textile: Textile
) : AndroidViewModel(application) {
    private val textilePath by lazy {
        File(application.applicationContext.filesDir, TEXTILE_FOLDER_NAME).absolutePath
    }
    val userName = MutableLiveData("")
    val userNameLength = Transformations.map(userName) { it.length }
    val areInputsEnabled = MutableLiveData(false)
    val startMainActivityEvent = SingleLiveEvent<Boolean>()

    init {
        if (Textile.isInitialized(textilePath)) launchTextile()
        else areInputsEnabled.value = true
    }

    private fun launchTextile(new: Boolean = false) {
        Textile.launch(getApplication<Application>().applicationContext, textilePath, false)
        textile.addEventListener(TextileLoggingListener())
        textile.safelyInvokeIfNodeOnline {
            // TODO: move profile setting to AccountCreateActivity
            if (new) textile.profile.setName(userName.value)
            // fire the single live event by setting arbitrary value
            startMainActivityEvent.postValue(true)
        }
    }

    // TODO: move wallet setting to AccountCreateActivity
    fun onCreateAccount() {
        // TODO: save phrase to shared preference
        val phrase = Textile.initializeCreatingNewWalletAndAccount(textilePath, false, false)
        Timber.i("Create new wallet: $phrase")
        areInputsEnabled.value = false
        launchTextile(new = true)
    }
}