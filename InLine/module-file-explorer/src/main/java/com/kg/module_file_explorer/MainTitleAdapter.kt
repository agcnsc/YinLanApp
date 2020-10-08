package com.kg.module_file_explorer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainTitleAdapter(val items: List<Cursor>) : RecyclerView.Adapter<MainTitleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.get(parent, R.layout.title_view_item)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.getView<TextView>(R.id.fileexplorer_title).text = data.displayName
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            fun get(parent: ViewGroup, layoutId: Int): ViewHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
                return ViewHolder(itemView)
            }
        }

        fun <T : View> getView(viewId: Int): T {
            val v = view.findViewById<View>(viewId)
            return v as T
        }
    }
}