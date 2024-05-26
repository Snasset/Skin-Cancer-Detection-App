package com.dicoding.asclepius.data.local.room


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.asclepius.data.local.entity.SavemarkEntity


@Dao
interface SavemarkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: SavemarkEntity)

    @Update
    fun update(favorite: SavemarkEntity)

    @Delete
    fun delete(favorite: SavemarkEntity)

    @Query("SELECT * FROM SavemarkEntity WHERE label = :label")
    fun getSavemark(label: String): LiveData<SavemarkEntity>

    @Query("SELECT * from SavemarkEntity")
    fun getAllSavemark(): LiveData<List<SavemarkEntity>>
}