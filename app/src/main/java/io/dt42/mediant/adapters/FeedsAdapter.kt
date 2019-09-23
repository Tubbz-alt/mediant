package io.dt42.mediant.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.google.protobuf.Timestamp
import io.dt42.mediant.R
import io.dt42.mediant.activities.ProofActivity
import io.dt42.mediant.models.Feed
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class FeedsAdapter(private val context: Context) :
    RecyclerView.Adapter<FeedsAdapter.FeedViewHolder>() {

    val feeds = SortedList<Feed>(Feed::class.java, object : SortedList.Callback<Feed>() {
        override fun areItemsTheSame(item1: Feed, item2: Feed): Boolean {
            return item1 == item2
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }

        override fun compare(o1: Feed, o2: Feed): Int {
            return o1.compareTo(o2)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }
    })

    override fun getItemCount() = feeds.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.apply {
            username.text = feeds[position].username
            date.text = convertToFormattedString(feeds[position].date)
            feeds[position].data?.also {
                image.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
            caption.text = feeds[position].caption
            showProofButton.setOnClickListener {
                //Log.d(TAG, feeds[position].caption)
                dispatchProofActivityIntent()
            }
        }
    }

    private fun dispatchProofActivityIntent() = Intent(context, ProofActivity::class.java).also {
        context.startActivity(it)
    }

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val image: ImageView = itemView.findViewById(R.id.image)
        val caption: TextView = itemView.findViewById(R.id.caption)
        val showProofButton: ImageButton = itemView.findViewById(R.id.showProofButton)
    }

}

fun convertToFormattedString(timestamp: Timestamp): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    val instant = Instant.ofEpochSecond(timestamp.seconds, timestamp.nanos.toLong())
    return formatter.format(instant)
}
