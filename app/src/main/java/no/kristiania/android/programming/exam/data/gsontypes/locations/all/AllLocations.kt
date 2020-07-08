package no.kristiania.android.programming.exam.data.gsontypes.locations.all

//API format
//"type": "FeatureCollection",
//"features" is list of all locations in API
data class AllLocations(
    val type: String,
    val features: ArrayList<Location>
)