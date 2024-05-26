package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.entity.SavemarkEntity
import com.dicoding.asclepius.data.repository.SavemarkRepository

class ResultViewModel(application: Application) : ViewModel() {
    private val mSavemarkRepository: SavemarkRepository = SavemarkRepository(application)

    fun insert(savemark: SavemarkEntity) {
        mSavemarkRepository.insert(savemark)
    }

    fun delete(savemark: SavemarkEntity) {
        mSavemarkRepository.delete(savemark)
    }

    fun getSavemark(label: String) =
        mSavemarkRepository.getSavemark(label)

}