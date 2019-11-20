package com.example.houserules

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import com.google.zxing.Result

//this activity is the camera view for scanning codes and returning the house view for the appropriate house
class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    lateinit var dataReference: DatabaseReference
    lateinit var houseList: MutableList<House>

    private var mScannerView: ZXingScannerView? = null

    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)   // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view


        houseList = mutableListOf()
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

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }


    override fun handleResult(rawResult: Result) {
        //JACOB: whenever the scanner picks up something from the scan, it sends it here

        // Do something with the result here
        Log.d("QR DEBUG", rawResult.getText()) // Prints scan results

        val intent = Intent(this, HouseActivity::class.java)

        var correct_position = 0

        Log.d("QR DEBUG", "SUCCESSFULLY CAPTURED DATABASE with length " + houseList.size.toString())

        var current_count = 0

        for (house in houseList) {
            Log.d("QR DEBUG", "ENTERING QR CHECK LOOP")
            Log.d("QR DEBUG house key", house.qr_key)
            Log.d("QR DEBUG raw result", rawResult.text)

            if (house.qr_key == rawResult.text) {
                correct_position = current_count
                Log.d("QR DEBUG", "CORRECT QR CODE FOUND!")
            }

            current_count += 1
        }

        Log.d("QR DEBUG", "OPENING HOUSE AT POSITION " + correct_position.toString())

        intent.putExtra("position", correct_position)
        startActivity(intent)

    }


}