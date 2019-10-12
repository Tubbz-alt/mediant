package io.numbers.mediant.ui.initialization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.R
import io.numbers.mediant.SingleLiveEvent
import javax.inject.Inject

class InitializationViewModel @Inject constructor() : ViewModel() {

    val loadingText = MutableLiveData(R.string.connect_to_ipfs)
    val navToMainFragmentEvent = SingleLiveEvent<Unit>()
}