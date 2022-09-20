package com.example.runtimepremission.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.runtimepremission.BuildConfig
import com.example.runtimepremission.Constants.Constants
import com.example.runtimepremission.FilePath.getImageTempDirectory
import com.example.runtimepremission.FilePath.getPhotoFileUriForCamera
import com.example.runtimepremission.R
import com.example.runtimepremission.callBack.PremissionCallBack
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException


class MainActivity : BaseActivity(), PremissionCallBack {

    val permissionsArrayFor11 = arrayOf(
        Manifest.permission.CAMERA,
    )
    val permissionsArrayBelow11 = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    var cameraPhotoUri : Uri?=null
    private val RC_SIGN_IN = 1

    private var mGoogleSignInClient: GoogleSignInClient? = null
var price =" "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        googleOptions()
        init()
        initView()
        callApi()

        price = price.concatString("$","20")
    }

    fun String.concatString(value1 : String,value2 : String): String = value1.plus(value2)

    private fun googleOptions() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("669393541522-3lernrl39jagjmtteqg30gddretsmrq7.apps.googleusercontent.com")
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
    }

    override fun initView() {
        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val intent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun callApi() {
        super.callApi()
    }

    private fun init() {

        if (allPermissionsGranted(premArray = permissionsArrayFor11))
            setUpUI()
         else {
            if (checkIsAndroid11())
                requestPermissions(prem = permissionsArrayFor11, this)

            else
                requestPermissions(prem = permissionsArrayBelow11, this)
        }
    }

    private fun setUpUI() {

        Toast.makeText(this, "onPremissionAllow", Toast.LENGTH_SHORT).show()

//        capturePhoto()
        selectMediaAndFile()
    }

    private fun selectMediaAndFile() {

        findViewById<Button>(R.id.btnSelectDocumnet).setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.type = "*/*"
            val mimeTypes = arrayOf("application/pdf", "application/pdf")

            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, FILE_REQUEST_CODE)

        }
        findViewById<Button>(R.id.btnSelectImageVideo).setOnClickListener {

//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            val type = "image/* video/*"
//            intent.setType(type)
//            startActivityForResult(intent, FILE_REQUEST_CODE)


            val intent = Intent(Intent.ACTION_SEND)
            val shareBody = "Here is the share content body"
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Hello")
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(intent, "Share"))
        }

        findViewById<Button>(R.id.btnOpenCamera).setOnClickListener {

            /* Save pic in CacheDir storage */
//           val file = File(
//               this.externalCacheDir!!.absolutePath,
//               "/" + System.currentTimeMillis() + ".png"
//           )
//
//           val photoURI = FileProvider.getUriForFile(
//               this,
//               BuildConfig.APPLICATION_ID+".provider",
//               file
//           )

            /* Save pic in private storage */
            val photoURI = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                getImageTempDirectory(this)!!
//                commonDocumentDirPath("RunTimePremission")!!
            )
            cameraPhotoUri = getPhotoFileUriForCamera(this)
            val picIntent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(picIntent, CAMERA_REQUEST_CODE)

        }
    }

    override fun onPremissionAllow() {

        setUpUI()

    }

    override fun onPremissionDeny(eventKey: String) {

        if (eventKey.equals(Constants.PREMISSION.GIVE_REASON_TO_USER))
            whyPremissionIsReqired(msg = "This is required for access camera")
        else showDialogWhenNeverAskPress()

    }

    override fun onPremissionNeverAsk() {

    }

    override fun onPremissionRequestAgain() {

        requestPermissions(prem = permissionsArrayFor11, this)

    }


//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val task =
//                GoogleSignIn.getSignedInAccountFromIntent(data)
//
//            try {
//                val account : GoogleSignInAccount? = task.getResult(ApiException::class.java)
//
////                val intent = Intent(this@SignInActivity, UserProfile::class.java)
////                startActivity(intent)
//
//            } catch (e: ApiException) {
//                // The ApiException status code indicates the detailed failure reason.
//                // Please refer to the GoogleSignInStatusCodes class reference for more information.
//                Log.e("TAG","signInResult:failed code=" + e.statusCode)
//            }
//        }
//    }

   }