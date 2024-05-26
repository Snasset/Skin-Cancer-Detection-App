package com.dicoding.asclepius.view

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_IMAGE_URI
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_RESULT_DESC
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_RESULT_LABEL
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_RESULT_SCORE
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private lateinit var topLabel: String
    private lateinit var highestScore: String
    private lateinit var resultDesc: String

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        with(binding) {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            analyzeButton.setOnClickListener {
                currentImageUri?.let { imageUri ->
                    analyzeImage(imageUri)
                } ?: run {
                    showToast(getString(R.string.empty_image))
                }
            }
            savemarkButton.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, SavemarkFragment())
                    .addToBackStack(null)
                    .commit()
            }

            newsButton.setOnClickListener {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, NewsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            currentImageUri = UCrop.getOutput(data!!)
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast(cropError.toString())
        }
    }

    private fun openCropActivity(inputUri: Uri?) {
        val timestamp = Date().time
        val outputUri = File(cacheDir, "croppedImage_${timestamp}.jpg").toUri()
        UCrop.of(inputUri!!, outputUri)
            .withMaxResultSize(800, 800)
            .withAspectRatio(5f, 5f)
            .start(this)
    }



    fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            imageFile
        )
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            openCropActivity(currentImageUri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            openCropActivity(currentImageUri)
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(imageUri: Uri) {
        binding.progressIndicator.visibility = View.VISIBLE
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        showToast(error)
                    }
                }

                override fun onResult(result: List<Classifications>?) {
                    runOnUiThread {
                        result?.let { classificationList ->
                            if (classificationList.isNotEmpty() && classificationList[0].categories.isNotEmpty()) {
                                println(classificationList)
                                val sortedCategories =
                                    classificationList[0].categories.sortedByDescending { it?.score }
                                topLabel = sortedCategories[0].label
                                highestScore = NumberFormat.getPercentInstance()
                                    .format(sortedCategories[0].score).trim()
                                Log.d(
                                    "DESCDESCKANKERLABEL",
                                    "onResult: ${sortedCategories[0].label}"
                                )
                                if (topLabel == "Cancer") {
                                    resultDesc =
                                        "Dari hasil analisis dari gambar bahwa anda memiliki kanker dengan tingkat kepercayaan $highestScore. " +
                                                "\nDisarankan untuk mengunjungi halaman berita tentang kanker pada aplikasi ini"
                                } else {
                                    resultDesc =
                                        "Dari hasil analisis dari gambar bahwa anda tidak memiliki kanker dengan tingkat kepercayaan $highestScore." +
                                                "\nAnda dapat melihat berita kanker secara lanjut dengan mengunjungi halaman berita tentang kanker pada aplikasi ini"
                                }

                            } else {
                                topLabel = ""
                                highestScore = ""
                                resultDesc = "gagal"
                            }
                        }
                    }
                }
            }
        )
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                imageClassifierHelper.classifyStaticImage(imageUri)
            }
            binding.progressIndicator.visibility = View.GONE
            moveToResult()
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(topLabel, EXTRA_RESULT_LABEL)
        intent.putExtra(currentImageUri.toString(), EXTRA_IMAGE_URI)
        intent.putExtra(highestScore, EXTRA_RESULT_SCORE)
        intent.putExtra(resultDesc, EXTRA_RESULT_DESC)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainAct"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}