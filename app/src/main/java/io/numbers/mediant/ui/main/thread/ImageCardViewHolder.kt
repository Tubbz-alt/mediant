package io.numbers.mediant.ui.main.thread

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import io.numbers.mediant.R
import io.numbers.mediant.api.textile.TextileService
import io.numbers.mediant.ui.listeners.FeedItemListener
import io.numbers.mediant.util.timestampToString
import io.textile.textile.FeedItemData
import io.textile.textile.FeedItemType
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class ImageCardViewHolder(
    itemView: View,
    private val textileService: TextileService,
    private val listener: FeedItemListener,
    private val isPersonal: Boolean
) : ThreadRecyclerViewAdapter.ViewHolder(itemView) {

    private var job = Job()

    private val imageView: ImageView = itemView.findViewById(R.id.image)
    private val userNameTextView: TextView = itemView.findViewById(R.id.userName)
    private val dateTextView: TextView = itemView.findViewById(R.id.date)
    private val showProofButton: Button = itemView.findViewById(R.id.showProofButton)
    private val publishButton: Button = itemView.findViewById(R.id.publishButton)
    private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

    override fun bind(item: FeedItemData, type: FeedItemType) {
        job.cancel()

        userNameTextView.text = item.files.user.name
        dateTextView.text = timestampToString(item.files.date)
        showProofButton.setOnClickListener { listener.onShowProof(item) }
        publishButton.setOnClickListener { listener.onPublish(item) }
        deleteButton.setOnClickListener { listener.onDelete(item) }

        textileService.getImageContent(item.files) {
            job = CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
        }

        if (isPersonal) {
            publishButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        } else {
            publishButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            textileService: TextileService,
            listener: FeedItemListener,
            isPersonal: Boolean
        ): ThreadRecyclerViewAdapter.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view =
                layoutInflater.inflate(R.layout.layout_thread_image_card, parent, false)
            return ImageCardViewHolder(view, textileService, listener, isPersonal)
        }
    }
}