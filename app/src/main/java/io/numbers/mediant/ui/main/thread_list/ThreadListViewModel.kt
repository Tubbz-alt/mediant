package io.numbers.mediant.ui.main.thread_list

import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.numbers.mediant.BuildConfig.APPLICATION_ID
import io.numbers.mediant.BuildConfig.VERSION_NAME
import io.numbers.mediant.R
import io.numbers.mediant.ui.OnItemClickListener
import io.numbers.mediant.ui.OnItemMenuClickListener
import io.textile.pb.Model
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.Textile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ThreadListViewModel @Inject constructor(
    private val textile: Textile
) : ViewModel(), OnItemClickListener, OnItemMenuClickListener {

    val adapter = ThreadListRecyclerViewAdapter(this, this)
    val isLoading = MutableLiveData(false)

    init {
        textile.addEventListener(object : BaseTextileEventListener() {
            override fun threadAdded(threadId: String) {
                super.threadAdded(threadId)
                viewModelScope.launch(Dispatchers.Main) {
                    adapter.data.add(textile.threads.get(threadId))
                }
            }
        })
        loadThreadList()
    }

    override fun onItemClick(position: Int) {
        Timber.i("onClick $position")
    }

    override fun onItemMenuClick(position: Int, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_thread_info -> {
            }
            R.id.action_leave_thread -> leaveThread(adapter.data[position])
        }
        return true
    }

    fun loadThreadList() {
        isLoading.value = true
        adapter.data.clear()
        adapter.data.addAll(textile.threads.list().itemsList)
        isLoading.value = false
    }

    fun addThread() {
        val name = "placeholder"
        val schema = View.AddThreadConfig.Schema.newBuilder()
            .setPreset(View.AddThreadConfig.Schema.Preset.MEDIA)
            .build()
        val config = View.AddThreadConfig.newBuilder()
            .setKey(generateThreadKey(name))
            .setName(name)
            .setType(Model.Thread.Type.OPEN)
            .setSharing(Model.Thread.Sharing.SHARED)
            .setSchema(schema)
            .build()
        textile.threads.add(config)
    }

    private fun generateThreadKey(name: String): String {
        var key: String
        do {
            key =
                "$APPLICATION_ID.$VERSION_NAME.$name.${textile.profile.get().address}.${System.currentTimeMillis()}"
        } while (textile.threads.list().itemsList.any { it.key == key })
        return key
    }

    private fun leaveThread(thread: Model.Thread) {
        // Must remove UI item first and then remove backend Textile thread item.
        if (adapter.data.remove(thread)) textile.threads.remove(thread.id)
        else Timber.e("Cannot find the target thread to remove.")
    }
}