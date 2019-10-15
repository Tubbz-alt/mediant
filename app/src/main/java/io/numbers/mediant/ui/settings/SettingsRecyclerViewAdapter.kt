package io.numbers.mediant.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import io.numbers.mediant.R
import io.numbers.mediant.data.SettingItem
import io.numbers.mediant.ui.OnItemClickListener

class SettingsRecyclerViewAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder>() {

    val data = SortedList<SettingItem>(
        SettingItem::class.java,
        object : SortedList.Callback<SettingItem>() {

            override fun areItemsTheSame(item1: SettingItem, item2: SettingItem) = item1 == item2
            override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem) =
                oldItem == newItem

            override fun onMoved(fromPosition: Int, toPosition: Int) =
                notifyItemMoved(fromPosition, toPosition)

            override fun onChanged(position: Int, count: Int) =
                notifyItemRangeChanged(position, count)

            override fun onInserted(position: Int, count: Int) =
                notifyItemRangeInserted(position, count)

            override fun onRemoved(position: Int, count: Int) =
                notifyItemRangeRemoved(position, count)

            override fun compare(o1: SettingItem, o2: SettingItem) = o1.title.compareTo(o2.title)
        })

    override fun getItemCount() = data.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val summaryTextView: TextView = itemView.findViewById(R.id.summary)
        private val iconImageView: ImageView = itemView.findViewById(R.id.icon)

        fun bind(item: SettingItem) {
            titleTextView.setText(item.title)
            summaryTextView.setText(item.summary)
            iconImageView.setImageResource(item.icon)
        }

        override fun onClick(view: View) = onItemClickListener.onItemClick(adapterPosition)

        companion object {
            fun from(parent: ViewGroup, onItemClickListener: OnItemClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_setting_item, parent, false)
                return ViewHolder(view, onItemClickListener)
            }
        }
    }
}