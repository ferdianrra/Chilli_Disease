package com.dicoding.deteksicabai.view

import Chi.R
import Chi.databinding.ActivityUploadBinding
import ImageClassifierHelper
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.dicoding.deteksicabai.db.DatabaseContract
import com.dicoding.deteksicabai.db.HistoryHelper
import com.dicoding.deteksicabai.entity.history
import com.dicoding.deteksicabai.getImageUri
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var imageUri: Uri? = null
    private var myResult: String = ""
    private var imageSize: Int = 256
    private var historyDisease: history? = null
    private lateinit var historyHelper: HistoryHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        historyHelper = HistoryHelper.getInstance(applicationContext)
        historyHelper.open()

        binding.apply {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { openGallery() }
            btnHistory.setOnClickListener { startActivity(Intent(this@UploadActivity, HistoryActivity::class.java)) }
            btnUpload.setOnClickListener {
                if (imageUri != null) {
                    val inputStream = contentResolver.openInputStream(imageUri!!)

                    // 2. Konversi gambar dari URI ke Bitmap
                    val imageBitmap = BitmapFactory.decodeStream(inputStream)

                    // 3. Lakukan penskalaan jika diperlukan
                    val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false)

                    // 4. Gunakan Bitmap yang dihasilkan untuk proses klasifikasi
                    classifyImage(scaledBitmap)
                } else {
                    showToast("Mohon untuk memasukkan gambar terlebih dahulu")
                }
            }
        }
    }


    private fun moveToDiseaseDetect(detectDisease: String, diseaseDesc: String, diseasePrevention: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_URI, imageUri.toString())
        intent.putExtra(EXTRA_RESULT, detectDisease)
        intent.putExtra(EXTRA_DESC, diseaseDesc)
        intent.putExtra(EXTRA_PREVENTION, diseasePrevention)
        AddHistory(detectDisease, diseaseDesc, imageUri.toString().toUri())
        startActivity(intent)
    }

    private fun classifyImage(image: Bitmap?) {
        try {
            val model = Chi.ml.Model.newInstance(applicationContext)

            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image?.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++]
                    byteBuffer.putFloat(((`val` shr 16) and 0xFF) * (1f / 1))
                    byteBuffer.putFloat(((`val` shr 8) and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 1))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences = outputFeature0.floatArray
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val disease = resources.getStringArray(R.array.chilli_disease)[maxPos]
            val diseaseDesc = resources.getStringArray(R.array.disease_desc)[maxPos]
            val diseasePrevetion = resources.getStringArray(R.array.disease_prevention)[maxPos]
            moveToDiseaseDetect(disease, diseaseDesc, diseasePrevetion)
            Log.e("Upload Activity", myResult)

            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImage.setImageURI(it)
        }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        val contentResolver = contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)
            cursor.close()
            filePath
        } else {
            null
        }
    }

    private fun startCamera() {
        imageUri = getImageUri(this)
        launcherCamera.launch(imageUri!!)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Date())
        return dateFormat
    }

    private fun AddHistory(title: String, desc: String, photoUri: Uri) {
        historyDisease?.date = getCurrentDate()
        historyDisease?.disease = title
        historyDisease?.descDisease = desc

        val values = ContentValues()
        values.put(DatabaseContract.HistoryColumns.DISEASE, title)
        values.put(DatabaseContract.HistoryColumns.DESCRIPTION, desc)

        // Get real file path from Uri
        val filePath = getRealPathFromUri(photoUri)
        if (filePath != null) {
            values.put(DatabaseContract.HistoryColumns.PHOTO, filePath)
            historyDisease?.photoLeaf = filePath  // Update photoLeaf in history object
        } else {
            Log.e("UploadActivity", "Failed to get real path from Uri")
            // Handle the case where the file path cannot be obtained (e.g., show error message)
            return
        }

        values.put(DatabaseContract.HistoryColumns.DATE, getCurrentDate())
        val result = historyHelper.insert(values)
        if (result > 0) {
            historyDisease?.id = result.toInt()
            setResult(RESULT_ADD, intent)
            finish()
        } else {
            Toast.makeText(
                this@UploadActivity,
                "Gagal menambah data",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_URI = "extra_uri"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_PREVENTION = "extra_prevention"
        const val RESULT_ADD = 101
    }
}
