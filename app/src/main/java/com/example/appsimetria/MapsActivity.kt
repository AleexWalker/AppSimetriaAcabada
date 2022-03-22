package com.example.appsimetria

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Camera
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.appsimetria.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.custom_toast_maps_1.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation : Location
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding

    private var latitud : Double = 0.0
    private var longitud : Double = 0.0

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.secondary_statusbar)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        item_boton_card.setOnClickListener {
            val intentMenu = Intent(this, MenuOpcionesPreparadoMaps::class.java)
            startActivity(intentMenu)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val succes : Boolean = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.estilomapaestandar))
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

        item_boton_maps_type.setOnClickListener {
            if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL)
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            else
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
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
                saveLatLngData(latitud, longitud)
            }
        }
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions
            .title("$currentLatLong")
            .draggable(true)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
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

    override fun onMyLocationButtonClick(): Boolean {
        mMap.animateCamera(CameraUpdateFactory
            .newLatLngZoom(LatLng(latitud, longitud), 18f))
        return true
    }
}

