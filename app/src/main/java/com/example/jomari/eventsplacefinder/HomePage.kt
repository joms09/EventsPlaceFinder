package com.example.jomari.eventsplacefinder

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.toolbar.*

class HomePage : AppCompatActivity() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        setSupportActionBar(toolbar)


        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            //connected to internet

            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            }

            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            }
        } else {
            Toast.makeText(baseContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            this.finish()
        }

        val eventWedding: ImageButton = findViewById(R.id.event_wedding)
        eventWedding.setOnClickListener {
            val intent = Intent(this, EventWedding::class.java)
            startActivity(intent)
        }

        val eventBirthday: ImageButton = findViewById(R.id.event_birthday)
        eventBirthday.setOnClickListener {
            val intent = Intent(this, EventBirthday::class.java)
            startActivity(intent)
        }

        val eventCorporate: ImageButton = findViewById(R.id.event_corporate)
        eventCorporate.setOnClickListener {
            val intent = Intent(this, EventCorporate::class.java)
            startActivity(intent)
        }

        val eventParty: ImageButton = findViewById(R.id.event_party)
        eventParty.setOnClickListener {
            val intent = Intent(this, EventParty::class.java)
            startActivity(intent)
        }

        val eventResto: ImageButton = findViewById(R.id.event_resto)
        eventResto.setOnClickListener {
            val intent = Intent(this, EventResto::class.java)
            startActivity(intent)
        }

        val eventSports: ImageButton = findViewById(R.id.event_sports)
        eventSports.setOnClickListener {
            val intent = Intent(this, EventSports::class.java)
            startActivity(intent)
        }

        val eventShoots: ImageButton = findViewById(R.id.event_shoots)
        eventShoots.setOnClickListener {
            val intent = Intent(this, EventShoots::class.java)
            startActivity(intent)
        }

        val eventWorkshop: ImageButton = findViewById(R.id.event_workshop)
        eventWorkshop.setOnClickListener {
            val intent = Intent(this, EventWorkshop::class.java)
            startActivity(intent)
        }

        val eventSeminar: ImageButton = findViewById(R.id.event_seminar)
        eventSeminar.setOnClickListener {
            val intent = Intent(this, EventSeminar::class.java)
            startActivity(intent)
        }

        val eventOthers: ImageButton = findViewById(R.id.event_others)
        eventOthers.setOnClickListener {
            val intent = Intent(this, EventOthers::class.java)
            startActivity(intent)
        }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.messenger_btn -> {
                val mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Redirecting to Live Chat")
                mProgressbar.setMessage("Please wait..")
                mProgressbar.show()
                Handler().postDelayed({
                    mProgressbar.dismiss()
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    startActivity(intent)
                }, 1500)
                super.onOptionsItemSelected(item)
            }
            R.id.menu_sign_out -> {
                val mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Signing Out!")
                mProgressbar.setMessage("Please wait..")
                mProgressbar.show()
                Handler().postDelayed({
                    mProgressbar.dismiss()
                    auth.signOut()
                    mGoogleSignInClient.signOut()
                    val intent = Intent(this, OpenId::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}