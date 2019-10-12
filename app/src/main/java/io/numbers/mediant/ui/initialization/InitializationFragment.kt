package io.numbers.mediant.ui.initialization

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.numbers.mediant.R
import io.numbers.mediant.databinding.FragmentInitializationBinding

class InitializationFragment : Fragment() {

    lateinit var viewModel: InitializationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[InitializationViewModel::class.java]
        } ?: throw RuntimeException("Invalid activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentInitializationBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_initialization, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        button.setOnClickListener {
//            it.findNavController().navigate(R.id.action_initializationFragment_to_mainFragment)
//        }
//    }
}