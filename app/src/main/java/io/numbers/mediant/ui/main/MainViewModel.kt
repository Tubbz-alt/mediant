package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.textile.textile.Textile
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val textile: Textile
) : ViewModel() {

    val text = MutableLiveData("logProfile")

    fun onClick() = try {
        Timber.d("${textile.profile.get()}")
    } catch (e: Exception) {
        Timber.e(e)
    }
}