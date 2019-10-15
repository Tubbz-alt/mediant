package io.numbers.mediant.ui.initialization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.R
import io.numbers.mediant.api.TextileService
import io.numbers.mediant.util.Event
import io.textile.textile.TextileLoggingListener
import javax.inject.Inject

class InitializationViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    val loadingText = MutableLiveData(R.string.connect_to_ipfs)
    val navToMainFragmentEvent = MutableLiveData<Event<Unit>>()

    init {
        if (!textileService.hasLaunched.value!!) initializeTextile()
        textileService.safelyInvokeIfNodeOnline {
            // fire the single live event by posting an arbitrary value at worker thread
            navToMainFragmentEvent.postValue(Event(Unit))
        }
    }

    private fun initializeTextile() {
        if (!textileService.hasInitialized()) {
            loadingText.value = R.string.create_wallet
            textileService.createNewWalletAndAccount()
        }
        loadingText.value = R.string.connect_to_ipfs
        textileService.launch()
        textileService.addEventListener(TextileLoggingListener())
    }
}