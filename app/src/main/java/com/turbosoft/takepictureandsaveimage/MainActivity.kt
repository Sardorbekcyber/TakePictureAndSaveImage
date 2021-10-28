package com.turbosoft.takepictureandsaveimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import java.io.File
import java.io.FileDescriptor

const val TAG = "====MobilApp"

class MainActivity : AppCompatActivity() {

    private lateinit var dao: UriDao

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    fromCamera.setImageURI(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Log.d(TAG, "fromGalleryUri: $it")
                val thread  = Thread{
                    dao.insertContentUri(ContentUri(it.toString()))
                }
                thread.start()
                fromGallery.setImageURI(uri)
            }
        }

    private var latestTmpUri: Uri? = null

    private lateinit var fromCamera: ImageView
    private lateinit var fromGallery: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val databaseProvider = DatabaseProvider(this)
        val db = databaseProvider.provideDatabase()
        dao = db.uriDao()
        val thread = Thread{
            Log.d(TAG, "onShowAll: ${dao.getAllUris()}")
            val uris = dao.getAllUris()
            runOnUiThread {
                fromCamera.setImageURI(Uri.parse(uris.first().uri))
                val parcelFileDescriptor: ParcelFileDescriptor =
                    contentResolver.openFileDescriptor(Uri.parse(uris[2].uri), "r")!!
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                fromGallery.setImageBitmap(image)
            }
        }
        thread.start()
        fromCamera = findViewById(R.id.img_1)
        fromGallery = findViewById(R.id.img_2)
        val btn1 = findViewById<Button>(R.id.btn_1)
        val btn2 = findViewById<Button>(R.id.btn_2)
        btn1.setOnClickListener {
            takeImage()
        }
        btn2.setOnClickListener {
            selectImageFromGallery()
        }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                Log.d(TAG, "takeImageUri: $uri")
                val thread = Thread{
                    dao.insertContentUri(ContentUri(uri.toString()))
                }
                thread.start()
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }
}
