package no.kristiania.android.programming.exam

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import no.kristiania.android.programming.exam.utils.SharedPreferencesController
import no.kristiania.android.programming.exam.utils.SharedPreferencesController.Companion.DATABASE_UPDATE
import no.kristiania.android.programming.exam.utils.SharedPreferencesController.Companion.SETTINGS_DOWNLOAD_CHECKBOX
import no.kristiania.android.programming.exam.utils.SharedPreferencesController.Companion.SETTINGS_SEEKER_VALUE

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesController: SharedPreferencesController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("myLog", "In settings activity")
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        sharedPreferences = getSharedPreferences(
            "no.kristiania.android.programming.exam",
            Context.MODE_PRIVATE
        )
        sharedPreferencesController = SharedPreferencesController(sharedPreferences)

        val checkBoxCheckedValue =
            sharedPreferencesController.getFromPreference(SETTINGS_DOWNLOAD_CHECKBOX)
        val seekBarValue = sharedPreferencesController.getFromPreference(SETTINGS_SEEKER_VALUE)

        //Download new data automatically by default
        if (checkBoxCheckedValue.isBlank())
            sharedPreferencesController.putToPreference(SETTINGS_DOWNLOAD_CHECKBOX, "true")

        //Update every 30 days by default
        if (seekBarValue.isBlank())
            sharedPreferencesController.putToPreference(SETTINGS_SEEKER_VALUE, "30")

        //Setup values saved for checkbox and seeker
        if (checkBoxCheckedValue.contains("true")) {
            dataDownloadCheckbox.visibility = View.VISIBLE
            seekerSlider.progress = seekBarValue.toInt()
            var message = ""
            message = getTextForSeeker(seekBarValue)
            updateFrequency.text = message
        } else if (checkBoxCheckedValue.contains("false")) {
            dataDownloadCheckbox.isChecked = false
            seekerSlider.isEnabled = false
            seekerSlider.progress = seekBarValue.toInt()
            var message = ""
            message = getTextForSeeker(seekBarValue)
            updateFrequency.text = message
        }

        //Delete data about database update triggering downloading and saving data to database
        reDownloadBtn.setOnClickListener {
            sharedPreferencesController.putToPreference(DATABASE_UPDATE, "")
            val myIntent = Intent(this, SplashScreenActivity::class.java)
            startActivity(myIntent)
        }

        dataDownloadCheckbox.setOnClickListener {
            sharedPreferencesController.putToPreference(
                SETTINGS_DOWNLOAD_CHECKBOX,
                dataDownloadCheckbox.isChecked.toString()
            )
            seekerSlider.isEnabled = dataDownloadCheckbox.isChecked
        }

        seekerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sharedPreferencesController.putToPreference(
                    SETTINGS_SEEKER_VALUE,
                    progress.toString()
                )
                updateFrequency.text = getTextForSeeker(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        }
        )
    }

    private fun getTextForSeeker(seekBarValue: String): String {
        return when {
            seekBarValue.toInt() == 0 -> "On every lunch"
            seekBarValue.toInt() == 1 -> ("Every $seekBarValue day")
            else -> ("Every $seekBarValue days")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}