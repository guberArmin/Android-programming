package no.kristiania.android.programming.exam.data.gsontypes.locations.single

data class SingleLocation(
    val place: Place,
    val snapshots: List<Snapshot>
)