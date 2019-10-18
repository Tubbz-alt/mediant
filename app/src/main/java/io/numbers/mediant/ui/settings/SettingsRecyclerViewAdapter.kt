package io.numbers.mediant.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.R
import io.numbers.mediant.ui.listeners.ItemClickListener

class SettingsRecyclerViewAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder>() {

    var data: List<SettingItem>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<SettingItem>() {

        override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem) =
            oldItem == newItem
    })

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.from(parent, itemClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    class ViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
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

        override fun onClick(view: View) = itemClickListener.onItemClick(adapterPosition)

        companion object {
            fun from(parent: ViewGroup, itemClickListener: ItemClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_setting_item, parent, false)
                return ViewHolder(view, itemClickListener)
            }
        }
    }
}