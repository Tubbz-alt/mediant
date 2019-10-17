package io.numbers.mediant.ui.main.thread

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.textile.textile.FeedItemData
import io.textile.textile.Util
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class ImageCardViewHolder(itemView: View, private val textileService: TextileService) :
    ThreadRecyclerViewAdapter.ViewHolder(itemView), CoroutineScope by MainScope() {

    private val imageView: ImageView = itemView.findViewById(R.id.image)
    private val userNameTextView: TextView = itemView.findViewById(R.id.username)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)

    override fun bind(item: FeedItemData) {
        userNameTextView.text = item.files.user.name
        dateTextView.text = dateFormatter.format(Util.timestampToDate(item.files.date))

        textileService.getImageContent(item.files) {
            launch(Dispatchers.Main) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup, textileService: TextileService):
                ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.layout_thread_image_card, parent, false)
            return ImageCardViewHolder(view, textileService)
        }
    }
}