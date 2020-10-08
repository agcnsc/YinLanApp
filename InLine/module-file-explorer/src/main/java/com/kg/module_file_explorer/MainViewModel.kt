package com.kg.module_file_explorer

import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.UserHandle
import android.os.storage.StorageManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.Constructor
import java.lang.reflect.Method


class MainViewModel(val app: Application) : AndroidViewModel(app) {
    val openFileUtil = OpenFileUtil(app.applicationContext)
    var data: MutableList<LocalFileInfo> = mutableListOf()
    var mainFragmentDelegate: MainFragmentImpl? = null
    var cursors: MutableList<Cursor> = mutableListOf()

    init {
        cursors.clear()
        val c = Cursor(Environment.getExternalStorageDirectory().path, "Root")
        cursors.add(c)
    }

    fun updateIndex(offset: Int, pos: Int) {
        cursors.lastOrNull()?.currentOffset = offset
        cursors.lastOrNull()?.currentPosition = pos
    }

    fun onClickItem(position: Int) {
        val info = data[position]
        if (info.isFolder) {
            cursors.add(Cursor(info.filePath, info.fileName))
            updateList()
        } else {
            openFileUtil.openFile(info.filePath)
        }
    }

    fun onClickTitleItem(position: Int) {
        if (position == cursors.size - 1) {
            return
        }

        while (true) {
            if (position < cursors.lastIndex) {
                cursors.removeAt(cursors.lastIndex)
            } else {
                break
            }
        }

        updateList()
    }

    fun onLongClickItem(position: Int) {

    }

    fun onBackUp() {
        if (cursors.count() > 1) {
            cursors.removeAt(cursors.lastIndex)
            updateList()
        } else {
            //exit
            mainFragmentDelegate?.finishActivity()
        }

    }

    fun updateList() {
        mainFragmentDelegate?.showProgressDialog()
        data.clear()
        mainFragmentDelegate?.updateList()
        mainFragmentDelegate?.updateTitle()

        GlobalScope.launch {

            val ret = async {
                //            Thread.sleep(200)
                val path = cursors.lastOrNull()?.currentPath
                if (path!=null) {
                    val rootFolder = File(path)
                    val files = rootFolder.listFiles() ?: return@async
                    for (file: File in files) {
                        val info = LocalFileInfo(file.name, file.path, file.isDirectory)
                        data.add(info)
                    }
                    data.sort()
                }
            }

            ret.await()

            GlobalScope.launch(Dispatchers.Main) {
                mainFragmentDelegate?.dismissProgressDialog()
                mainFragmentDelegate?.updateList()

                mainFragmentDelegate?.scrollTo(
                    cursors.lastOrNull()?.currentOffset ?: 0,
                    cursors.lastOrNull()?.currentPosition ?: 0
                )
            }

        }
    }


//    fun externalSDCardPath(): String? {
//        try {
//            val storageManager = app.getSystemService(Context.STORAGE_SERVICE) as StorageManager?
//            // 7.0才有的方法
//            val storageVolumes = storageManager!!.storageVolumes
//            val volumeClass = Class.forName("android.os.storage.StorageVolume")
//            val getPath: Method = volumeClass.getDeclaredMethod("getPath")
//            val isRemovable: Method = volumeClass.getDeclaredMethod("isRemovable")
//            getPath.setAccessible(true)
//            isRemovable.setAccessible(true)
//            for (i in storageVolumes.indices) {
//                val storageVolume = storageVolumes[i]
//                val mPath = getPath.invoke(storageVolume) as String
//                val isRemove = isRemovable.invoke(storageVolume) as Boolean
//                Log.d("tag2", "mPath is === " + mPath + "isRemoveble == " + isRemove)
//            }
//        } catch (e: Exception) {
//            Log.d("tag2", "e == " + e.message)
//        }
//        return ""
//    }


//    private fun getPhysicalExternalFilePathAboveM(): String? {
//        try {
//            val userEnvironment = Class.forName("android.os.Environment\$UserEnvironment")
//            val getExternalDirs = userEnvironment.getDeclaredMethod("getExternalDirs")
//            getExternalDirs.isAccessible = true
//            //========获取构造UserEnvironment的必要参数UserId================
//            val userHandle = Class.forName("android.os.UserHandle")
//            val myUserId = userHandle.getDeclaredMethod("myUserId")
//            myUserId.isAccessible = true
//            val mUserId = myUserId.invoke(UserHandle::class.java) as Int
//            val declaredConstructor: Constructor<*> = userEnvironment.getDeclaredConstructor(Integer.TYPE)
//            // 得到UserEnvironment instance
//            val instance: Any = declaredConstructor.newInstance(mUserId)
//            val files = getExternalDirs.invoke(instance) as Array<File>
//            for (i in files.indices) {
//                if (Environment.isExternalStorageRemovable(files[i])) {
//                    return files[i].path
//                }
//            }
//        } catch (e: java.lang.Exception) {
//            Log.d("tag2", "e == " + e.message)
//        }
//        return ""
//    }
}
