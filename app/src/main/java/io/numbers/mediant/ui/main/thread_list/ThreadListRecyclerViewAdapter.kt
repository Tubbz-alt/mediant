package io.numbers.mediant.ui.main.thread_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import io.numbers.mediant.R
import io.numbers.mediant.ui.OnItemClickListener
import io.numbers.mediant.ui.OnItemMenuClickListener
import io.textile.pb.Model

class ThreadListRecyclerViewAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val onItemMenuClickListener: OnItemMenuClickListener
) :
    RecyclerView.Adapter<ThreadListRecyclerViewAdapter.ViewHolder>() {

    val data = SortedList<Model.Thread>(
        Model.Thread::class.java, object : SortedList.Callback<Model.Thread>() {

            override fun areContentsTheSame(oldItem: Model.Thread, newItem: Model.Thread) =
                oldItem == newItem

            override fun areItemsTheSame(item1: Model.Thread, item2: Model.Thread) =
                item1.id == item2.id

            override fun onMoved(fromPosition: Int, toPosition: Int) =
                notifyItemMoved(fromPosition, toPosition)

            override fun onChanged(position: Int, count: Int) =
                notifyItemRangeChanged(position, count)

            override fun onInserted(position: Int, count: Int) =
                notifyItemRangeInserted(position, count)

            override fun onRemoved(position: Int, count: Int) =
                notifyItemRangeRemoved(position, count)

            override fun compare(o1: Model.Thread, o2: Model.Thread): Int {
                // Head blocks might not be initialize right after the thread creation.
                return o1.name.compareTo(o2.name)
            }
        })

    override fun getItemCount() = data.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onItemClickListener, onItemMenuClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(
        itemView: View,
        private val onItemClickListener: OnItemClickListener,
        private val onItemMenuClickListener: OnItemMenuClickListener
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

        override fun onClick(view: View) = onItemClickListener.onItemClick(adapterPosition)

        override fun onLongClick(view: View): Boolean {
            PopupMenu(view.context, view).apply {
                setOnMenuItemClickListener {
                    onItemMenuClickListener.onItemMenuClick(adapterPosition, it)
                }
                inflate(R.menu.thread_actions)
                show()
            }
            return true
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener,
                onItemMenuClickListener: OnItemMenuClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_thread_item, parent, false)
                return ViewHolder(view, onItemClickListener, onItemMenuClickListener)
            }
        }
    }
}