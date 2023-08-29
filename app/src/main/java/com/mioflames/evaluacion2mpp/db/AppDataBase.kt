package com.mioflames.evaluacion2mpp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Clase appdatebase que contendrá la base de datos.
//anotación database que incluye un array de entities.
@Database(entities = [Compra::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun compraDao(): CompraDao

    //Singleton
    companion object{
        @Volatile
        private var BASE_DATOS: AppDataBase?=null

        fun getInstance(contexto: Context):AppDataBase{
            return BASE_DATOS ?: synchronized(this){
                Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDataBase::class.java,
                    "ComprasBD.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it }
            }
        }
    }
}