package io.numbers.mediant.repo

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextileRepository @Inject constructor() {
    var nodeOnline = MutableLiveData(false)
}