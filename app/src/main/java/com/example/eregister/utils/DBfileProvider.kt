package com.example.eregister.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class DBFileProvider : FileProvider() {

    fun getDatabaseURI(c: Context, dbName: String?): Uri? {
        val file: File = c.getDatabasePath(dbName)
        return getFileUri(c, file)
    }

    private fun getFileUri(context: Context, file: File): Uri? {
        return getUriForFile(context, "com.android.example.provider", file)
    }

}