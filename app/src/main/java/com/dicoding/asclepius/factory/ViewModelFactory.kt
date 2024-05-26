package com.dicoding.asclepius.factory


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.viewmodel.NewsViewModel
import com.dicoding.asclepius.viewmodel.ResultViewModel
import com.dicoding.asclepius.viewmodel.SavemarkViewModel

class ViewModelFactory private constructor(
    private val mApplication: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavemarkViewModel::class.java)) {
            return SavemarkViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(mApplication) as T
        } else if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (instance == null) {
                synchronized(ViewModelFactory::class.java) {
                    instance = ViewModelFactory(application)
                }
            }
            return instance as ViewModelFactory
        }
    }
}