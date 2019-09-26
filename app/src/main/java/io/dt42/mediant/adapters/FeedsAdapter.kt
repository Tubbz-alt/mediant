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
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import io.dt42.mediant.R
import io.dt42.mediant.activities.PROOF_BUNDLE_EXTRA
import io.dt42.mediant.activities.ProofActivity
import io.dt42.mediant.models.Feed
import io.textile.textile.Util.timestampToDate
import java.text.SimpleDateFormat
import java.util.*

class FeedsAdapter(private val context: Context, @LayoutRes private val resource: Int) :
    RecyclerView.Adapter<FeedsAdapter.FeedViewHolder>() {

    // TODO: replace Feed with FeedItemData
    // TODO: use DI
    val feeds = SortedList<Feed>(Feed::class.java, object : SortedList.Callback<Feed>() {
        override fun areItemsTheSame(item1: Feed, item2: Feed) = item1 == item2
        override fun areContentsTheSame(oldItem: Feed, newItem: Feed) = oldItem == newItem
        override fun compare(o1: Feed, o2: Feed) = o1.compareTo(o2)
        override fun onChanged(position: Int, count: Int) = notifyItemRangeChanged(position, count)
        override fun onInserted(position: Int, count: Int) =
            notifyItemRangeInserted(position, count)

        override fun onMoved(fromPosition: Int, toPosition: Int) =
            notifyItemMoved(fromPosition, toPosition)

        override fun onRemoved(position: Int, count: Int) = notifyItemRangeRemoved(position, count)
    })

    override fun getItemCount() = feeds.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context).inflate(resource, parent, false).let {
            FeedViewHolder(it)
        }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.apply {
            username.text = feeds[position].username
            date.text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(
                timestampToDate(feeds[position].date)
            )
            feeds[position].data?.also {
                image.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
            showProofButton.setOnClickListener { dispatchProofActivityIntent(feeds[position].caption) }
        }
    }

    private fun dispatchProofActivityIntent(proofBundleJson: String) =
        Intent(context, ProofActivity::class.java).apply {
            putExtra(PROOF_BUNDLE_EXTRA, proofBundleJson)
            context.startActivity(this)
        }

    // TODO: use DI
    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val image: ImageView = itemView.findViewById(R.id.image)
        val showProofButton: ImageButton = itemView.findViewById(R.id.showProofButton)
    }

}
