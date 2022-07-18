package com.hogl.eregister.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream

fun Context.getFolder(): File {
    val directory = File("${this.filesDir}/json")
    if(!directory.exists())
    {
        directory.mkdir()
    }
    return directory
}

fun Context.buildJSON(data: ByteArray)
{
    val directory = this.getFolder()
    val file = File(directory,"data.json")

    var fos: FileOutputStream? = null
    fos = FileOutputStream(file)
    fos.write(data)
    fos!!.close()
}