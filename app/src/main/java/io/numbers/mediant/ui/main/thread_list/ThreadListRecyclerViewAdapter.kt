package io.numbers.mediant.ui.main.thread_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.R
import io.numbers.mediant.ui.ItemClickListener
import io.numbers.mediant.ui.ItemMenuClickListener
import io.textile.pb.Model

class ThreadListRecyclerViewAdapter(
    private val itemClickListener: ItemClickListener,
    private val itemMenuClickListener: ItemMenuClickListener
) :
    RecyclerView.Adapter<ThreadListRecyclerViewAdapter.ViewHolder>() {

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
        ViewHolder.from(parent, itemClickListener, itemMenuClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    class ViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val itemMenuClickListener: ItemMenuClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        private val threadNameTextView: TextView = itemView.findViewById(R.id.threadName)
        private val threadIdTextView: TextView = itemView.findViewById(R.id.threadId)

        fun bind(item: Model.Thread) {
            threadNameTextView.text = item.name
            threadIdTextView.text = item.id
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(adapterPosition)

        override fun onLongClick(view: View): Boolean {
            PopupMenu(view.context, view).apply {
                setOnMenuItemClickListener {
                    itemMenuClickListener.onItemMenuClick(adapterPosition, it)
                }
                inflate(R.menu.thread_actions)
                show()
            }
            return true
        }

        companion object {
            fun from(
                parent: ViewGroup,
                itemClickListener: ItemClickListener,
                itemMenuClickListener: ItemMenuClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_thread_item, parent, false)
                return ViewHolder(view, itemClickListener, itemMenuClickListener)
            }
        }
    }
}