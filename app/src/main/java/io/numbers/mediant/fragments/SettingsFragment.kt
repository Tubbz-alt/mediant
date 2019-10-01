package io.numbers.mediant.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.numbers.mediant.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) =
        setPreferencesFromResource(R.xml.preferences, rootKey)
}