package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.SavemarkEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.factory.ViewModelFactory
import com.dicoding.asclepius.viewmodel.ResultViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(application)
        val viewModel = ViewModelProvider(this, factory)[ResultViewModel::class.java]
        val label = intent.getStringExtra(EXTRA_RESULT_LABEL) ?: ""
        val score = intent.getStringExtra(EXTRA_RESULT_SCORE) ?: ""
        val desc = intent.getStringExtra(EXTRA_RESULT_DESC) ?: ""
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val labelScore = "$label : $score"
        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }
        val imageString = imageUri.toString()

        binding.resultText.text = labelScore
        binding.descText.text = desc

        viewModel.getSavemark(label).observe(this) { savemarkEntity ->
            var isSaved = false
            binding.btnSavemark.setOnClickListener {
                if (isSaved) {
                    viewModel.delete(savemarkEntity)
                } else {
                    val savemark = SavemarkEntity(label, score, imageString, desc)
                    viewModel.insert(savemark)
                }
            }

            if (savemarkEntity != null) {
                isSaved = true
                binding.btnSavemark.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.btnSavemark.context, R.drawable.ic_bookmark_full_orange
                    )
                )
            } else {
                binding.btnSavemark.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.btnSavemark.context, R.drawable.ic_bookmark_border_orange
                    )
                )
            }
        }

    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT_LABEL = "extra_result"
        const val EXTRA_RESULT_SCORE = "extra_score"
        const val EXTRA_RESULT_DESC = "extra_desc"
    }


}