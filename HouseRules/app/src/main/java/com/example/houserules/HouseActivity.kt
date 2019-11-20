package com.example.houserules

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_house.*


class HouseActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var ruleList: MutableList<Rule>


    //important to note here that the position in the array list, by the nature of the JSON storage system
    //is going to be the same as its ID in the firebase database.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house)


        //get intent to have house_name and gps and stuff in it
        //change houseadapter to rulebookadapter and implement it into listview
        //consider taking out rulebooks

        var position = 0
        var house_name = "DEFAULT HOUSE"
        var gps_coordinates = "DEFAULT GPS"
        var this_key = "DEFAULT KEY"
        //var house_from_key: House = null!!

        val bundle = intent.extras


        if (bundle!=null){
            //house_from_key =
            //this_key = bundle.getString("key")!!
            //house_name = bundle.getString("name")!!
            //gps_coordinates = bundle.getString("gps")!!
            position = bundle.getInt("position")


            dataReference = FirebaseDatabase.getInstance().getReference("houses/" + position.toString())
            ruleList = mutableListOf()

            //this_key = bundle.getString("key")!!
            //house_name = dataReference.
            //gps_coordinates = bundle.getString("gps")!!

            this.nameView.setText(house_name)
            this.gpsView.setText(gps_coordinates)
            this.qrView.setText(this_key)
        }

        dataReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                //
            }

            override fun onDataChange(p0: DataSnapshot?) {
                Log.d("DEBUG", "ENTER ON DATA CHANGE")
                if(p0!!.exists()) {
                    //ruleList.clear()

                    nameView.text = ""
                    gpsView.text = ""
                    qrView.text = ""
                    house_name = ""
                    gps_coordinates = ""
                    this_key = ""

                    //messy way of collecting the different attributes from the one house json node
                    var counter = 0
                    for (i in p0.children) {
                        val temp = i.getValue()
                        Log.d("VALUE AT " + counter.toString(), temp.toString())

                        if (counter == 0) {
                            gps_coordinates = temp.toString()
                        }

                        if (counter == 1) {
                            gps_coordinates += ", " + temp.toString()
                        }

                        if (counter == 2) {
                            house_name = temp.toString()
                        }

                        if (counter == 3) {
                            this_key = temp.toString()
                        }

                        if (counter == 4) {
                            for (j in i.children) {
                                var temp_name = j.getKey()
                                var temp_desc = j.getValue().toString()
                                val rule = Rule(temp_name, temp_desc)
                                Log.d("RULE DEBUG" , rule.toString())
                                ruleList.add(rule)
                            }
                        }


                        counter += 1


                    }
                    //val adapter = HouseAdapter(applicationContext, R.layout.house, houseList)
                    //listView.adapter = adapter
                }
                else {
                    Log.d("XOXO", "Data Snapshot does not exist")
                }
                nameView.setText(house_name)
                gpsView.setText("Located at: " + gps_coordinates)
                qrView.setText(this_key)
                //displays rules in list form
                val adapter = RuleAdapter(applicationContext, R.layout.rule, ruleList)
                listView.adapter = adapter
            }
        })
    }
}