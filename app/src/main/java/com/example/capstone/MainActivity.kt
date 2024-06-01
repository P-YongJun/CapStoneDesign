package com.example.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import android.provider.Settings
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.capstone.alert.BackgroundWorkBridge
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment

    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_gps -> {
                startActivity(Intent(this, com.example.capstone.GPS.GpsActivity::class.java))
                Log.d("MainActivity", "GPS")
                true
            }

            R.id.navigation_heartrate -> {
                startActivity(Intent(this, com.example.capstone.Heart_Rate.HeartActivity::class.java))
                Log.d("MainActivity", "HEARTRATE")
                true
            }

            R.id.navigation_home -> {
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, homeFragment).commit()
                Log.d("MainActivity", "HOME")
                true
            }
            R.id.navigation_location -> {
                startActivity(Intent(this, com.example.capstone.Search.SearchActivity::class.java))
                Log.d("MainActivity", "SEARCH")
                true
            }
            else -> false
        }
    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: MainActivity started")

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)

        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_activity_main, homeFragment).commit()

        navView.selectedItemId = R.id.navigation_home

        val workRequest = PeriodicWorkRequest.Builder(
            BackgroundWorkBridge::class.java,
            1, // 주기
            TimeUnit.MINUTES // 주기의 시간 단위
        ).build()
        WorkManager.getInstance(this).enqueue(workRequest)


        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        // 위치 권한이 부여되어 있는지 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우, 권한 요청 다이얼로그 표시
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        } else {
            // 권한이 있는 경우, 위치 서비스가 활성화되어 있는지 확인
            checkLocationServicesStatus()
        }
    }

    private fun checkLocationServicesStatus() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 위치 서비스가 비활성화된 경우, 알림 다이얼로그 표시
            showLocationServiceAlertDialog()
        }
    }

    private fun showLocationServiceAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" +
                "위치 설정을 수정하실래요?")
        builder.setPositiveButton("설정") { dialog: DialogInterface?, which: Int ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("취소") { dialog: DialogInterface?, which: Int ->
            dialog?.dismiss()
        }
        builder.create().show()
    }

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 1001
    }
}
