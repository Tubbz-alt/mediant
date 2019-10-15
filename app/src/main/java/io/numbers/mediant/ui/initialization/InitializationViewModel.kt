package io.numbers.mediant.ui.initialization

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.numbers.mediant.R
import io.numbers.mediant.api.TextileService
import io.numbers.mediant.util.Event
import io.textile.textile.Textile
import io.textile.textile.TextileLoggingListener
import timber.log.Timber
import java.io.File
import javax.inject.Inject

private const val TEXTILE_FOLDER_NAME = "textile"

class InitializationViewModel @Inject constructor(
    application: Application,
    private val sharedPreferences: SharedPreferences,
    private val textileService: TextileService
) : AndroidViewModel(application) {

    val loadingText = MutableLiveData(R.string.connect_to_ipfs)
    val navToMainFragmentEvent = MutableLiveData<Event<Unit>>()

    private val textilePath by lazy {
        File(application.applicationContext.filesDir, TEXTILE_FOLDER_NAME).absolutePath
    }

    init {
        if (!textileService.isNodeOnline.value!!) initializeTextile()
        textileService.safelyInvokeIfNodeOnline {
            // fire the single live event by posting an arbitrary value at worker thread
            navToMainFragmentEvent.postValue(Event(Unit))
        }
    }

    private fun initializeTextile() {
        if (!Textile.isInitialized(textilePath)) {
            loadingText.value = R.string.create_wallet
            // TODO: save phrase to shared preference
            val phrase =
                Textile.initializeCreatingNewWalletAndAccount(textilePath, false, false)
            Timber.i("Create new wallet: $phrase")
            sharedPreferences.edit().putString(
                getApplication<Application>().resources.getString(R.string.key_wallet_recovery_phrase),
                phrase
            ).apply()
        }
        loadingText.value = R.string.connect_to_ipfs
        Textile.launch(getApplication<Application>().applicationContext, textilePath, false)
        textileService.addEventListener(TextileLoggingListener())
    }
}