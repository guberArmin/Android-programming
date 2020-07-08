package no.kristiania.android.programming.exam.fragments

import android.Manifest
import android.app.ActionBar
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_location_description.view.*
import no.kristiania.android.programming.exam.R
import no.kristiania.android.programming.exam.SingleLocationActivity
import kotlin.math.roundToInt

class LocationDescriptionFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val LONGITUDE = "longitude"
        private const val LATITUDE = "latitude"
        private const val NAME = "name"
        private const val DESCRIPTION = "description"
        private const val IMAGE_URL = "imageURL"

        fun newInstance(
            description: String,
            imageURL: String,
            lat: Double?,
            lon: Double?,
            name: String?
        ): LocationDescriptionFragment {
            val fragment = LocationDescriptionFragment()
            val bundle = Bundle();
            bundle.putString(LONGITUDE, lon.toString())
            bundle.putString(LATITUDE, lat.toString())
            bundle.putString(DESCRIPTION, description)
            bundle.putString(NAME, name)
            bundle.putString(IMAGE_URL, imageURL)
            fragment.arguments = bundle
            return fragment
        }

    }

    //Map variables
    private lateinit var mapFragment: SupportMapFragment
    private var latitude = ""
    private var longitude = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragment =
            inflater.inflate(R.layout.fragment_location_description, container, false)
        val description = arguments?.getString(DESCRIPTION)
        val imageURL = arguments?.getString(IMAGE_URL)
        val locationName = arguments?.getString(NAME)
        latitude = arguments?.getString(LATITUDE)!!
        longitude = arguments?.getString(LONGITUDE)!!
        //As data coming in is in HTML format I am using web view to display it in proper way
        rootFragment.locationDescription.loadDataWithBaseURL(
            null,
            description,
            "text/html",
            "utf-8",
            null
        );
        rootFragment.locationName.text = locationName


        if (imageURL.equals("no_image_available") || imageURL.isNullOrBlank())
            Log.d("myTag", "No image available")
        else
            Picasso.get().load(imageURL).into(rootFragment.locationImage)
        rootFragment.locationImage.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    rootFragment.locationImage.layoutParams.height =
                        ActionBar.LayoutParams.WRAP_CONTENT
                    rootFragment.locationImage.layoutParams.width =
                        ActionBar.LayoutParams.MATCH_PARENT
                    rootFragment.locationImage.requestLayout()
                }
                MotionEvent.ACTION_UP -> {
                    rootFragment.locationImage.layoutParams.height =
                        ActionBar.LayoutParams.WRAP_CONTENT
                    rootFragment.locationImage.layoutParams.width = dpToPx(200, context!!)
                    rootFragment.locationImage.requestLayout()
                }
                else -> {

                }
            }
            true
        }


        rootFragment.pinImageFragment.setOnClickListener {
            mapFragment = SupportMapFragment()
            mapFragment.getMapAsync(this)
            if (permissionCheck()) {
                createMapFragment()
            }
        }


        return rootFragment
    }

    override fun onMapReady(map: GoogleMap) {
        SingleLocationActivity.myMapSettings(map, latitude, longitude)
    }

    //Check do we have needed permissions
    private fun permissionCheck(): Boolean =
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermissions()
            false
        } else {
            true
        }

    // Prompt window, asking user about permissions
    private fun askForPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1234
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1234 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Show map when permission is given
                    createMapFragment()

                } else {
                    Toast.makeText(
                        activity,
                        "Permission denied, can't show a map",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun createMapFragment() {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentHolder, mapFragment, "map")
            ?.addToBackStack("singleLocation")?.commit()
    }

    // Convert dp to pixels so that we can restore default picture of image
    // https://stackoverflow.com/questions/35803313/set-imageview-size-programmatically-in-dp-java/35803372
    fun dpToPx(dp: Int, context: Context): Int {
        val density: Float = context.resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }
}