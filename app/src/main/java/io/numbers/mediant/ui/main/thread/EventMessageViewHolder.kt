package io.numbers.mediant.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.numbers.mediant.R
import io.numbers.mediant.util.timestampToString
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType

class EventMessageViewHolder(itemView: View) :
    ThreadRecyclerViewAdapter.ViewHolder(itemView) {

    private val eventMessageTextView: TextView = itemView.findViewById(R.id.eventMessage)
    private val userNameTextView: TextView = itemView.findViewById(R.id.username)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)

    override fun bind(item: FeedItemData, type: FeedItemType) {
        eventMessageTextView.text = type.name
        userNameTextView.text = item.join.user.name
        dateTextView.text = timestampToString(item.join.date)
    }

    companion object {
        fun from(parent: ViewGroup): ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.layout_thread_event_message, parent, false)
            return EventMessageViewHolder(view)
        }
    }
}