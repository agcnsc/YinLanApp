package com.kg.module_file_explorer

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class MainAdapter(val items: List<LocalFileInfo>) : RecyclerView.Adapter<MainAdapter.ViewHolder>(), FastScrollRecyclerView.SectionedAdapter {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.get(parent, R.layout.recycler_view_item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.getView<TextView>(R.id.fileexplorer_title1).text = data.fileName
//        holder.getView<TextView>(R.id.fileexplorer_title2).text = data.filePath

        if (data.isFolder) {
            holder.getView<ImageView>(R.id.fileexplorer_icon).setImageResource(R.drawable.ic_folder_open_black_24dp)
        } else {
            holder.getView<ImageView>(R.id.fileexplorer_icon).setImageResource(R.drawable.ic_insert_drive_file_black_24dp)
        }

    }

    override fun getSectionName(position: Int): String {
        val data = items[position]
        return data.fileName[0].toString()
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

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
