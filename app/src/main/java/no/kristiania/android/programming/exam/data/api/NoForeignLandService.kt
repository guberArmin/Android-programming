package no.kristiania.android.programming.exam.data.api

import no.kristiania.android.programming.exam.data.gsontypes.locations.all.AllLocations
import no.kristiania.android.programming.exam.data.gsontypes.locations.single.SingleLocation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NoForeignLandService {

    @GET("/home/api/v1/places")//End point to get all places
    fun getAllLocations():Call<AllLocations>

    @GET("/home/api/v1/place")
    fun getSingleLocation(@Query("id") id:Long):Call<SingleLocation>
}