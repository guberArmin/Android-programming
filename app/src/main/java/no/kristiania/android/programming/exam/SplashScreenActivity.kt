package no.kristiania.android.programming.exam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import no.kristiania.android.programming.exam.data.api.AllLocationsListener
import no.kristiania.android.programming.exam.data.api.NoForeignLandFeedClient
import no.kristiania.android.programming.exam.data.gsontypes.locations.all.Location
import no.kristiania.android.programming.exam.data.room.AppDatabase
import no.kristiania.android.programming.exam.data.room.dao.AllLocationsDAO
import no.kristiania.android.programming.exam.utils.SharedPreferencesController
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class SplashScreenActivity : AppCompatActivity(), AllLocationsListener {
    private lateinit var allLocationsDAO: AllLocationsDAO
    private lateinit var sharedPreferences: SharedPreferences
    private var isFetchingData = false
    private lateinit var listOfFetchedLocations: ArrayList<Location>
    private lateinit var sharedPreferencesController: SharedPreferencesController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Database controller
        allLocationsDAO = AppDatabase.getDataBase(this).allLocationsDAO()
        //Using shared preference we are checking should we fetch new data or used data from database
        //If data is older then x days fetch it again and update database, else use data from database
        //As fetching data and writing it to database takes long time we don't want to do it to often
        sharedPreferences = getSharedPreferences(
            "no.kristiania.android.programming.exam",
            Context.MODE_PRIVATE
        )
        sharedPreferencesController = SharedPreferencesController(sharedPreferences)
        isFetchingData = sharedPreferencesController.checkIfDataShouldBeFetched();

        //If we are fetching data first call no foreign land API and on success save to database
        if (isFetchingData) {
            NoForeignLandFeedClient().getAllPlaces(this)
        } else {
            //If we are loading data from database
            //Make short delay such that we can display our splash screen and go to main activity
            Handler().postDelayed({
                val mainIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                this@SplashScreenActivity.startActivity(mainIntent)
                this@SplashScreenActivity.finish()
            }, 500)
        }
    }

    /**
     * On API call success save all data to database
     */
    private fun saveDataToDatabase(locations: ArrayList<Location>?) {
        //Have to save to database async so that we don't freeze UI
        Thread(Runnable {
            val numberOFFetched = locations?.size
            var counter = 0
            allLocationsDAO.deleteAll()//If data is saved delete it so that we can get newest data
            Log.d("myTag", "Starting saving to database ${Date()}")
            locations?.forEach { location ->
                counter++
                val percentageSaved: Double =
                    (counter.toDouble() / numberOFFetched!! * 100).toBigDecimal().setScale(
                        1,
                        RoundingMode.UP
                    )
                        .toDouble() // Get how many locations have been saved to database out of downloaded locations
                runOnUiThread {
                    loadingMessage.text =
                        ("Saving data \n $percentageSaved %\n performed only on first run of app\n and updates")
                }
                allLocationsDAO.insert(location.properties.id, location.properties.name)
            }
            Log.d("myTag", "Done writing to database ${Date()}")
            //After we have saved all data from API response to database we can save date of download in shared preference
            //in case of interrupting writing to database we are going to fetch again and save again
            sharedPreferencesController.putToPreference()
            val mainIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(mainIntent)
            //Activity is done and should be closed. This way we avoid user going back to splash screen
            this@SplashScreenActivity.finish()
        }).start()
    }

    override fun onAllLocationsSuccess(locationList: ArrayList<Location>?) {
        if (isFinishing)
            return
        Log.d("myTag", "API call success")
        if (locationList != null) {
            listOfFetchedLocations = locationList
        }
        updateLocations(locationList!!)
    }


    override fun onAllLocationsError() {
        Toast.makeText(this, "Unable to fetch data from server", Toast.LENGTH_LONG).show()
    }

    override fun showProgress(show: Boolean) {
        if (isFinishing)
            return
        loadingMessage.text =
            if (show) "Fetching data" else "No internet connection"
    }

    private fun updateLocations(list: ArrayList<Location>) {
        if (isFinishing)
            return
        saveDataToDatabase(list)
        Log.d("myTag", "All fetching operations are done.")

    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}