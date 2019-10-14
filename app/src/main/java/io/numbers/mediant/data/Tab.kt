package io.numbers.mediant.data

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

data class Tab(@StringRes val title: Int, val fragment: Fragment)