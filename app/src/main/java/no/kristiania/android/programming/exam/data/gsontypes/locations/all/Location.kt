package no.kristiania.android.programming.exam.data.gsontypes.locations.all

data class Location(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)