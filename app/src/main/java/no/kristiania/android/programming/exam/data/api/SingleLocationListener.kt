package no.kristiania.android.programming.exam.data.api

import no.kristiania.android.programming.exam.data.gsontypes.locations.single.Place

interface SingleLocationListener {
    fun onSingleLocationSuccess(place: Place?)
    fun onSingleLocationError()
}