package com.example.taskhelper.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskhelper.data.database.dao.ContactDao
import com.example.taskhelper.data.database.dao.TaskDao
import com.example.taskhelper.data.entities.ContactEntities
import com.example.taskhelper.data.entities.TaskEntities
import retrofit2.Converter


@Database(
    entities = [TaskEntities::class, ContactEntities::class],
    version = 1,
    exportSchema = false)

abstract class HelperDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun contactDao(): ContactDao
    //abstract fun checkListDao(): CheckListDao

    companion object {

        var dataBaseInstance: HelperDataBase? = null

        @Synchronized
        fun getDataBaseInstance(context: Context): HelperDataBase {

            if (dataBaseInstance == null) {

                dataBaseInstance = Room.databaseBuilder(
                    context.applicationContext,
                    HelperDataBase::class.java,
                    "TaskHelper.db"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return dataBaseInstance!!
        }
    }
}