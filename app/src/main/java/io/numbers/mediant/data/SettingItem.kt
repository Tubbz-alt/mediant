package io.numbers.mediant.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SettingItem(
    @StringRes val title: Int,
    @StringRes val summary: Int,
    @DrawableRes val icon: Int
)