package com.kg.inline

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment


class PermissionManager(private val context: Context, private val fragment: Fragment) {
    private var callback: ((Boolean) -> Unit)? = null

    companion object {
        const val requestPermissionCode = 1234
    }

    fun request(permission: String, callback: (Boolean) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            callback(true)
        } else {
            this.callback = callback

            fragment.requestPermissions(arrayOf(permission), requestPermissionCode)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray?) {
        if (requestCode == requestPermissionCode) {
            val result = verifyPermissions(grantResults)
            this.callback?.invoke(result)
        }
    }

    private fun verifyPermissions(grantResults: IntArray?): Boolean {
        if (grantResults == null || grantResults.isEmpty()) {
            return false
        }

        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


}