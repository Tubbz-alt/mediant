package io.numbers.mediant.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.data.Tab
import javax.inject.Inject

class MainViewModel @Inject constructor(val tabs: List<Tab>) : ViewModel() {

    val selectedOptionsItem = MutableLiveData<@androidx.annotation.IdRes Int>()
}