package io.numbers.mediant.ui.main.thread

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.FeedItemData
import javax.inject.Inject

class ThreadViewModel @Inject constructor(private val textileService: TextileService) :
    ViewModel() {

    private lateinit var threadId: String
    var feedList = MutableLiveData<List<FeedItemData>>()
    val isLoading = MutableLiveData(false)

    fun setThreadId(value: String) {
        threadId = value
        initFeedListLiveData()
    }

    private fun initFeedListLiveData() {
        loadFeedList()
        textileService.addEventListener(object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                if (threadId == this@ThreadViewModel.threadId) {
                    feedList.postValue(textileService.listFeeds(threadId).filter {
                        textileService.feedItemSubtype.contains(it.type)
                    })
                }
            }
        })
    }

    fun loadFeedList() {
        isLoading.value = true
        feedList.value = textileService.listFeeds(threadId)
        isLoading.value = false
    }
}