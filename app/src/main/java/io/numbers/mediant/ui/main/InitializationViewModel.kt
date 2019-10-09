package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class InitializationViewModel : ViewModel() {

    val userName = MutableLiveData("")

    fun onCreateAccount() = Timber.d(userName.value)
}