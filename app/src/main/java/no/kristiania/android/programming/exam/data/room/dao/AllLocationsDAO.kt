package no.kristiania.android.programming.exam.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import no.kristiania.android.programming.exam.data.room.entities.AllLocationsEntities

@Dao
interface AllLocationsDAO {
    @Query("DELETE FROM ALL_LOCATIONS_TABLE")
    fun deleteAll();

    @Query("INSERT INTO ALL_LOCATIONS_TABLE VALUES (:id, :name)")
    fun insert(id: Long, name: String)

    @Query("SELECT * FROM ALL_LOCATIONS_TABLE")
    fun fetchAllLocations(): List<AllLocationsEntities>?
}