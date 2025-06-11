package com.desirecodes.imagepicker.sample

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.desirecodes.imagepicker.ImagePicker
import com.desirecodes.imagepicker.ImagePicker.Companion.getError
import com.desirecodes.imagepicker.ImagePicker.Companion.with
import com.desirecodes.imagepicker.constant.ImageProvider
import com.desirecodes.imagepicker.listener.DismissListener
import com.desirecodes.imagepicker.sample.ImageViewerDialog.Companion.newInstance
import com.desirecodes.imagepicker.sample.util.FileUtil.getFileInfo
import com.desirecodes.imagepicker.sample.util.IntentUtil.openURL
import com.desirecodes.imagepicker.util.IntentUtils.getUriViewIntent
import java.io.File

class SampleActivity : AppCompatActivity() {
    private var mCameraUri: Uri? = null
    private var mGalleryUri: Uri? = null
    private var mProfileUri: Uri? = null

    private var imgProfileInfo: ImageView? = null
    private var imgCameraInfo: ImageView? = null
    private var imgGalleryInfo: ImageView? = null

    private var imgProfile: ImageView? = null
    private var imgGallery: ImageView? = null
    private var imgCamera: ImageView? = null

    private var imgProfileCode: ImageView? = null
    private var imgGalleryCode: ImageView? = null
    private var imgCameraCode: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        imgProfileInfo = findViewById(R.id.imgProfileInfo)
        imgCameraInfo = findViewById(R.id.imgCameraInfo)
        imgGalleryInfo = findViewById(R.id.imgGalleryInfo)

        imgProfile = findViewById(R.id.imgProfile)
        imgCamera = findViewById(R.id.imgCamera)
        imgGallery = findViewById(R.id.imgGallery)

        imgProfileCode = findViewById(R.id.imgProfileCode)
        imgCameraCode = findViewById(R.id.imgCameraCode)
        imgGalleryCode = findViewById(R.id.imgGalleryCode)

        imgProfile?.setDrawableImage(R.drawable.ic_person, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_github) {
            openURL(this, GITHUB_REPOSITORY)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun pickProfileImage(view: View?) {
        with(this) // Crop Square image
            .cropSquare()
            .setImageProviderInterceptor { imageProvider: ImageProvider ->
                Log.d(
                    "ImagePicker",
                    "Selected ImageProvider: $imageProvider"
                )
                null
            }.setDismissListener(object : DismissListener {
                override fun onDismiss() {
                    Log.d("ImagePicker", "Dialog Dismiss")
                }
            }) // Image resolution will be less than 512 x 512
            .maxResultSize(200, 200)
            .start(PROFILE_IMAGE_REQ_CODE)
    }

    fun pickGalleryImage(view: View?) {
        with(this) // Crop Image(User can choose Aspect Ratio)
            .crop() // User can only select image from Gallery
            .galleryOnly()

            .galleryMimeTypes(
                arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            ) // Image resolution will be less than 1080 x 1920
            .maxResultSize(1080, 1920) // .saveDir(getExternalFilesDir(null))
            .start()
    }

    /**
     * Ref: https://gist.github.com/granoeste/5574148
     */
    fun pickCameraImage(view: View?) {
        with(this) // User can only capture image from Camera
            .cameraOnly() // Image size will be less than 1024 KB
            // .compress(1024)
            //  Path: /storage/sdcard0/Android/data/package/files
            .saveDir(getExternalFilesDir(null)!!) //  Path: /storage/sdcard0/Android/data/package/files/DCIM
            .saveDir(getExternalFilesDir(Environment.DIRECTORY_DCIM)!!) //  Path: /storage/sdcard0/Android/data/package/files/Download
            .saveDir(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!) //  Path: /storage/sdcard0/Android/data/package/files/Pictures
            .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!) //  Path: /storage/sdcard0/Android/data/package/files/Pictures/ImagePicker
            .saveDir(
                File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "ImagePicker"
                )
            ) //  Path: /storage/sdcard0/Android/data/package/files/ImagePicker
            .saveDir(getExternalFilesDir("ImagePicker")!!) //  Path: /storage/sdcard0/Android/data/package/cache/ImagePicker
            .saveDir(
                File(
                    externalCacheDir,
                    "ImagePicker"
                )
            ) //  Path: /data/data/package/cache/ImagePicker
            .saveDir(File(cacheDir, "ImagePicker")) //  Path: /data/data/package/files/ImagePicker
            .saveDir(
                File(
                    filesDir,
                    "ImagePicker"
                )
            ) // Below saveDir path will not work, So do not use it
            //  Path: /storage/sdcard0/DCIM
            //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
            //  Path: /storage/sdcard0/Pictures
            //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
            //  Path: /storage/sdcard0/ImagePicker
            //  .saveDir(File(Environment.getExternalStorageDirectory(), "ImagePicker"))

            .start(CAMERA_IMAGE_REQ_CODE)
    }

    fun showImageCode(view: View) {
        var resource = 0
        if (view === imgProfileCode) {
            resource = R.drawable.img_profile_code
        } else if (view === imgCameraCode) {
            resource = R.drawable.img_camera_code
        } else if (view === imgGalleryCode) {
            resource = R.drawable.img_gallery_code
        }
        newInstance(resource)
            .show(supportFragmentManager, "")
    }

    fun showImage(view: View) {
        val uri = if (view === imgProfile) {
            mProfileUri
        } else if (view === imgCamera) {
            mCameraUri
        } else if (view === imgGallery) {
            mGalleryUri
        } else {
            null
        }

        if (uri != null) {
            startActivity(getUriViewIntent(this, uri))
        }
    }

    fun showImageInfo(view: View) {
        val uri = if (view === imgProfileInfo) {
            mProfileUri
        } else if (view === imgCameraInfo) {
            mCameraUri
        } else if (view === imgGalleryInfo) {
            mGalleryUri
        } else {
            null
        }

        AlertDialog.Builder(this)
            .setTitle("Image Info")
            .setMessage(getFileInfo(this, uri))
            .setPositiveButton("Ok", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            // Uri object will not be null for RESULT_OK
            val uri = data!!.data

            when (requestCode) {
                PROFILE_IMAGE_REQ_CODE -> {
                    mProfileUri = uri
                    imgProfile!!.setLocalImage(uri!!, true)
                }

                GALLERY_IMAGE_REQ_CODE -> {
                    mGalleryUri = uri
                    imgGallery!!.setLocalImage(uri!!, false)
                }

                CAMERA_IMAGE_REQ_CODE -> {
                    mCameraUri = uri
                    imgCamera!!.setLocalImage(uri!!, false)
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val GITHUB_REPOSITORY = "https://github.com/Dhaval2404/ImagePicker"

        private const val PROFILE_IMAGE_REQ_CODE = 101
        private const val GALLERY_IMAGE_REQ_CODE = 102
        private const val CAMERA_IMAGE_REQ_CODE = 103
    }
}