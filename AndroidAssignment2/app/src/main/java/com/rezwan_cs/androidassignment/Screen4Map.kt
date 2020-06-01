package com.rezwan_cs.androidassignment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
class Screen4Map:FragmentActivity(), OnMapReadyCallback {
    lateinit var mMap:GoogleMap
    var latitude:Double = 11.00
    var longitude:Double = 13.49034
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen4_map)
        val mapFragment = getSupportFragmentManager()
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googleMap:GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        latitude = getIntent().getDoubleExtra("latitude", 56.3294)
        longitude = getIntent().getDoubleExtra("longitude", 20.3451)
        val name = getIntent().getStringExtra("name")
        Log.d("LAT2: ", latitude.toString() + " " + longitude.toString())
        val latLng = LatLng(-latitude, longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title(name))
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(18.toFloat()).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}