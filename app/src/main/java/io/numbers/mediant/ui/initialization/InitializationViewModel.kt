package io.numbers.mediant.ui.initialization

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.numbers.mediant.R
import io.numbers.mediant.SingleLiveEvent
import io.numbers.mediant.util.isNodeOnline
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

    val loadingText = MutableLiveData(R.string.connect_to_ipfs)
    val navToMainFragmentEvent = SingleLiveEvent<Unit>()

    private val textilePath by lazy {
        File(application.applicationContext.filesDir, TEXTILE_FOLDER_NAME).absolutePath
    }

    init {
        Timber.d("init")
        if (!textile.isNodeOnline) initializeTextile()
        textile.safelyInvokeIfNodeOnline {
            // fire the single live event by posting an arbitrary value at worker thread
            navToMainFragmentEvent.postValue(null)
        }
    }

    private fun initializeTextile() {
        Timber.d("textile init")
        if (!Textile.isInitialized(textilePath)) {
            loadingText.value = R.string.create_wallet
            // TODO: save phrase to shared preference
            val phrase =
                Textile.initializeCreatingNewWalletAndAccount(textilePath, false, false)
            Timber.i("Create new wallet: $phrase")
        }
        loadingText.value = R.string.connect_to_ipfs
        Textile.launch(getApplication<Application>().applicationContext, textilePath, false)
        textile.addEventListener(TextileLoggingListener())
    }
}