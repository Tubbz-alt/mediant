package io.dt42.mediant.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.google.protobuf.Timestamp
import io.dt42.mediant.R
import io.dt42.mediant.models.Feed
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class FeedsAdapter : RecyclerView.Adapter<FeedsAdapter.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = feeds[position].username
        holder.date.text = convertToFormattedString(feeds[position].date)
        feeds[position].data?.apply {
            holder.image.setImageBitmap(BitmapFactory.decodeByteArray(this, 0, this.size))
        }
        holder.caption.text = feeds[position].caption
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val image: ImageView = itemView.findViewById(R.id.image)
        val caption: TextView = itemView.findViewById(R.id.caption)
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
