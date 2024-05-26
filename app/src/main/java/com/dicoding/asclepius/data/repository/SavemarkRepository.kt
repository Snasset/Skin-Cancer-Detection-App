package com.dicoding.asclepius.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.SavemarkEntity
import com.dicoding.asclepius.data.local.room.SavemarkDao
import com.dicoding.asclepius.data.local.room.SavemarkDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SavemarkRepository(application: Application) {
    private val mSavemarkDao: SavemarkDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = SavemarkDatabase.getInstance(application)
        mSavemarkDao = db.savemarkDao()
    }

    fun getAllSavemark(): LiveData<List<SavemarkEntity>> = mSavemarkDao.getAllSavemark()

    fun insert(savemark: SavemarkEntity) {
        executorService.execute { mSavemarkDao.insert(savemark) }
    }

    fun getSavemark(label: String):
            LiveData<SavemarkEntity> = mSavemarkDao.getSavemark(label)

    fun delete(savemark: SavemarkEntity) {
        executorService.execute { mSavemarkDao.delete(savemark) }
    }

}