package com.mioflames.evaluacion2mpp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CompraDao {

    //Uso de metodos entregados por room para interactuar con los
    //datos de la tabla
    @Query("SELECT * FROM compra ORDER BY comprado")
    fun getAll(): List<Compra>
    @Query("SELECT COUNT(*) FROM compra")
    fun count():Int

    @Insert
    fun insert(compra: Compra):Long

    @Update
    fun update(compra: Compra)

    @Delete
    fun delete(compra: Compra)
}