package io.numbers.mediant.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.hasSameContentsTo
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import io.textile.textile.Util.timestampToDate
import java.text.SimpleDateFormat
import java.util.*

class ThreadRecyclerViewAdapter : RecyclerView.Adapter<ThreadRecyclerViewAdapter.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (feedItemTypeValues[viewType]) {
            FeedItemType.FILES -> ImageCardViewHolder.from(parent)
            FeedItemType.JOIN -> JoinMessageViewHolder.from(parent)
            else -> throw IllegalStateException("Cannot display the feed item type: ${feedItemTypeValues[viewType]}")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    class ImageCardViewHolder(itemView: View) : ViewHolder(itemView) {

        //        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val userNameTextView: TextView = itemView.findViewById(R.id.username)
        private val dateTextView: TextView = itemView.findViewById(R.id.date)

        override fun bind(item: FeedItemData) {
            userNameTextView.text = item.files.user.name
            dateTextView.text = dateFormatter.format(timestampToDate(item.files.date))
        }

        // TODO: get raw image
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    layoutInflater.inflate(R.layout.layout_thread_image_card, parent, false)
                return ImageCardViewHolder(view)
            }
        }
    }

    class JoinMessageViewHolder(itemView: View) : ViewHolder(itemView) {

        private val eventMessageTextView: TextView = itemView.findViewById(R.id.eventMessage)
        private val userNameTextView: TextView = itemView.findViewById(R.id.username)
        private val dateTextView: TextView = itemView.findViewById(R.id.date)

        override fun bind(item: FeedItemData) {
            eventMessageTextView.text =
                itemView.context.resources.getString(R.string.event_message_join_thread)
            userNameTextView.text = item.join.user.name
            dateTextView.text = dateFormatter.format(timestampToDate(item.join.date))
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    layoutInflater.inflate(R.layout.layout_thread_event_message, parent, false)
                return JoinMessageViewHolder(view)
            }
        }
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

        abstract fun bind(item: FeedItemData)
    }
}