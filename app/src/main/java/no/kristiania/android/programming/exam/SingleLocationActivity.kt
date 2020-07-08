package no.kristiania.android.programming.exam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import no.kristiania.android.programming.exam.data.api.NoForeignLandFeedClient
import no.kristiania.android.programming.exam.data.api.SingleLocationListener
import no.kristiania.android.programming.exam.data.gsontypes.locations.single.Place
import no.kristiania.android.programming.exam.fragments.LocationDescriptionFragment


class SingleLocationActivity : AppCompatActivity(), SingleLocationListener, OnMapReadyCallback {


    companion object {
        //As we could try to show map from main activity or SingleLocationActivity
        // it makes sense to put i in one static function
        fun myMapSettings(map: GoogleMap, latitude: String, longitude: String) {
            map.isMyLocationEnabled = true
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            map.uiSettings.isZoomGesturesEnabled = true

            val location = LatLng(latitude.toDouble(), longitude.toDouble())
            val camera = CameraUpdateFactory.newLatLngZoom(
                location,
                14.0f
            )
            map.addMarker(
                MarkerOptions().position(location)
                    .title("")
            )
            map.moveCamera(camera)

        }

    }


    private lateinit var locationID: String
    private lateinit var operation: String
    private lateinit var latitude: String
    private lateinit var longitude: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_location)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Location"
        supportActionBar?.subtitle = "No foreign land"
        locationID = intent.getStringExtra("id")!!
        operation = intent.getStringExtra("operation")!!
        NoForeignLandFeedClient().getSingleLocation(this, locationID.toLong())
    }


    override fun onSingleLocationSuccess(place: Place?) {
        this@SingleLocationActivity.supportActionBar?.title = place?.name
        longitude = place?.lon.toString()
        latitude = place?.lat.toString()
        if (operation == "desc")
            getLocationDescriptionFragment(place)
        else
            getMapFragment()
        Log.d("myTag", "Single location data received")

    }

    private fun getMapFragment() {
        val mapFragment = SupportMapFragment()
        mapFragment.getMapAsync(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentHolder, mapFragment, "locationFragment")
            .commit()
    }

    private fun getLocationDescriptionFragment(place: Place?) {

        //It can happen that we have no picture or comments about place
        var description = if (place?.comments == null)
            "no comments"
        else
            place.comments

        var imageURL = if (place?.banner == null)
            "no_image_available"
        else
            place.banner


        val locationDescriptionFragment =
            LocationDescriptionFragment.newInstance(
                description,
                imageURL,
                place?.lat,
                place?.lon,
                place?.name
            )

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentHolder, locationDescriptionFragment, "locationFragment")
            .commit()
    }

    override fun onSingleLocationError() {
        Toast.makeText(this, "Error receiving data", Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(map: GoogleMap) {
        myMapSettings(map, latitude, longitude)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.three_dots, menu);
        return true
    }

    /**
     * This way we add back button to action bar
     * We simulate onBackPressed when user is clicking it
     * Also as in main activity we have ... menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.settingsMenuBtn -> {
                val myIntent = Intent(this, SettingsActivity::class.java)
                startActivity(myIntent)
                return false
            }
            R.id.closeAppMenuBtn -> {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Close application")
                alertDialogBuilder.setMessage("Are you sure that you want to close application?")
                alertDialogBuilder.setPositiveButton(android.R.string.yes) { _, _ ->
                    finishAffinity()
                }
                alertDialogBuilder.setNegativeButton(android.R.string.no) { _, _ ->
                }

                alertDialogBuilder.show()
                false
            }
            else -> false
        }
    }

}