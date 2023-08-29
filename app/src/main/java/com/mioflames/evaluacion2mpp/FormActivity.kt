package com.mioflames.evaluacion2mpp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mioflames.evaluacion2mpp.db.AppDataBase
import com.mioflames.evaluacion2mpp.db.Compra
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(modifier = Modifier.fillMaxSize()) {
                barritaFormulario()
                agregarCompra()

            }
            backButton()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun agregarCompra() {

    val contexto = LocalContext.current
    //Instancia de la base de datos
    val database = AppDataBase.getInstance(contexto)
    val dao = database.compraDao()
    //variable para almacenar lo ingresado en el textfield.
    val compraEstado = remember { mutableStateOf(TextFieldValue()) }
    //Variable de estado para validar si la compra fué ingresada inicializada en falso
    val compraAgregada = remember { mutableStateOf(false) }
    //Layout principal
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "Ingrese una nueva compra para su lista")
        //Campo de texto para ingresar la compra
        TextField(
            value = compraEstado.value,
            onValueChange = { compraEstado.value = it },
            label = { Text(text = "Ej: Paltas") },
            //modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
        //Programacion del botón para agregar una nueva compra.
        Button(//modifier = Modifier.fillMaxWidth(),
            onClick = {
                //variable que contendrá los datos ingresado en el textfield para pasarlo a validar
                val compraNombre = compraEstado.value.text
                //condicional que valida que la variable no esté vacía
                if (compraNombre.isNotBlank()) {
                    //variable que almacena los datos ingresados agregandolos a la bd según
                    //las columnas
                    val nuevaCompra = Compra(compra = compraNombre, comprado = false)
                    //Corrutina
                    CoroutineScope(Dispatchers.IO).launch {
                        //llamado a la variable instancia dao para insertar finalmente la compra
                        dao.insert(nuevaCompra)
                    }
                    //Cambio de estado a verdadero si la compra fué correctamente agregada.
                    compraAgregada.value = true
                }
            }

        )
        {
            Text(text = stringResource(id = R.string.botonAgregarForm))
        }

        //se vuelve a validar si la compra fué agregada, mostrará el texto.
        if(compraAgregada.value == true){
            Text(
                text = "Compra agregada correctamente",
                color = Color.Green,
                modifier = Modifier.padding(top = 20.dp)
            )
        }
    }
}

//Barra para formulario, solo cumple función estética.
@Composable
fun barritaFormulario(){

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.LightGray)
        .padding(15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ){
        Icon(Icons.Filled.Add ,
            contentDescription ="carrito",
            Modifier.padding(5.dp))
        Text(text = stringResource(id = R.string.barraForm),
            Modifier.padding(5.dp))
    }
}

@Composable
//Programación botón para volver a la pantalla de inicio
fun backButton(){
    //variable de contexto
    val contexto = LocalContext.current
    Column(modifier = Modifier
        .padding(20.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        //boton con objeto intent llamando a la actividad principal para hacer la transición
        //de vuelta a la lista o pantalla de inicio
        ExtendedFloatingActionButton(
            onClick = {val intent: Intent = Intent(contexto, MainActivity::class.java)
                contexto.startActivity(intent)},
            icon = { Icon(Icons.Filled.ArrowBack, "Volver") },
            text = { Text(text = stringResource(id = R.string.botonVolver)) },
        )
    }
}

@Composable
@Preview
fun previewAll(){
    agregarCompra()
    backButton()
}



