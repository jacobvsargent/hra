package com.example.houserules

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_showall.*


class ShowAllActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var houseList: MutableList<House>

    //this activity just collects every house from the firebase and populates the houselist with it then
    //displays all the houses

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showall)

        dataReference = FirebaseDatabase.getInstance().getReference("houses")
        houseList = mutableListOf()

        listView.setOnItemClickListener { _, _, position, _ ->
            displayHouse(position)
        }

        dataReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                //
            }

            override fun onDataChange(p0: DataSnapshot?) {
                Log.d("DEBUG", "ENTER ON DATA CHANGE")
                if(p0!!.exists()) {
                    houseList.clear()
                    for (i in p0.children) {
                        val temp = i.getValue()
                        Log.d("VALUE", "XOXO" + temp.toString())

                        val house = i.getValue(House::class.java)
                        houseList.add(house!!)
                    }
                    val adapter = HouseAdapter(applicationContext, R.layout.house, houseList)
                    listView.adapter = adapter
                }
                else {
                    Log.d("XOXO", "Data Snapshot does not exist")
                }
            }

        })


    }

    fun displayHouse(position: Int) {
        val intent = Intent(this, HouseActivity::class.java)
        intent.putExtra("position", position)
        startActivity(intent)
    }


}