package com.example.runtimepremission.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.runtimepremission.Constants.Constants
import com.example.runtimepremission.FilePath
import com.example.runtimepremission.FilePath.getPhotoFileUriForCamera
import com.example.runtimepremission.FilePath.getRealPathFromURI_API28
import com.example.runtimepremission.FilePath.getRealPathFromURI_API29
import com.example.runtimepremission.R
import com.example.runtimepremission.callBack.PremissionCallBack
import java.io.File
import java.io.IOException
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    private val RC_STORAGE_PREMISSION = 11000
    val FILE_REQUEST_CODE = 1000
    val CAMERA_REQUEST_CODE = 1001
    var premissionCallBack: PremissionCallBack? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        fun String.removeFirstLast(): String =  this.substring(1, this.length - 1)

        fun String.replaceStringWithBlank(value : String): String = this.replace(value,"")

        fun String.concatString(value1 : String,value2 : String): String = value1+value2

    }


    /**
     * Here check premission granted or not
     * @param premArray
     * @return boolean true or false
     * */
    internal fun allPermissionsGranted(premArray: Array<String>): Boolean {

        var isGranted = false

        for (permission in premArray) {
            isGranted =
                ContextCompat.checkSelfPermission(
                    this@BaseActivity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
        }

        return isGranted
    }

    abstract fun initView()

    protected open fun callApi(){

    }


    /**
     * Here request dangerous premission to user
     */
    internal fun requestPermissions(prem: Array<String>, permissionsCallBack: PremissionCallBack) {

        this.premissionCallBack = permissionsCallBack
        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

              try {
                  val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                  intent.addCategory("android.intent.category.DEFAULT")
                  intent.data =
                      Uri.parse(String.format("package:%s", applicationContext.packageName))
                  startActivityForResult(
                      intent,
                      RC_STORAGE_PREMISSION
                  )
              } catch (e: Exception) {
                  val intent = Intent()
                  intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                  startActivityForResult(
                      intent,
                      RC_STORAGE_PREMISSION
                  )
              }
          }else{*/
        ActivityCompat.requestPermissions(this, prem, RC_STORAGE_PREMISSION)

        //}

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_STORAGE_PREMISSION) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                premissionCallBack!!.onPremissionDeny(Constants.PREMISSION.GIVE_REASON_TO_USER)

            } else {

                if (checkIsAndroid11()) {

                    if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED)
                        premissionCallBack!!.onPremissionAllow()

                } else if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED)

                    premissionCallBack!!.onPremissionAllow()

                else premissionCallBack!!.onPremissionDeny(Constants.PREMISSION.GO_TO_SETTINGS)

            }
        }

    }

    /**
     * Alert dialog show when user click on never ask again button
     * */
    internal fun showDialogWhenNeverAskPress() {

        val mAlertDialog = AlertDialog.Builder(this@BaseActivity)
        mAlertDialog.setIcon(R.mipmap.ic_launcher_round) //set alertdialog icon
        mAlertDialog.setTitle(getString(R.string.app_name)) //set alertdialog title
        mAlertDialog.setMessage("You denied camera permission. To enable it please go in  permission section under app settings") //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + this.packageName)
                )
            )
            finish()

        }
        mAlertDialog.setCancelable(false)
        mAlertDialog.show()
    }

    /**
     * Alert dialog show when user click on never ask again button
     * */
    internal fun whyPremissionIsReqired(msg: String) {

        val mAlertDialog = AlertDialog.Builder(this@BaseActivity)
        mAlertDialog.setIcon(R.mipmap.ic_launcher_round) //set alertdialog icon
        mAlertDialog.setTitle(getString(R.string.app_name)) //set alertdialog title
        mAlertDialog.setMessage(msg) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

            premissionCallBack!!.onPremissionRequestAgain()
        }
        mAlertDialog.setCancelable(false)
        mAlertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_STORAGE_PREMISSION) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.CAMERA
                        ),
                        RC_STORAGE_PREMISSION
                    )
                } else
                    whyPremissionIsReqired(msg = "This is required for access your files")
            }
        } else {
            if (resultCode == Activity.RESULT_OK && requestCode == FILE_REQUEST_CODE && data != null) {

                val selectedFile = data.data
                val filePathFromUri = data.data?.let { FilePath.getPathForSelectedFile(this, it) }
                val file = File(filePathFromUri)
                val absolutePath = file.absolutePath

                Log.d(">>> selectedFile ", selectedFile.toString())
                Log.d(">>> absolutePath ", absolutePath.toString())

            } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
                val file = FilePath.getImageTempDirectory(this)!!

                var myBitmap = BitmapFactory.decodeFile(file.absolutePath);
//              if (checkIsAndroidEqualAndAbove10())
//
//                Log.d(">>> absolutePath ",getRealPathFromURI_API29(this,(this as MainActivity).cameraPhotoUri).toString())
//              else
//                 getRealPathFromURI_API28(this, getPhotoFileUriForCamera(this))?.let { Log.d(">>> absolutePath ", it) }
//                // uriToFilename(getPhotoFileUri(this))?.let { Log.d(">>> absolutePath ", it) }
//
//            }
            }
        }
    }

    /**
     * Here check Api level
     */
    internal fun checkIsAndroid11(): Boolean {

        return Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R
    }

    /**
     * Here check Api Level 29(ANDROID 10)
     */
    internal fun checkIsAndroidEqualAndAbove10(): Boolean {

        return Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    }

    /**
     *  Here creating an image directory Store in cache memeory
     *
     *  @param context
     *  @return
     */
