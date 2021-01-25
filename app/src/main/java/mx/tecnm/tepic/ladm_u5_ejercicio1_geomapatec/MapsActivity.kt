package mx.tecnm.tepic.ladm_u5_ejercicio1_geomapatec

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var nombre = ""
    var longitud = 0.0
    var latitud = 0.0
    lateinit var ubicacionUsuario: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        var extras = intent.extras
        nombre = extras!!.getString("nombre").toString()
        latitud = extras!!.getDouble("latitud")
        longitud = extras!!.getDouble("longitud")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),700)
        }

        ubicacionUsuario = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 700){
            setTitle("SE OTORGO PERMISO");
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val edificio = LatLng(latitud, longitud)
        mMap.addMarker(MarkerOptions().position(edificio).title(nombre))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(edificio))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(edificio, 18f))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        /*ubicacionUsuario.lastLocation.addOnSuccessListener { it->
            if(it!=null){
                val posicionActual = LatLng(it.latitude,it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicionActual,15f))
            }
        }*/

    }
}