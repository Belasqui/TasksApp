package com.example.tasksapp

import android.app.Application
import androidx.room.Room
import com.example.tasksapp.data.AppDataBase

class TaskAppAplication : Application() {

    private lateinit var dataBase: AppDataBase


    override fun onCreate() {
        super.onCreate()

        dataBase = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "taskbeats-database"
        ).build()
    }

        fun getAppDataBase(): AppDataBase {
            return dataBase
        }
    }
