package no.kristiania.android.programming.exam.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//https://www.noforeignland.com/home/api/v1/places/
//API returns a lot of values that are not needed for our app
//We just need id of location and name of it
//Name we are going to display while ID is needed to retrieve data from
// https://www.noforeignland.com/home/api/v1/place?id=... endpoint
@Entity(tableName = "ALL_LOCATIONS_TABLE")
data class AllLocationsEntities (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "ID")
    val id: Long,
    @ColumnInfo(name = "NAME")
    val locationName: String?
)