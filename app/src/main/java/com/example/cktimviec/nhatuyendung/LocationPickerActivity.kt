package com.example.cktimviec.nhatuyendung

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cktimviec.databinding.ActivityLocationPickerBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.cktimviec.R

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityLocationPickerBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thiết lập Map
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)

        // Nút hoàn tất chọn địa điểm
        binding.btnDone.setOnClickListener {
            val location = binding.etLocation.text.toString()
            if (location.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("selectedLocation", location)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Vui lòng chọn một địa điểm", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Đặt vị trí mặc định (Hà Nội)
        val defaultLocation = LatLng(21.0285, 105.8542)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))

        // Xử lý sự kiện khi người dùng chọn vị trí trên bản đồ
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Chọn địa điểm"))
            binding.etLocation.setText("Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
        }
    }
}
