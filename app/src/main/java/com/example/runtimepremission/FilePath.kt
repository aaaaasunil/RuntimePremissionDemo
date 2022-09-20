package com.example.runtimepremission

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


object FilePath {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getPathForSelectedFile(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {

                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                val fileName: String = getFilePath(context, uri).toString()
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/Download/" + fileName
                }

                var id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:".toRegex(), "")
                    val file = File(id)
                    if (file.exists()) return id
                }

                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)


//                val decodedURI = Uri.decode(uri.toString())
//
//                if (decodedURI.contains("raw:")) {
//                    return decodedURI.substring(decodedURI.indexOf("raw:") + 4)
//                }
//
//                val id = DocumentsContract.getDocumentId(Uri.parse(decodedURI))
//
//                val contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)
//
//                return getDataColumn(context, contentUri, null, null)

//                var wholeID = DocumentsContract.getDocumentId(uri);
//                val id: String = wholeID.split(":").get(1)
//                var  contentUri = ContentUris.withAppendedId(
//                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {

                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }else {
                    contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                try {
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }catch (e:Exception){}

            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, null, null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }




    @SuppressLint("NewApi")
    fun getRealPathFromURI_API28(context: Context, uri: Uri): String? {
        uri.getPath()?.let { Log.e("uri", it) }
        var filePath = ""
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val wholeID = DocumentsContract.getDocumentId(uri)
            Log.e("wholeID", wholeID)
            // Split at colon, use second item in the array
            val splits = wholeID.split(":").toTypedArray()
            if (splits.size == 2) {
                val id = splits[1]
                val column = arrayOf(MediaStore.Images.Media.DATA)
                // where id is equal to
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor: Cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf(id), null
                )!!
                val columnIndex: Int = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } else {
            filePath = uri.getPath().toString()
        }
        return filePath
    }

    fun getRealPathFromURI_API29(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    /**
     *  Here creating an image directory Store in cache memeory
     *
     *  @param context
     *  @return
     */
    internal fun getImageTempDirectory(context: Context): File? {
        val storageDir: File =
            File(
                "${context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath}/${
                    ""
                }"
            )

        try {
            // Make sure the directory exists.
            storageDir.mkdirs()
        } catch (e: IOException) {
            // Error occurred while creating the File
            // LogHelper.error(tag = TAG, msg = "getImageTempDirectory: mkdirs error- ${e.message}")
        }

        if (storageDir.isDirectory) {
            val newFile =
                File(storageDir.toString() + File.separator + System.currentTimeMillis() + ".png")
            return newFile
        } else
            return null
    }

    /**
     * Invoke when user take picture from camera
     * Save Image above and equal 29 api level(ANDROID 10)
     */
    internal fun getPhotoFileUriForCamera(context: Context): Uri {

        val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH_mmss", Locale.getDefault()).format(Date())
        val fileName = "PremDemo${timeStamp}.jpg"

        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/RunTimePrem/")
            }

            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
        return uri ?: getUriForBelow29(fileName,context)
//        return uri ?: getImageTempDirectory(context)!!
    }

    /**
     * Save Image below api level 29(ANDROID 9)
     * getExternalStoragePublicDirectory deprecated in Android Q(28)
     */
    internal fun getUriForBelow29(fileName: String, context: Context): Uri {

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val photoFile = File(dir, "/RunTimePrem/$fileName")
        if (photoFile.parentFile?.exists() == false) photoFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )
    }
}