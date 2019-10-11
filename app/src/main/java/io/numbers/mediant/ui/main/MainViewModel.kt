package io.numbers.mediant.ui.main

import androidx.lifecycle.ViewModel
import io.textile.textile.Textile
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val textile: Textile
) : ViewModel() {
    val userName: String = textile.profile.name()

    fun onCreateAccount() = Timber.d("name: ${textile.profile.name()}")
}