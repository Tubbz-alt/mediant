package io.numbers.mediant.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import io.numbers.mediant.R
import io.numbers.mediant.activities.PROOF_BUNDLE_EXTRA
import io.numbers.mediant.activities.ProofActivity
import io.numbers.mediant.activities.TAG
import io.numbers.mediant.wrappers.TextileWrapper
import io.textile.textile.FeedItemData
import io.textile.textile.Util.timestampToDate
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class FeedsAdapter(@LayoutRes private val resource: Int) :
    RecyclerView.Adapter<FeedsAdapter.FeedViewHolder>(), CoroutineScope by MainScope() {

    var context: Context? = null

    val feeds = SortedList<FeedItemData>(
        FeedItemData::class.java,
        object : SortedList.Callback<FeedItemData>() {
            override fun areItemsTheSame(item1: FeedItemData, item2: FeedItemData) = item1 == item2
            override fun areContentsTheSame(oldItem: FeedItemData, newItem: FeedItemData) =
                oldItem == newItem

            override fun compare(o1: FeedItemData, o2: FeedItemData) =
                -timestampToDate(o1.files.date).compareTo(timestampToDate(o2.files.date))

            override fun onChanged(position: Int, count: Int) =
                notifyItemRangeChanged(position, count)

            override fun onInserted(position: Int, count: Int) =
                notifyItemRangeInserted(position, count)

            override fun onMoved(fromPosition: Int, toPosition: Int) =
                notifyItemMoved(fromPosition, toPosition)

            override fun onRemoved(position: Int, count: Int) =
                notifyItemRangeRemoved(position, count)
        })

    override fun getItemCount() = feeds.size()

    override fun getItemViewType(position: Int) = resource

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return LayoutInflater.from(parent.context).inflate(viewType, parent, false).let {
            if (viewType == R.layout.feed_personal) PersonalFeedViewHolder(it)
            else FeedViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        feeds[position].files.apply {
            holder.username.text = user.name
            holder.date.text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
                timestampToDate(date)
            )
            holder.showProofButton.setOnClickListener { dispatchProofActivityIntent(caption) }

            launch(Dispatchers.IO) {
                try {
                    TextileWrapper.getImageContent(this@apply)?.also {
                        withContext(Dispatchers.Main) {
                            holder.image.setImageBitmap(
                                BitmapFactory.decodeByteArray(it, 0, it.size)
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, Log.getStackTraceString(e))
                }
            }

            if (holder.itemViewType == R.layout.feed_personal) {
                holder as PersonalFeedViewHolder
                holder.publishButton.setOnClickListener {
                    launch { filesList?.also { TextileWrapper.publishFile(data, caption) } }
                }
                holder.deleteButton.setOnClickListener {
                    TextileWrapper.deleteFile(block)
                    for (i in 0 until feeds.size()) {
                        if (feeds[i].block == block) {
                            feeds.remove(feeds[i])
                            break
                        }
                    }
                }
            }
        }
    }

    private fun dispatchProofActivityIntent(proofBundleJson: String) =
        Intent(context, ProofActivity::class.java).apply {
            putExtra(PROOF_BUNDLE_EXTRA, proofBundleJson)
            context?.startActivity(this)
        }

    open class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val image: ImageView = itemView.findViewById(R.id.image)
        val showProofButton: ImageButton = itemView.findViewById(R.id.showProofButton)
    }

    class PersonalFeedViewHolder(itemView: View) : FeedViewHolder(itemView) {
        val publishButton: ImageButton = itemView.findViewById(R.id.publishButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }
}
