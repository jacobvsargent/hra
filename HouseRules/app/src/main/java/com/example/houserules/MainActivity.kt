package com.example.houserules

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.Manifest.permission
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_showall.*


class MainActivity : AppCompatActivity() {

    //lateinit three main data references to be used throughout the activity
    lateinit var location: Location
    lateinit var dataReference: DatabaseReference
    lateinit var houseList: MutableList<House>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //basic go-to methods for displaying other pages on button clicks
        aboutBtn.setOnClickListener {
            displayAbout()
        }

        goToBtn.setOnClickListener {
            displayGoTo()
        }

        qrBtn.setOnClickListener {
            displayQr()
        }

        showAllBtn.setOnClickListener {
            displayShowAll()
        }

        //populate houseList with current houses
        houseList = mutableListOf()
        Log.d("GPS DEBUG", "ENTERED ID OF CLOSEST HOUSE FUN")
        dataReference = FirebaseDatabase.getInstance().getReference("houses")
        dataReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //
            }

            override fun onDataChange(p0: DataSnapshot?) {
                Log.d("GPS DEBUG", "HOUSE LIST IS BEING CREATED")
                if(p0!!.exists()) {
                    houseList.clear()
                    for (i in p0.children) {
                        val temp = i.getValue()
                        Log.d("VALUE", "XOXO" + temp.toString())
                        val house = i.getValue(House::class.java)
                        houseList.add(house!!)
                    }
                }
                else {
                    Log.d("GPS DEBUG", "Data Snapshot does not exist, so not house list")
                }
            }

        })
    }

    fun displayAbout() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    //finds closest house and pulls up that page
    fun displayGoTo() {
        var locationManager : LocationManager?

        //i don't think this ever actually gets used but it's helpful for debugging
        /*val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.d("GPS DEBUG","HERE: + " + location.longitude + ", " + location.latitude)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        */


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?


        //must request permissions specifically before using gps or camera features
        try {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA), 1337)
            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            Log.d("GPS DEBUG", "SUCCESSFULLY CAPTURED LOCATION at " + location.toString())
            Log.d("GPS DEBUG", "LATITUDE AT " + location.latitude.toString())
            Log.d("GPS DEBUG", "LONGITUDE AT " + location.longitude.toString())
        } catch (e: SecurityException) {
            Log.d("GPS ERROR", "THERE WAS AN ERROR WITH THE GPS") // lets the user know there is a problem with the gps
        }

        Log.d("GPS DEBUG", "ATTEMPTING TO FIND ID OF CLOSEST HOUSE")
        var nearest_house_id = getIdOfClosestHouse(location)

        Log.d("GPS DEBUG", "ATTEMPTING TO OPEN HOUSE THAT'S CLOSEST")
        displayHouse(nearest_house_id)

        //locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)


    }

    fun displayQr() {
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }

    fun displayShowAll() {
        val intent = Intent(this, ShowAllActivity::class.java)
        startActivity(intent)
    }

    fun getIdOfClosestHouse(location: Location): Int{
        var id = 0

        Log.d("GPS DEBUG", "SUCCESSFULLY CAPTURED DATABASE with length " + houseList.size.toString())

        var current_distance = 0L
        var best = 10000000000000000L
        var current_id = 0

        //simply passes through the list of houses and calculates the user's distance to each one
        for (house in houseList) {
            Log.d("GPS DEBUG", "ENTERING DISTANCE CALC LOOP")
            var temp_location = Location("")
            temp_location.latitude = house.GPS_lat.toDouble()
            temp_location.longitude = house.GPS_long.toDouble()
            current_distance = location.distanceTo(temp_location).toLong()

            if (current_distance < best) {
                Log.d("GPS DEBUG", "NEW CLOSEST FOUND, house " + current_id.toString() + " is closer " +
                        "to the user than house " + id.toString())
                Log.d("GPS DEBUG", "PREVIOUS BEST: " + best.toString() + " meters.")
                Log.d("GPS DEBUG", "NEW BEST: " + current_distance.toString()+ " meters.")


                id = current_id
                best = current_distance
            }

            if (current_id == 0) {
                best = current_distance
            }

            current_id += 1
        }


        return id
    }

    fun displayHouse(position: Int) {
        val intent = Intent(this, HouseActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }
}
