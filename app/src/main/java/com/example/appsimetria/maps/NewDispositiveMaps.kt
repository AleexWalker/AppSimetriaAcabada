package com.example.appsimetria.maps

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appsimetria.ServicesMenu
import com.example.appsimetria.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.appsimetria.databinding.ActivityNewDispositiveMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_new_dispositive_maps.*
import kotlinx.android.synthetic.main.custom_toast_maps_1.*

class NewDispositiveMaps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityNewDispositiveMapsBinding

    private lateinit var baseDatos: FirebaseFirestore
    private lateinit var resultScanner: String

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    data class Dispositivo(val id: String, val latitud: String, val longitud: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewDispositiveMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firestore Database & Load Scanner
        baseDatos = Firebase.firestore
        loadScanner()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun loadScanner() {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        resultScanner = sharedPreferences.getString("datos", null).toString()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val succes : Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.estilomapa
                ))
            if (!succes) {
                Log.e("MapsActivity", "Style pairsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find map style. Error: ", e)
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        mMap.setOnMarkerDragListener(this)
        mMap.setOnMyLocationButtonClickListener(this)

        item_boton_add_maps_type.setOnClickListener {
            if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL)
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            else
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        item_boton_add_maps_back.setOnClickListener {
            startActivity(Intent(this, ServicesMenu::class.java))
        }

        item_boton_card.setOnClickListener {
            saveLatLngData(latitud, longitud)
            writeDispositivo(Dispositivo(resultScanner, latitud.toString(), longitud.toString()))

            val intentMenu = Intent(this, ServicesMenu::class.java)
            startActivity(intentMenu)
        }

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)

                mMap
                    .animateCamera(CameraUpdateFactory
                        .newLatLngZoom(currentLatLong, 18f))

                latitud = location.latitude
                longitud = location.longitude
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        mMap.animateCamera(CameraUpdateFactory
            .newLatLngZoom(LatLng(latitud, longitud), 18f))
        return true
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions
            .title("$resultScanner $currentLatLong")
            .draggable(true)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerDrag(p0: Marker) {
        return
    }

    override fun onMarkerDragEnd(p0: Marker) {
        latitud = p0.position.latitude
        longitud = p0.position.longitude

        toastPersonalizadoMaps1()
        saveLatLngData(latitud, longitud)
    }

    override fun onMarkerDragStart(p0: Marker) {
        Toast.makeText(this, "Lo has arrastrado a: (${p0.position.latitude},${p0.position.longitude}) ", Toast.LENGTH_LONG).show()
        return
    }

    private fun saveLatLngData(latitud : Double, longitud : Double) {
        val sharedPreferences = getSharedPreferences("LatLng", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putFloat("latitud", latitud.toFloat())
        editor.putFloat("longitud", longitud.toFloat())
        editor.apply()
    }

    private fun toastPersonalizadoMaps1() {
        val layoutToast =  layoutInflater.inflate(R.layout.custom_toast_maps_1, constraintToastMaps1)
        Toast(this).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.TOP, 0, 0)
            view = layoutToast
        }.show()
    }

    private fun writeDispositivo(dispositivo: Dispositivo) {
        val data = HashMap<String, Any>()
        data["ID"] = dispositivo.id
        data["Latitud"] = dispositivo.latitud
        data["Longitud"] = dispositivo.longitud

        baseDatos.collection("Dispositivos").document(resultScanner)
            .set(data)
            .addOnSuccessListener { Log.d(TAG, "Successfly written") }
            .addOnFailureListener { Log.w(TAG, "Failed to be written!") }
    }
}