package io.numbers.mediant.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.numbers.mediant.R
import io.textile.textile.FeedItemData
import io.textile.textile.Util

class JoinMessageViewHolder(itemView: View) : ThreadRecyclerViewAdapter.ViewHolder(itemView) {

    private val eventMessageTextView: TextView = itemView.findViewById(R.id.eventMessage)
    private val userNameTextView: TextView = itemView.findViewById(R.id.username)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)

    override fun bind(item: FeedItemData) {
        eventMessageTextView.text =
            itemView.context.resources.getString(R.string.event_message_join_thread)
        userNameTextView.text = item.join.user.name
        dateTextView.text = dateFormatter.format(Util.timestampToDate(item.join.date))
    }

    companion object {
        fun from(parent: ViewGroup): ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.layout_thread_event_message, parent, false)
            return JoinMessageViewHolder(view)
        }
    }
}