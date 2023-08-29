package com.mioflames.evaluacion2mpp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.mioflames.evaluacion2mpp.db.AppDataBase
import com.mioflames.evaluacion2mpp.db.Compra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Instancia de la base de datos
        lifecycleScope.launch(Dispatchers.IO) {
            val compraDao = AppDataBase.getInstance(this@MainActivity).compraDao()
            //llamado al método contar
            val cantRegistros = compraDao.count()
            //Condicional en caso de que no existan datos, entregará el texto de que no hay registros.
            if (cantRegistros <1){
                Log.d("MainActivity", "No hay registro de compras, agrega una nueva a tu lista")
            }
        }
        setContent {
            //Agregué un layout de Column para que la barrita de título quedara al principio.
            Column(modifier = Modifier.fillMaxSize()) {
                barritaTitulo()
                ListaComprasUI()

            }
            addCompraButton()
        }
    }
}

//Barrita de título, solo cumple función estética. 
@Composable
fun barritaTitulo(){

    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = Color.LightGray)
        .padding(15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ){
        Icon(Icons.Filled.ShoppingCart ,
            contentDescription ="carrito",
            Modifier.padding(5.dp))
        Text(text = stringResource(id = R.string.barraTitulo),
            Modifier.padding(5.dp))
    }
}

@Composable
fun ListaComprasUI() {
    //variable de contexto local
    val contexto = LocalContext.current
    //Estado para almacenar la lista de compras con la función de actualizar.
    val (compras, setCompras) = remember { mutableStateOf(emptyList<Compra>()) }
    //Efecto cuando cambia la lista de compras
    LaunchedEffect(compras){
        withContext(Dispatchers.IO){
            //Variable para instanciar Dao de la base de datos
            val dao = AppDataBase.getInstance(contexto).compraDao()
            //actualiza la lista de compras con los datos de la bd
            setCompras(dao.getAll())
        }
    }
    //Lazy column para mostrar la lista de compras
    LazyColumn(modifier = Modifier.fillMaxSize()){
        items(compras){ compra ->
            //Muestra los elementos de la lista usando la función CompraItem Ui
            compraItemUI(compra) {
                //Actualiza la lista de compras al borrar algún elemento
                setCompras(emptyList<Compra>())
            }
        }
    }

}

@Composable
fun compraItemUI(compra: Compra, onSave:() -> Unit = {}){
    //variable de alcance de corrutina para las operaciones asincronicas.
    val corrutina = rememberCoroutineScope()
    //variable de contexto
    val contexto = LocalContext.current
    //Layout principal
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 20.dp, horizontal = 20.dp)){
        //Condicional para validar si la compra está marcada como comprada , pueda mostrar
        //un icono de check en verde para diferenciar
        if(compra.comprado){
            Icon(
                Icons.Filled.CheckCircle,
                tint = Color.Green,
                contentDescription = "Comprado",
                modifier = Modifier.clickable {
                    //lanza corrutina en contexto IO
                    corrutina.launch(Dispatchers.IO) {
                        //variable para instanciar dao de la base de datos
                        val dao = AppDataBase.getInstance(contexto).compraDao()
                        //cambia el estado de comprado
                        compra.comprado = false
                        //Actualización del estado en la base de datos usando el método dao update
                        dao.update(compra)
                        //Función Onsave para actualizar la lista
                        onSave()
                    }
                }
            )
        }else{
            //Condicional valida si la compra no está "comprada", mostrará el icono check en gris.
            Icon(
                Icons.Filled.Check,
                contentDescription = "Compra por hacer",
                modifier = Modifier.clickable {
                    //corrutina en contexto IO
                    corrutina.launch(Dispatchers.IO) {
                        //Instancia de dao de la base de datos
                        val dao = AppDataBase.getInstance(contexto).compraDao()
                        //cambio de estado
                        compra.comprado = true
                        //Actualización en la base de datos
                        dao.update(compra)
                        //Función Onsave para actualizar la lista
                        onSave()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = compra.compra,
            modifier = Modifier.weight(2f))
        //Icono delete para elimianr la compra en color rojo
        Icon(
            Icons.Filled.Delete,
            tint = Color.Red,
            contentDescription = "eliminar",
            //Icono con evento clickeable
            modifier = Modifier.clickable{
                //corrutina en contexto IO
                corrutina.launch(Dispatchers.IO){
                    //variable que instancia dao de la base de datos
                    val dao = AppDataBase.getInstance(contexto).compraDao()
                    //actualización en la base de datos, usando el método delete de DAO
                    dao.delete(compra)
                    //Función onsave para actualizar la lista en pantalla
                    onSave()
                }
            })
    }
}

//Programación de Botón que lleve al formulario de ingreso nueva compra
@Composable
fun addCompraButton(){
    //variable de contexto local
    val contexto = LocalContext.current
    //Layout para que el boton se ubique en la parte derecha inferior
    Column(modifier = Modifier
        .padding(20.dp)
        .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        //Boton flotante con objeto intent, llamando a la formactivity para hacer la
        //transición al formulario de ingreso a una nueva compra.
        ExtendedFloatingActionButton(
            onClick = {val intent: Intent = Intent(contexto, FormActivity::class.java)
                contexto.startActivity(intent)},
            icon = { Icon(Icons.Filled.Add, "agregar") },
            text = { Text(text = stringResource(id = R.string.botonAgregar))},
        )
    }
}



@Preview
@Composable
fun previewItems(){
    Column {
        barritaTitulo()
        val compra = Compra(1, "Mantequilla", false)
        compraItemUI(compra)
        val compra2 = Compra(2, "Margarina", true)
        compraItemUI(compra2)
        addCompraButton()
    }


}
