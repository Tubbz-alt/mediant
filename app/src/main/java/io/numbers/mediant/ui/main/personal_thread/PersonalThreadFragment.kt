package io.numbers.mediant.ui.main.personal_thread

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import io.numbers.mediant.ui.main.thread.ThreadFragment
import io.numbers.mediant.ui.tab.TabFragment
import io.numbers.mediant.viewmodel.EventObserver

class PersonalThreadFragment : ThreadFragment(), TabFragment {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.scrollToTopEvent.observe(
            viewLifecycleOwner, EventObserver { smoothScrollToTop() })
    }

    override fun smoothScrollToTop() {
        binding.recyclerView.layoutManager?.startSmoothScroll(object :
            LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference() = SNAP_TO_START
        }.apply { targetPosition = 0 })
    }
}