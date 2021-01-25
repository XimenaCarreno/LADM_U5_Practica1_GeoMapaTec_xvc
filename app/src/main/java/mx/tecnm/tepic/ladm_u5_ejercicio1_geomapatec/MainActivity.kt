package mx.tecnm.tepic.ladm_u5_ejercicio1_geomapatec

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.google.android.gms.location.LocationServices

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data> ()
    lateinit var locacion : LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }


        var arreglo : ArrayList<String> = ArrayList()
        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)


        baseRemota.collection("tecnologico")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        textView.setText("ERROR: "+firebaseFirestoreException.message)
                        return@addSnapshotListener
                    }


                    arreglo.clear()
                    var resultado = ""
                    posicion.clear()


                    for(document in querySnapshot!!){
                        var data = Data()
                        data.nombre = document.getString("nombre").toString()
                        data.posicion1 = document.getGeoPoint("posicion1")!!
                        data.posicion2 = document.getGeoPoint("posicion2")!!
                        data.descripcion = document.getString("descripcion").toString()
                        posicion.add(data)
                        arreglo.add(data.nombre)

                    }

                    val list = ArrayAdapter(this,android.R.layout.simple_list_item_1,arreglo)
                    listaLugares.adapter = list
                }

        listaLugares.setOnItemClickListener { parent, view, position, id ->
            baseRemota.collection("tecnologico")
                .whereEqualTo("nombre", arreglo.get(position))
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Toast.makeText(this, "NO HAY CONEXION", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    var p = ""
                    var nombre = ""
                    var posicionLatitud = 0.0
                    var posicionLongitud = 0.0

                    for(document in  querySnapshot!!){
                        nombre = document.getString("nombre").toString()
                        posicionLatitud = document.getGeoPoint("posicion1")!!.latitude
                        posicionLongitud = document.getGeoPoint("posicion1")!!.longitude

                        p = "EDIFICIO: ${document.getString("nombre")} \n\n COORDENADAS: \n" +
                                "De [${document.getGeoPoint("posicion1")!!.latitude}, ${document.getGeoPoint("posicion1")!!.longitude}] A \n" +
                                "[${document.getGeoPoint("posicion2")!!.latitude}, ${document.getGeoPoint("posicion2")!!.longitude}]\n\n" +
                                "AQUI ENCUENTRAS: ${document.getString("descripcion")}"
                    }

                    AlertDialog.Builder(this)
                        .setMessage("BIENVENIDO AL SERVICIO DE MAPA. SELECCIONASTE:\n\n"+p)
                        .setPositiveButton("VER EN EL MAPA") {d, p ->
                            var otraVentana = Intent(this, MapsActivity::class.java)
                            otraVentana.putExtra("latitud", posicionLatitud)
                            otraVentana.putExtra("longitud", posicionLongitud)
                            otraVentana.putExtra("nombre", nombre)
                            startActivity(otraVentana)
                        }
                        .setNegativeButton("CANCELAR") {d, p -> }
                        .show()

                }
        }

    }


}

class Oyente(puntero:MainActivity) : LocationListener{
    var p = puntero

    override fun onLocationChanged(location: Location) {
        p.textView3.setText("${location.latitude}, ${location.longitude}")
        var geoPosicionGPS = GeoPoint(location.latitude,location.longitude)

        for(item in p.posicion){
            if(item.estoyEn(geoPosicionGPS)){
                p.textView3.setText("Estas en ${item.nombre}")
            }
        }
    }

}

