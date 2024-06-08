package com.dicoding.deteksicabai.view

import Chi.databinding.ActivityDetailBinding
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.getStringExtra(EXTRA_URI)?.toUri()
        uri?.let { showImage(it) }

        val myResult = intent.getStringExtra(EXTRA_RESULT)
        val myResultDesc = intent.getStringExtra(EXTRA_DESC)
        val myResultPrevention = intent.getStringExtra(EXTRA_PREVENTION)

        binding.jenisPenyakit.text = myResult
        binding.deskripsiPenyakit.text = myResultDesc
        binding.solutionDesc.text = myResultPrevention
        binding.goHome.setOnClickListener {
            startActivity(Intent(this@DetailActivity, UploadActivity::class.java))
        }
    }

    private fun showImage(uri: Uri) {
        binding.ivPlant.setImageURI(uri)
    }
    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_URI = "extra_uri"
        const val EXTRA_DESC = "extra_desc"
        const val EXTRA_PREVENTION = "extra_prevention"
    }
}