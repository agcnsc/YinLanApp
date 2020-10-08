package com.kg.module_file_explorer

import android.content.Intent
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

class OpenFileUtil(val context: Context) {

    fun openFile(filePath: String) {
        val file = File(filePath)
        if (!file.exists())
            return

        val intent: Intent?
        val end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length)
            .toLowerCase(Locale.getDefault())
        if (end == "m4a" || end == "mp3" || end == "mid" || end == "xmf" || end == "ogg" || end == "wav") {
            intent = getAudioFileIntent(filePath)
        } else if (end == "3gp" || end == "mp4") {
            intent = getVideoFileIntent(filePath)
        } else if (end == "jpg" || end == "gif" || end == "png" || end == "jpeg" || end == "bmp") {
            intent = getImageFileIntent(filePath)
        } else if (end == "apk") {
            intent = getApkFileIntent(filePath)
        } else if (end == "ppt") {
            intent = getPptFileIntent(filePath)
        } else if (end == "xls") {
            intent = getExcelFileIntent(filePath)
        } else if (end == "doc") {
            intent = getWordFileIntent(filePath)
        } else if (end == "pdf") {
            intent = getPdfFileIntent(filePath)
        } else if (end == "chm") {
            intent = getChmFileIntent(filePath)
        } else if (end == "txt") {
            intent = getTextFileIntent(filePath, false)
        } else {
            intent = getAllIntent(filePath)
        }

        context.startActivity(intent)
    }

    // Android获取一个用于打开APK文件的intent
    private fun getAllIntent(param: String): Intent {

        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "*/*")
        return intent
    }

    // Android获取一个用于打开APK文件的intent
    private fun getApkFileIntent(param: String): Intent {

        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Intent.ACTION_VIEW
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        return intent
    }

    // Android获取一个用于打开VIDEO文件的intent
    private fun getVideoFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "video/*")
        return intent
    }

    // Android获取一个用于打开AUDIO文件的intent
    private fun getAudioFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("oneshot", 0)
        intent.putExtra("configchange", 0)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "audio/*")
        return intent
    }

    // Android获取一个用于打开Html文件的intent
    private fun getHtmlFileIntent(param: String): Intent {

        val uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
            .encodedPath(param).build()
        val intent = Intent("android.intent.action.VIEW")
        intent.setDataAndType(uri, "text/html")
        return intent
    }

    // Android获取一个用于打开图片文件的intent
    private fun getImageFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "image/*")
        return intent
    }

    // Android获取一个用于打开PPT文件的intent
    private fun getPptFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        return intent
    }

    // Android获取一个用于打开Excel文件的intent
    private fun getExcelFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "application/vnd.ms-excel")
        return intent
    }

    // Android获取一个用于打开Word文件的intent
    private fun getWordFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "application/msword")
        return intent
    }

    // Android获取一个用于打开CHM文件的intent
    private fun getChmFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "application/x-chm")
        return intent
    }

    // Android获取一个用于打开文本文件的intent
    private fun getTextFileIntent(param: String, paramBoolean: Boolean): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (paramBoolean) {
            val uri1 = Uri.parse(param)
            intent.setDataAndType(uri1, "text/plain")
        } else {
            val uri2 = getUri(intent, File(param))
            intent.setDataAndType(uri2, "text/plain")
        }
        return intent
    }

    // Android获取一个用于打开PDF文件的intent
    private fun getPdfFileIntent(param: String): Intent {

        val intent = Intent("android.intent.action.VIEW")
        intent.addCategory("android.intent.category.DEFAULT")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = getUri(intent, File(param))
        intent.setDataAndType(uri, "application/pdf")
        return intent
    }

    private fun getUri(intent: Intent, file: File): Uri {
        val uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //判断版本是否在7.0以上
            uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

}