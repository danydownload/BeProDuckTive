package com.example.beproducktive.ui.addedittasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.beproducktive.R

class CustomArrayAdapter(context: Context, private val itemList: List<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_item_layout, itemList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_item_layout, parent, false)
        }
        val itemText = view!!.findViewById<TextView>(R.id.text_view_spinner_item)
        itemText.text = itemList[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_item_layout, parent, false)
        }
        val itemText = view!!.findViewById<TextView>(R.id.text_view_spinner_item)
        itemText.text = itemList[position]
        return view
    }
}
