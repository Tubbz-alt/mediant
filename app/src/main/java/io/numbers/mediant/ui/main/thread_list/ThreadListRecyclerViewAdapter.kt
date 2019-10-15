package io.numbers.mediant.ui.main.thread_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
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

    var data = listOf<Model.Thread>()
        set(value) {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    field[oldItemPosition].id == value[newItemPosition].id

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                    field[oldItemPosition].id == value[newItemPosition].id

                override fun getOldListSize() = field.size

                override fun getNewListSize() = value.size
            }, true).apply { dispatchUpdatesTo(this@ThreadListRecyclerViewAdapter) }
            field = value
        }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, itemClickListener, itemMenuClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(
        itemView: View,
        private val itemClickListener: ItemClickListener,
        private val itemMenuClickListener: ItemMenuClickListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

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