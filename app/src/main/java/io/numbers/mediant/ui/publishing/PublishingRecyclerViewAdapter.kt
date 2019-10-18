package io.numbers.mediant.ui.publishing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import io.numbers.mediant.R
import io.numbers.mediant.ui.listeners.ItemClickListener
import io.textile.pb.Model

class PublishingRecyclerViewAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<PublishingRecyclerViewAdapter.ViewHolder>() {

    var data: List<Model.Thread>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Model.Thread>() {

        override fun areItemsTheSame(oldItem: Model.Thread, newItem: Model.Thread) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Model.Thread, newItem: Model.Thread) =
            oldItem == newItem
    })

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent, itemClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    class ViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(itemView) {

        private val publishButton: MaterialButton = itemView.findViewById(R.id.publishButton)
        private val threadNameTextView: TextView = itemView.findViewById(R.id.threadName)
        private val threadIdTextView: TextView = itemView.findViewById(R.id.threadId)

        fun bind(item: Model.Thread) {
            threadNameTextView.text = item.name
            threadIdTextView.text = item.id
            publishButton.setOnClickListener {
                publishButton.isClickable = false
                publishButton.setIconResource(R.drawable.ic_done_black_24dp)
                itemClickListener.onItemClick(adapterPosition)
            }
        }

        companion object {
            fun from(parent: ViewGroup, itemClickListener: ItemClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =
                    layoutInflater.inflate(R.layout.layout_publishing_thread_item, parent, false)
                return ViewHolder(view, itemClickListener)
            }
        }
    }
}