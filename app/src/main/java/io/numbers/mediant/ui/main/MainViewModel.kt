package io.numbers.mediant.ui.main

import androidx.lifecycle.ViewModel
import io.numbers.mediant.repo.TextileRepository
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(textileRepository: TextileRepository) : ViewModel() {

    var name = textileRepository.nodeOnline

    fun onClick() {
        name.value = name.value?.not()
        Timber.d("name has changed to: ${name.value}")
    }
}