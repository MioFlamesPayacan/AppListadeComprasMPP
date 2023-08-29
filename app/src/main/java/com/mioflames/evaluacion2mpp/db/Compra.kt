package com.mioflames.evaluacion2mpp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

//Primer componente para el uso de room
@Entity
data class Compra (
    //Llave primaria id que se genere automaticamente
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //nombre del producto a comprar
    var compra:String,
    //variable booleana que permita mostrar si el producto ya fue comprado o no.
    var comprado:Boolean
)