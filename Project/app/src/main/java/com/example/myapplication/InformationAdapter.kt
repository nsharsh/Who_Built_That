package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class InformationAdapter(private val context: Context, private val data: ArrayList<String>): BaseAdapter() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val itemNames = arrayOf("Annual Revenue", "Estimated Employee Count", "Owners", "Location", "Subsidiaries")
    private val imageNames = arrayOf("dollar_sign", "employees", "owners", "location", "subsidiary")

    override fun getCount(): Int {
        return data.size
    }
    override fun getItem(position: Int): String {
        return data[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View {
        val itemView = inflater.inflate(R.layout.list_item, parent, false)
        val itemTextView = itemView.findViewById(R.id.item_title) as TextView
        itemTextView.text = itemNames[position]
        val itemValueView = itemView.findViewById(R.id.item_value) as TextView
        itemValueView.text = getItem(position)
        val itemImageView = itemView.findViewById(R.id.list_img) as ImageView
        val imageName = imageNames[position]
        val resourceID = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        itemImageView.setImageResource(resourceID)
        return itemView
    }
}