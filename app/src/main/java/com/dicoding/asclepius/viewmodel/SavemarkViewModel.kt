package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.repository.SavemarkRepository

class SavemarkViewModel(application: Application) : ViewModel() {
    private val mSavemarkRepository: SavemarkRepository = SavemarkRepository(application)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllSavemark() = mSavemarkRepository.getAllSavemark()
}