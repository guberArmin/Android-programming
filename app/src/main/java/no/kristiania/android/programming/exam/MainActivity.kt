package no.kristiania.android.programming.exam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import no.kristiania.android.programming.exam.adapter.LocationToVisitAdapter
import no.kristiania.android.programming.exam.data.gsontypes.locations.all.Geometry
import no.kristiania.android.programming.exam.data.gsontypes.locations.all.Location
import no.kristiania.android.programming.exam.data.gsontypes.locations.all.Properties
import no.kristiania.android.programming.exam.data.room.AppDatabase
import no.kristiania.android.programming.exam.data.room.dao.AllLocationsDAO


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var adapter: LocationToVisitAdapter
    private lateinit var allLocationsDAO: AllLocationsDAO
    private lateinit var listOfLocations: ArrayList<Location>
    private lateinit var singleLocationIntent: Intent //We need this intent as shared variable to be able to put extra from permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Home";
        supportActionBar?.subtitle = "No foreign land";
        //Database controller
        allLocationsDAO = AppDatabase.getDataBase(this).allLocationsDAO()

        adapter = LocationToVisitAdapter()
        recyclerViewLocations.layoutManager = LinearLayoutManager(this)
        recyclerViewLocations.adapter = adapter
        adapter.onClickListener = this

        getFromDatabase()

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var toSearchFor = s!!.toString().toLowerCase()
                //We are converting all location names to lower case and typed in text to lower case
                //so that we can compare them properly
                val filteredLocations = listOfLocations.filter { location ->
                    location.properties.name.toLowerCase().contains(toSearchFor)
                }
                adapter.list = filteredLocations as ArrayList<Location>
                counter.text = ("${filteredLocations.size} - locations displayed")

                adapter.notifyDataSetChanged()
            }

        })
    }

    //If we get back from previous activity we don't want to keep state
    override fun onResume() {
        super.onResume()
        if (searchBox.text.isNotEmpty())
            searchBox.setText("")
    }

    private fun getFromDatabase() {
        Thread(Runnable {
            val retrievedLocations = allLocationsDAO.fetchAllLocations()
            val convertedListOfLocations = ArrayList<Location>()
            //Get data from database and convert it to appropriate data for recycler view adapter
            retrievedLocations?.forEach { location ->
                convertedListOfLocations.add(
                    Location(
                        Geometry(
                            listOf(),
                            ""
                        ),
                        Properties(
                            "",
                            location.id,
                            location.locationName!!
                        ), ""
                    )
                )
            }
            listOfLocations = convertedListOfLocations

            adapter.list = listOfLocations
            runOnUiThread {
                counter.text = ("${listOfLocations.size} - locations displayed")
                adapter.notifyDataSetChanged()
            }
        }).start()

    }

    /**
     * Clicking on pin opens map
     * Clicking on any other part of item in list is opening description page
     */
    override fun onClick(v: View?) {
        singleLocationIntent = Intent(this, SingleLocationActivity::class.java)
        singleLocationIntent.putExtra("id", v?.tag.toString())
        when (v?.id) {
            R.id.pinImage -> {
                if (permissionCheck())
                    singleLocationIntent.putExtra(
                        "operation",
                        "map"
                    )//Get google map of given location
            }
            else ->
                singleLocationIntent.putExtra("operation", "desc")//Get description of location
        }
        //If we try to click on pin but don't give permissions we should not start activity
        if (singleLocationIntent.getStringExtra("operation").isNullOrEmpty())
            return
        else
            startActivity(singleLocationIntent)
    }

    private fun permissionCheck(): Boolean =
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            promptForPermission()
            false
        } else {
            true
        }

    private fun promptForPermission() {
        requestPermissions(
            this,
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
                    //Start single location activity on success
                    // or else there is no point on starting it as we can not show map
                    singleLocationIntent.putExtra(
                        "operation",
                        "map"
                    )//Configure intent to send data to SingleLocationActivity so it knows we want to show map fragment
                    startActivity(singleLocationIntent)
                } else {
                    Toast.makeText(
                        this,
                        "Permission denied, can't show a map",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    //Needed to add three dots menu for fetching latest data
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.three_dots, menu);
        return true
    }

    //Add listeners for each of options in our 3 dots menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
