package no.kristiania.android.programming.exam.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*


class SharedPreferencesController(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val DATABASE_UPDATE = "database_update_data"
        const val SETTINGS_DOWNLOAD_CHECKBOX = "settings_download_checkbox"
        const val SETTINGS_SEEKER_VALUE = "settings_seeker_value"
    }

    private val pattern = "MM/dd/yyyy"
    private val simpleDateFormat = SimpleDateFormat(pattern)
    private val currentDate = Date()
    private val currentDateFormatted: String = simpleDateFormat.format(currentDate)
    fun checkIfDataShouldBeFetched(): Boolean {
        //Check if we have information about last database update in our shared preferences
        val lastUpdateDate = sharedPreferences
            .getString(DATABASE_UPDATE, null)

        //If there is no value for default update frequency set it to 30 as default
        var updateFrequency = getFromPreference(SETTINGS_SEEKER_VALUE)
        if (updateFrequency.isBlank()) updateFrequency = "30"
        //If there is no value of is download checkbox checked sett it to tare
        var isUpdating = getFromPreference(SETTINGS_DOWNLOAD_CHECKBOX)
        if(isUpdating.isBlank()) isUpdating = true.toString()

        //If we did not download data then we have to do it
        if (lastUpdateDate.isNullOrBlank())
            return true

        //Convert last update date in appropriate formant
        val lastUpdateFormatted = SimpleDateFormat(pattern).parse(lastUpdateDate)
        //Get difference between current date and date of last update of database
        val dateDifference = currentDate.time - lastUpdateFormatted!!.time
        //Convert data difference in days
        val dateDifferenceInDays = dateDifference / (24 * 60 * 60 * 1000) + 1

        //If data is x or more days old fetch it again
        if (dateDifferenceInDays.toInt() > updateFrequency.toInt() && isUpdating.toBoolean() )
            return true
        //If data is fetched less then 30 days ago no need to fetch again
        return false
    }

    /**
     * Although it looks like unnecessary code to make this method
     * it is useful because we don't have to get formatted date in splash screen activity
     */
    fun putToPreference(
        key: String = DATABASE_UPDATE,
        value: String = currentDateFormatted
    ) {
        //If there is no information about database in our shared preference
        //it means we are fetching data for the first time
        sharedPreferences.edit().putString(key, value)
            .apply()
    }

    fun getFromPreference(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }
}