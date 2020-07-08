package no.kristiania.android.programming.exam.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import no.kristiania.android.programming.exam.data.room.dao.AllLocationsDAO
import no.kristiania.android.programming.exam.data.room.entities.AllLocationsEntities

const val DATABASE_NAME = "NO_FOREIGN_LAND_DB"

@Database(entities = arrayOf(AllLocationsEntities::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun allLocationsDAO(): AllLocationsDAO

    //Creating singleton database object - only one connection to database is needed
    //As it is expensive operation we do not want to create new connection on each CRUD operation
    companion object {
        private var dataBase: AppDatabase? = null

        fun getDataBase(context: Context): AppDatabase {
            //If we have no instance of database create new one
            //else return existing instance
            if (dataBase == null) {
                dataBase = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
            }

            return dataBase!!
        }
    }
}