package io.numbers.mediant.ui.main.thread

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.numbers.mediant.R
import io.numbers.mediant.ui.listeners.DialogListener

class FeedDeletionDialogFragment : DialogFragment() {

    lateinit var listener: DialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            builder.setTitle(R.string.deletion_confirmation_title)
                .setPositiveButton(R.string.deletion_confirmation_positive) { _, _ ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(R.string.deletion_confirmation_negative) { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}