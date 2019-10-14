package io.numbers.mediant.ui.settings.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.numbers.mediant.R

class SharedPreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}