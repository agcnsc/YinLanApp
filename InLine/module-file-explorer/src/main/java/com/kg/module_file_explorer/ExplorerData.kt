package com.kg.module_file_explorer

import java.util.*

class LocalFileInfo(name: String, path: String, folder: Boolean) : Comparable<LocalFileInfo> {
    var fileName: String = ""
    var filePath: String = ""
    var isFolder: Boolean = false

    init {
        fileName = name
        filePath = path
        isFolder = folder
    }

    override fun compareTo(other: LocalFileInfo): Int {
        if (isFolder && !other.isFolder) {
            return -1
        } else if (!isFolder && other.isFolder) {
            return 1
        } else {
            return this.fileName.toLowerCase(Locale.getDefault()).compareTo(other.fileName.toLowerCase(Locale.getDefault()))
        }
    }

}

class Cursor(path:String, name: String) {
    var currentPath:String = path
    var displayName:String = name
    var currentOffset:Int = 0
    var currentPosition:Int = 0

}

interface MainFragmentImpl {
    fun showProgressDialog()
    fun dismissProgressDialog()
    fun updateList()
    fun scrollTo(offset:Int, pos:Int)
    fun finishActivity()
    fun updateTitle()
}

