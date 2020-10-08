package com.kg.inline.ui.fileextract

import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileExtractViewModel : ViewModel() {

    fun extract(target: String, save: String) {
        val file = File(target)
        file.listFiles()?.forEach { sub ->
            if (sub.isDirectory) {
                extract(sub.absolutePath, save)
            } else {
                val name = sub.name
                val dst = File("$save/$name")
                println("src: ${sub.absolutePath}")
                println("dsr: ${dst.absolutePath}")

                val ret = sub.renameTo(dst)
                println("ret: $ret")
            }
        }
    }

    fun moveFile(filePath: String?, newDirPath: String?) {
        if (filePath.isNullOrEmpty() || newDirPath.isNullOrEmpty()) {
            return
        }

        try {
            copyFile(filePath, newDirPath)
            removeFile(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun copyFile(filePath: String?, newDirPath: String?) {
        if (filePath.isNullOrEmpty() || newDirPath.isNullOrEmpty()) {
            return
        }
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return
            }

            val newDir = File(newDirPath)
            if (!newDir.exists()) {
                newDir.mkdirs()
            }
            val newFile: File? = newFile(newDirPath, file.name)
            val ins = FileInputStream(file)
            val fos = FileOutputStream(newFile)
            val buffer = ByteArray(4096)
            var byteCount = 0
            while (ins.read(buffer).also { byteCount = it } != -1) {
                fos.write(buffer, 0, byteCount)
            }
            fos.flush()
            ins.close()
            fos.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun removeFile(filePath: String?) {
        if (filePath == null || filePath.isEmpty()) {
            return
        }
        try {
            val file = File(filePath)
            if (file.exists()) {
                removeFile(file)
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun removeFile(file: File) {
        if (file.isFile) {
            file.delete()
            return
        }

        if (file.isDirectory) {
            val childFile = file.listFiles()
            if (childFile == null || childFile.isEmpty()) {
                file.delete()
                return
            }
            for (f in childFile) {
                removeFile(f)
            }
            file.delete()
        }
    }

    private fun newFile(filePath: String?, fileName: String?): File? {
        if (filePath == null || filePath.isEmpty() || fileName == null || fileName.isEmpty()) {
            return null
        }
        try {
            val dir = File(filePath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val sbFile = StringBuilder(filePath)
            if (!filePath.endsWith("/")) {
                sbFile.append("/")
            }
            sbFile.append(fileName)

            val file = File(sbFile.toString())
            if (!file.exists()) {
                file.createNewFile()
            }
            return file
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return null
    }
}