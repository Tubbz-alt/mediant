package io.numbers.mediant.ui.main.thread

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.numbers.mediant.R
import io.textile.textile.FeedItemData
import io.textile.textile.Util

class ImageCardViewHolder(itemView: View) : ThreadRecyclerViewAdapter.ViewHolder(itemView) {

    //        private val imageView: ImageView = itemView.findViewById(R.id.image)
    private val userNameTextView: TextView = itemView.findViewById(R.id.username)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)

    override fun bind(item: FeedItemData) {
        userNameTextView.text = item.files.user.name
        dateTextView.text = dateFormatter.format(Util.timestampToDate(item.files.date))
    }

    // TODO: get raw image
    companion object {
        fun from(parent: ViewGroup): ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.layout_thread_image_card, parent, false)
            return ImageCardViewHolder(view)
        }
    }
}