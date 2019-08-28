package io.dt42.mediant.ui.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import io.dt42.mediant.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}