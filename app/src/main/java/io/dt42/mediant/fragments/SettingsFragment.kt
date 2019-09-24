package io.dt42.mediant.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.dt42.mediant.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}