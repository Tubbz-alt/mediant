package io.numbers.mediant.ui.main.thread

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.api.textile.hasSameContentsTo
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

class ThreadRecyclerViewAdapter(private val textileService: TextileService) :
    RecyclerView.Adapter<ThreadRecyclerViewAdapter.ViewHolder>() {

    var data: List<FeedItemData>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<FeedItemData>() {
        override fun areItemsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
            oldItem.block == newItem.block

        override fun areContentsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
            oldItem.hasSameContentsTo(newItem)
    })

    private val feedItemTypeValues = FeedItemType.values() // cache the types

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = data[position].type.ordinal

    @ExperimentalCoroutinesApi
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (feedItemTypeValues[viewType]) {
            FeedItemType.FILES -> ImageCardViewHolder.from(parent, textileService)
            FeedItemType.JOIN -> JoinMessageViewHolder.from(parent)
            else -> throw IllegalStateException("Cannot display the feed item type: ${feedItemTypeValues[viewType]}")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

        abstract fun bind(item: FeedItemData)
    }
}