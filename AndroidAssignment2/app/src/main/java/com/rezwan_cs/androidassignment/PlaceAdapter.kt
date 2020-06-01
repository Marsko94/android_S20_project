package com.rezwan_cs.androidassignment

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PlaceAdapter(
    var context: Context,
    arrayList: ArrayList<Place>
) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {
    var placeArrayList = ArrayList<Place>()
    var placeArrayList2 = ArrayList<Place>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val listItem =
            layoutInflater.inflate(R.layout.single_place, parent, false)
        return PlaceViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int
    ) {
        val place = placeArrayList[position]
        holder.mPlaceNumber.setText(position.toString() + 1.toString() + ".")
        holder.mPlaceName.setText((position + 1 ).toString()+ ". " + place.name)
        holder.mPlaceIcon.setOnClickListener {
            //TODO: Go to screen 4
            val i = Intent(context, Screen4Map::class.java)
            i.putExtra("latitude", place.latitude)
            i.putExtra("longitude", place.longitude)
            i.putExtra("name", place.name)
            Log.d(
                "LAT: ",
                place.latitude.toString() + " " + place.longitude.toString()
            )
            context.startActivity(i)
            // ((Activity)context).finish();
        }
    }

    override fun getItemCount(): Int {
        return placeArrayList.size
    }

    fun filter(s: String) { //Log.d("FIL: ", s+" | "+placeArrayList2.get(0).getName());
        placeArrayList.clear()
        if (s.isEmpty()) {
            placeArrayList.addAll(placeArrayList2)
        } else {
            for (place in placeArrayList2) {
                if (place.name.toLowerCase().contains(s.toLowerCase())) {
                    placeArrayList.add(place)
                    //Log.d("FIL2: ", placeArrayList.get(0).getName());
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class PlaceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mPlaceNumber: TextView
        var mPlaceName: TextView
        var mPlaceIcon: ImageView

        init {
            mPlaceNumber = itemView.findViewById(R.id.tv_place_number)
            mPlaceName = itemView.findViewById(R.id.tv_place_name)
            mPlaceIcon = itemView.findViewById(R.id.iv_place_icon)
            itemView.setOnClickListener {
                //TODO: GO TO Screen3 to show place name and details
                val i = Intent(context, Screen3::class.java)
                i.putExtra("PlaceId", placeArrayList[adapterPosition].id)
                context.startActivity(i)
                // ((Activity)context).finish();
            }
        }
    }

    init {
        placeArrayList = arrayList
        placeArrayList2.addAll(arrayList)
        //Log.d("ADAP: ", arrayList.toString());
//placeArrayList.add(new Place("DFLJK", "Dflkj",1.4444,3.44));
    }
}