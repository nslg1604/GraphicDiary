package org.diary.utils

import android.content.Context
import java.io.*
import java.nio.channels.FileChannel


object MyFileUtils {
    @Throws(IOException::class)
    fun readTextFromAssets(context: Context, filename: String): String? {
        val reader = BufferedReader(
            InputStreamReader(context.getAssets().open(filename))
        )

        // do reading, usually loop until end of file reading
        val sb = StringBuilder()
        var mLine = reader.readLine()
        while (mLine != null) {
            sb.append(mLine) // process line
            mLine = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

    fun createDirectory(directory: String?) {
        MyLogger.d("MyFileUtils - createDirectory dir=" + directory)
        val file = File(directory)
        MyLogger.d("MyFileUtils - createDirectory file=" + file)
        if (!file.exists()) {
            file.mkdirs()
        }
        MyLogger.d("MyFileUtils - createDirectory created=" + isFileExist(directory!!))

    }

    fun isFileExist(path: String): Boolean {
        val file = File(path)
        return file.exists()
    }

    fun copyFile(src: String, dst: String): Boolean {
        MyLogger.d("MyFileUtils - copyFile src=$src  dst=$dst")
        var result = true
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            val sourceFile = File(src)
            val destFile = File(dst)
            if (destFile.exists()) {
                destFile.delete()
            }
            if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
            if (!destFile.exists()) {
                destFile.createNewFile()
            }
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } catch (e: IOException) {
            result = false
        }
        try {
            source?.close()
            destination?.close()
        } catch (e: IOException) {
        }
        return result
    }


    fun remove(path: String){
        var file = File(path)
        file.delete()
        MyLogger.d("MyFileUtils - remove path=" + path + " exists=" + file.exists())
    }


}