//    internal fun getImageTempDirectory(context: Context): File? {
//        val storageDir: File =
//            File(
//                "${getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath}/${
//                    ""
//                }"
//            )
//
//        try {
//            // Make sure the directory exists.
//            storageDir.mkdirs()
//        } catch (e: IOException) {
//            // Error occurred while creating the File
//            // LogHelper.error(tag = TAG, msg = "getImageTempDirectory: mkdirs error- ${e.message}")
//        }
//
//        if (storageDir.isDirectory) {
//            val newFile =
//                File(storageDir.toString() + File.separator + System.currentTimeMillis() + ".png")
//            return newFile
//        } else
//            return null
//    }

    /**
     * Save Image above and equal 29 api level(ANDROID 10)
     */
//    internal fun getPhotoFileUriFromCamera(context: Context): Uri {
//
//        val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH_mmss", Locale.getDefault()).format(Date())
//        val fileName = "PremDemo${timeStamp}.jpg"
//
//        var uri: Uri? = null
//        if (checkIsAndroidEqualAndAbove10()) {
//            val resolver = context.contentResolver
//            val contentValues = ContentValues().apply {
//                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/RunTimePrem/")
//            }
//
//            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//        }
//        return uri ?: getUriForPreQ(fileName,context)
////        return uri ?: getImageTempDirectory(context)!!
//    }

    /**
     * Save Image below api level 29(ANDROID 9)
     * getExternalStoragePublicDirectory deprecated in Android Q(28)
     */
//    internal fun getUriForPreQ(fileName: String,context: Context): Uri {
//
//        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//        val photoFile = File(dir, "/RunTimePrem/$fileName")
//        if (photoFile.parentFile?.exists() == false) photoFile.parentFile?.mkdir()
//        return FileProvider.getUriForFile(
//            context,
//            BuildConfig.APPLICATION_ID + ".provider",
//            photoFile
//        )
//    }



//    fun uriToFilename(uri: Uri): String? {
//        var path: String? = null
//        path = if (Build.VERSION.SDK_INT < 11) {
//            getRealPathFromURI_BelowAPI11(this, uri)
//        } else if (Build.VERSION.SDK_INT < 19) {
//            getRealPathFromURI_API11to18(this, uri)
//        } else {
//            getRealPathFromURI_API28(this, uri)
//        }
//        return path
//    }

    internal fun saveBitmap(
        context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat,
        mimeType: String, displayName: String
    ): Uri {

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val resolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use {
                if (!bitmap.compress(format, 95, it))
                    throw IOException("Failed to save bitmap.")
            } ?: throw IOException("Failed to open output stream.")

            return uri

        } catch (e: IOException) {

            uri?.let { orphanUri ->
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(orphanUri, null, null)
            }

            throw e
        }
    }
}