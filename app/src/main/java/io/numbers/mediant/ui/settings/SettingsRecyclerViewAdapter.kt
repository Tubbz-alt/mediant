package io.numbers.mediant.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.numbers.mediant.R

class SettingsRecyclerViewAdapter(private val data: List<SettingItem>) :
    RecyclerView.Adapter<SettingsRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val summaryTextView: TextView = itemView.findViewById(R.id.summary)
        private val iconImageView: ImageView = itemView.findViewById(R.id.icon)

        fun bind(item: SettingItem) {
            titleTextView.setText(item.title)
            summaryTextView.setText(item.summary)
            iconImageView.setImageResource(item.icon)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.layout_setting_item, parent, false)
                return ViewHolder(view)
            }
        }
    }
}