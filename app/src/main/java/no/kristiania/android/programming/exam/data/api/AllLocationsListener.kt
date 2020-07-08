package no.kristiania.android.programming.exam.data.api

import no.kristiania.android.programming.exam.data.gsontypes.locations.all.Location

interface AllLocationsListener {
    fun onAllLocationsSuccess(locationList: ArrayList<Location>?)
    fun onAllLocationsError()
    fun showProgress(show: Boolean)
}