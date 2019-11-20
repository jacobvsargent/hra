package com.example.houserules

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

//adapter for displaying rules in each house view
class RuleAdapter(val mContext: Context, val layoutResId: Int, val ruleList: List<Rule>):
    ArrayAdapter<Rule>(mContext, layoutResId, ruleList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflator: LayoutInflater = LayoutInflater.from(mContext)
        val view: View = layoutInflator.inflate(layoutResId, null)
        val nameTextView = view.findViewById<TextView>(R.id.nameView)
        val descTextView = view.findViewById<TextView>(R.id.descView)

        val rule = ruleList[position]

        nameTextView.text = "rules for " + rule.name
        descTextView.text = rule.desc
        var altColor = Color.parseColor("#EEEEEE")

        //alternates colors
        if (position % 2 == 0) {
            altColor = Color.parseColor("#E0E0E0")
        }

        view.setBackgroundColor(altColor)

        return view
    }
}