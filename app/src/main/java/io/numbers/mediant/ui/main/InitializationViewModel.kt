package io.numbers.mediant.ui.main

import androidx.lifecycle.ViewModel
import io.numbers.mediant.repo.TextileRepository
import timber.log.Timber

class InitializationViewModel : ViewModel() {

    private val textileRepository: TextileRepository = TextileRepository()
    var name = textileRepository.nodeOnline

    fun onClick() {
        name.value = name.value?.not()
        Timber.d("name has changed to: ${name.value}")
    }
}