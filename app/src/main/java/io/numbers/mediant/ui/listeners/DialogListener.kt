package io.numbers.mediant.ui.listeners

import androidx.fragment.app.DialogFragment

interface DialogListener {
    fun onDialogPositiveClick(dialog: DialogFragment)
    fun onDialogNegativeClick(dialog: DialogFragment)
}