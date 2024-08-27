package org.diary.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.diary.common.MyApplication


class MyPermissions {
    private val REQUEST_WRITE_PERMISSION = 1
    private val REQUEST_PHONE_PERMISSION = 2
    private val REQUEST_OVERLAY_PERMISSION = 4000
    private val myApplication: MyApplication? = null
    private var phonePermission = false
    private var readWritePermission = false
    private val overlayPermission = false
    var context = MyApplication.instance?.applicationContext
    

    /**
     * Check read/write permissions
     */
    fun checkReadWritePermissions():Boolean {
        MyLogger.d("MainActivity - checkReadWritePermissions=$readWritePermission")
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            readWritePermission = true
            return true
        } else {
            ActivityCompat.requestPermissions(
                MyApplication.instance?.activity!!,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_WRITE_PERMISSION
            )
            readWritePermission = false
        }
        return false
    }

    /**
     * On request permission result
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>?, grantResults: IntArray
    ) {
        MyLogger.d("MainActivity - onRequestPermissionsResult - requestCode=$requestCode")
        when (requestCode) {
            REQUEST_WRITE_PERMISSION -> {

                // If request is cancelled, the result arrays are empty.
                if ((grantResults.size > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // permission was granted
                    MyLogger.d("MainActivity - onRequestPermissionsResult - GRANTED")
                    readWritePermission = true
                } else {
                    // permission denied
                    MyLogger.d("MainActivity - onRequestPermissionsResult - DENIED")
                    readWritePermission = false
                }
                return
            }
            REQUEST_PHONE_PERMISSION -> {

                // If request is cancelled, the result arrays are empty.
                if ((grantResults.size > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // permission was granted
                    MyLogger.d("MainActivity - onRequestPermissionsResult - GRANTED")
                    phonePermission = true
                } else {
                    // permission denied
                    MyLogger.d("MainActivity - onRequestPermissionsResult - DENIED")
                    phonePermission = false
                }
                return
            }
        }
    }

}