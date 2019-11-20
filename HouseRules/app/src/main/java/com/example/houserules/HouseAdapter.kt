package com.example.houserules

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

//basic adapter for displaying house information on main page using layout inflator
class HouseAdapter(val mContext: Context, val layoutResId: Int, val houseList: List<House>):
    ArrayAdapter<House>(mContext, layoutResId, houseList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val nameTextView = view.findViewById<TextView>(R.id.houseView)
        val gpsTextView = view.findViewById<TextView>(R.id.gpsView)
        val qrTextView = view.findViewById<TextView>(R.id.qrView)

        val house = houseList[position]

        nameTextView.text = house.name
        gpsTextView.text = "GPS Coordinates: " + house.GPS_lat + ", " + house.GPS_long
        qrTextView.text = "QR Key: " + house.qr_key

        Log.d("XOXO", "VIEW RETURNED IN HOUSE ADAPTER")

        var altColor = Color.parseColor("#EEEEEE")

        //alternates colors
        if (position % 2 == 0) {
            altColor = Color.parseColor("#E0E0E0")
        }

        view.setBackgroundColor(altColor)

        return view
    }
}