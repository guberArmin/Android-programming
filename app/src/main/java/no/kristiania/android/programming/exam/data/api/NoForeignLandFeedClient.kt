package no.kristiania.android.programming.exam.data.api

import android.util.Log
import no.kristiania.android.programming.exam.data.gsontypes.locations.all.AllLocations
import no.kristiania.android.programming.exam.data.gsontypes.locations.single.SingleLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NoForeignLandFeedClient() {
    private var service: NoForeignLandService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.noforeignland.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(NoForeignLandService::class.java)
    }

    fun getSingleLocation(singleLocationListener: SingleLocationListener, id: Long) {

        val call = service.getSingleLocation(id)

        call.enqueue(object : Callback<SingleLocation> {
            override fun onFailure(call: Call<SingleLocation>, t: Throwable) {
                Log.d("MyTag", t.toString())
                singleLocationListener.onSingleLocationError()
            }

            override fun onResponse(
                call: Call<SingleLocation>,
                response: Response<SingleLocation>
            ) {
                if (response.isSuccessful) {
                    val singleLocation = (response.body()) as SingleLocation
                    singleLocationListener.onSingleLocationSuccess(singleLocation.place)
                } else {
                    singleLocationListener.onSingleLocationError()
                }
            }

        })
    }


    fun getAllPlaces(allLocationsListener: AllLocationsListener) {
        allLocationsListener.showProgress(true)

        val call = service.getAllLocations()

        call.enqueue(object : Callback<AllLocations> {
            override fun onFailure(call: Call<AllLocations>, t: Throwable) {
                allLocationsListener.showProgress(false)
                Log.d("MyTag", t.toString())
                allLocationsListener.onAllLocationsError()
            }

            override fun onResponse(
                call: Call<AllLocations>,
                response: Response<AllLocations>
            ) {
                allLocationsListener.showProgress(false)

                if (response.isSuccessful) {
                    val allLocations = (response.body()) as AllLocations
                    val locations = allLocations.features //As first line in API is just string
                    // we need to get features (which is actually list of all locations)
                    // that we are going to use as list of locations
                    allLocationsListener.onAllLocationsSuccess(locations)
                } else {
                    allLocationsListener.onAllLocationsError()
                }
            }

        }

        )
    }

}