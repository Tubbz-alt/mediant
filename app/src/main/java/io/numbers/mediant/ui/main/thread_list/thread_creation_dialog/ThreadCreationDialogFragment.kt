package io.numbers.mediant.ui.main.thread_list.thread_creation_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerDialogFragment
import io.numbers.mediant.R
import io.numbers.mediant.databinding.DialogThreadCreationBinding
import io.numbers.mediant.ui.listeners.DialogListener
import io.numbers.mediant.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class ThreadCreationDialogFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var viewModel: ThreadCreationDialogViewModel

    lateinit var listener: DialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
            this, viewModelProviderFactory
        )[ThreadCreationDialogViewModel::class.java]
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding: DialogThreadCreationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_thread_creation,
                null,
                false
            )
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            val builder = MaterialAlertDialogBuilder(it)
            builder.setView(binding.root)
                .setTitle(R.string.create_thread)
                .setPositiveButton(R.string.create) { _, _ -> listener.onDialogPositiveClick(this) }
                .setNegativeButton(R.string.cancel) { _, _ -> listener.onDialogNegativeClick(this) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}