package io.numbers.mediant.ui.initialization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.R

class InitializationViewModel : ViewModel() {

    val loadingText = MutableLiveData(R.string.connect_to_ipfs)
}