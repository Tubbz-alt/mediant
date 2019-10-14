package io.numbers.mediant.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.R
import io.numbers.mediant.data.SettingItem

class SettingsRecyclerViewAdapter(
    private val data: List<SettingItem>,
    private val onItemListener: OnItemListener
) :
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onItemListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(itemView: View, private val onItemListener: OnItemListener) :
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

        override fun onClick(view: View) {
            onItemListener.onItemClick(adapterPosition)
        }

        companion object {
            fun from(parent: ViewGroup, onItemListener: OnItemListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_setting_item, parent, false)
                return ViewHolder(view, onItemListener)
            }
        }
    }

    interface OnItemListener {
        fun onItemClick(position: Int)
    }
}