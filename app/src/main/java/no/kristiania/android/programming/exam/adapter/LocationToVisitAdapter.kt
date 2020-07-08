package no.kristiania.android.programming.exam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.location_item_view.view.*
import no.kristiania.android.programming.exam.R
import no.kristiania.android.programming.exam.data.gsontypes.locations.all.Location

class LocationToVisitAdapter(
    var list: ArrayList<Location> = ArrayList(),
    var onClickListener: View.OnClickListener? = null) : RecyclerView.Adapter<LocationToVisitAdapter.LocationToVisitHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationToVisitAdapter.LocationToVisitHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item_view, parent, false)

        return LocationToVisitHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(
        newHolder: LocationToVisitHolder,
        position: Int
    ) {
        newHolder.bindLocationToVisitWithViewHolder(list[position])
    }

    inner class LocationToVisitHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindLocationToVisitWithViewHolder(location: Location) {
            var properties = location.properties
            itemView.locationName.text = properties.name


            itemView.tag = properties.id
            itemView.setOnClickListener(onClickListener)
            itemView.pinImage.setOnClickListener(onClickListener)
            itemView.pinImage.tag =properties.id
            itemView.locationName.tag =properties.id
        }
    }
}