package io.numbers.mediant.ui.initialization

import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.TextileService
import timber.log.Timber
import javax.inject.Inject

class InitializationViewModel @Inject constructor(textileService: TextileService) :
    ViewModel() {

    val userName = textileService.name

    fun onCreateAccount() = Timber.d(userName.value)
}