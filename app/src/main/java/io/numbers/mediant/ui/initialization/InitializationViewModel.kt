package io.numbers.mediant.ui.initialization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import javax.inject.Inject

class InitializationViewModel @Inject constructor() :
    ViewModel() {

    val userName = MutableLiveData("your name")

    fun onCreateAccount() = Timber.d(userName.value)
}