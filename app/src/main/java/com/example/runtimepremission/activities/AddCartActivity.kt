package com.example.runtimepremission.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runtimepremission.FilePath
import com.example.runtimepremission.R
import com.example.runtimepremission.adapter.AddCartAdapter
import com.example.runtimepremission.callBack.RecycleViewItemClick
import com.example.runtimepremission.model.CartItems


class AddCartActivity : AppCompatActivity(), RecycleViewItemClick {

    var cartItems = arrayListOf<CartItems>()

    var startActivityLauncher: ActivityResultLauncher<Intent>? = null
    var takePicturePreviewLauncher: ActivityResultLauncher<Void>? = null

    var takePictureLauncher: ActivityResultLauncher<Uri>? = null
    var openDocumentLauncher: ActivityResultLauncher<Array<String>>? = null
    var addCartAdapter: AddCartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cart)

        cartItems.apply {
            /// OBJECT IS  == this
            //Return value     :    context object
            this.add(CartItems(1, false))
            this.add(CartItems(1, false))
            this.add(CartItems(155, false))
            this.add(CartItems(144, false))
            this.add(CartItems(5, false))
            this.add(CartItems(6, false))
        }

        cartItems.let { it ->

            /// OBJECT IS  == it (it ->)
            ///Return value     :   lambda result
            it.add(CartItems(1, false))
            it.add(CartItems(1, false))
            it.add(CartItems(155, false))
            it.add(CartItems(144, false))
            it.add(CartItems(5, false))
            it.add(CartItems(6, false))
        }
        cartItems.forEach {
            println("Name:${it.quantity}")
        }

/* ------------------------------------------------- */
        for (i in 0..cartItems.size - 1) {
            println(i)
        }
/* ------------------------------------------------- */
        for (i in 0 until cartItems.size) {
            println(i)
        }
/* ------------------------------------------------- */
        for (i in 0 until cartItems.size step 2) {
            println(i)
        }
/* ------------------------------------------------- */
        // For loop
        for ((index, i) in cartItems.withIndex()) {

            Log.d("For each withIndex", "$index is ${i.quantity}")

        }
/* ------------------------------------------------- */
        for (i in cartItems.indices) {

            Log.d("For each", "$i ")

        }
        /* ------------------------------------------------- */
        for (i in cartItems) {

            Log.d("For each", "${i.quantity} ")

        }
/* ------------------------------------------------- */
        // ForEach
        (0..10).forEach {
            println(it)

        }

        addCartAdapter = AddCartAdapter(this, cartItems, this)
        val recyclerview = findViewById<RecyclerView>(R.id.rvView)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = addCartAdapter

        val viewTreeObserver = findViewById<View>(R.id.rvView).viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
//                viewTreeObserver.removeOnGlobalLayoutListener(this)
                var bbbitMap = recyclerview.getDrawingCache()
                var bitMap = loadBitmapFromView(findViewById<TextView>(R.id.rvView))
                var bitMapButton = loadBitmapFromView(findViewById<TextView>(R.id.tvButton))
                var combineBitMap = overlay(bitMap!!, bitMapButton!!)


            }
        })
        var bitMap = recyclerview.getDrawingCache()
//        var bitMap = loadBitmapFromView( findViewById<TextView>(R.id.tvButton))
        onActivityResultMethods()

        val list = listOf("geeks", "for", "geeks", "hello", "world")

        //filtering all words with length > 4
        val longerThan4 = list.filter { it.contains("g") }
        Log.d("Intent PicturePreview", longerThan4.toString())

    }

    private fun onActivityResultMethods() {

        startActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                }
            }

        takePicturePreviewLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {

                Log.d("Intent PicturePreview", it.toString())

            }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.d("Intent Picture", it.toString())

        }

        openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {

            Log.d("Intent Document", it.toString())

        }

//        findViewById<TextView>(R.id.tvButton).setOnClickListener {
//
//            openDocumentLauncher(openDocumentLauncher = openDocumentLauncher!!)
//            //takePictureLauncher(takePictureLauncher = takePictureLauncher!!)
//            //takePicturePreviewLauncher(takePicturePreviewLauncher = takePicturePreviewLauncher!!)
//
//        }

    }

    private fun openSomeActivityForResult(startActivityLauncher: ActivityResultLauncher<Intent>) {

        val intent = Intent(this, MainActivity::class.java)
        startActivityLauncher.launch(intent)

    }

    private fun takePictureLauncher(takePictureLauncher: ActivityResultLauncher<Uri>) {

        val cameraPhotoUri = FilePath.getPhotoFileUriForCamera(this)
        takePictureLauncher.launch(cameraPhotoUri)

    }

    private fun openDocumentLauncher(openDocumentLauncher: ActivityResultLauncher<Array<String>>) {

        openDocumentLauncher.launch(
            arrayOf(
                "application/pdf",
                "application/msword",
                "application/ms-doc",
                "application/doc",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain"
            )
        )

    }

    fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val mCanvas = Canvas(b)
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom())
        v.draw(mCanvas)
        return b
    }

    fun overlay(bmp1: Bitmap, bmp2: Bitmap): Bitmap? {
        val bmOverlay = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(bmp2, Matrix(), null)
        canvas.drawBitmap(bmp1, 0f, bmp2.height.toFloat(), null)
        bmp1.recycle()
        bmp2.recycle()
        return bmOverlay
    }

    override fun onRecycleViewClickListener(model: Any, position: Int) {

        cartItems.set(position, model as CartItems)
        addCartAdapter!!.notifyItemChanged(position)
    }


//    private fun takePicturePreviewLauncher(takePicturePreviewLauncher:ActivityResultLauncher<Void>) {
//
//        takePicturePreviewLauncher.launch()
//
//    }

    //The BroadcastReceiver that listens for bluetooth broadcasts

    val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            TODO("Not yet implemented")
        }


    }

    var clickListener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

    }

}
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                ... //Device found
//            }
//            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                ... //Device is now connected
//            }
//            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                ... //Done searching
//            }
//            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
//                ... //Device is about to disconnect
//            }
//            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//                ... //Device has disconnected
//            }
//        }
//
//}