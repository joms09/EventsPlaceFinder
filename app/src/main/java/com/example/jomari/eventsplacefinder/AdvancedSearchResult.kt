package com.example.jomari.eventsplacefinder

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.advance_search_result.*

class AdvancedSearchResult : AppCompatActivity() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advance_search_result)

        val location = intent.getStringExtra("location")
        val pickstarttime = intent.getStringExtra("pickstarttime")
        val pickendtime = intent.getStringExtra("pickendtime")
        val pickstartdate = intent.getStringExtra("pickstartdate")
        val pickenddate = intent.getStringExtra("pickenddate")
        val capacity1 = intent.getStringExtra("capacity1")
        val event = intent.getStringExtra("event")
        val minibudget = intent.getStringExtra("minibudget")

        locationLabel.text = location
        pickstarttimeLabel.text = pickstarttime
        pickendtimeLabel.text = pickendtime
        pickstartdateLabel.text = pickstartdate
        pickenddateLabel.text = pickenddate
        capacity1Label.text = capacity1
        eventLabel.text = event
        minibudgetLabel.text = minibudget

    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}