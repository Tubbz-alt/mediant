package io.numbers.mediant.ui.main.thread

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.util.PreferenceHelper
import io.numbers.mediant.viewmodel.Event
import io.textile.pb.View
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import javax.inject.Inject

class ThreadViewModel @Inject constructor(
    private val textileService: TextileService,
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {

    private lateinit var threadId: String
    val isPersonal: Boolean
        get() = preferenceHelper.personalThreadId == threadId
    val feedList = MutableLiveData<List<FeedItemData>>()
    val isLoading = MutableLiveData(false)
    val scrollToTopEvent = MutableLiveData<Event<Unit>>()

    fun setThreadId(value: String) {
        threadId = value
        initFeedListLiveData()
    }

    private fun initFeedListLiveData() {
        loadFeedList()
        textileService.addEventListener(object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                super.threadUpdateReceived(threadId, feedItemData)
                if (threadId == this@ThreadViewModel.threadId
                    && textileService.isSupportedFeedItemType(feedItemData)
                ) {
                    feedList.postValue(textileService.listFeeds(threadId).filter {
                        textileService.isSupportedFeedItemType(it)
                    })

                    if (feedItemData.type != FeedItemType.IGNORE) {
                        scrollToTopEvent.postValue(Event(Unit))
                    }
                }
            }
        })
    }

    fun loadFeedList() {
        isLoading.value = true
        feedList.value = textileService.listFeeds(threadId).filter {
            textileService.isSupportedFeedItemType(it)
        }
        isLoading.value = false
    }

    fun deleteFile(files: View.Files) = textileService.ignoreFile(files)
}