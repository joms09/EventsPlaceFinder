package com.example.jomari.eventsplacefinder

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_company_messages.*

class CompanyMessages : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_messages)

        verifyUserisLoggedIn()



    }

    private fun verifyUserisLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, OpenId::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menu_sign_out -> {
                val mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Signing Out!")
                mProgressbar.setMessage("Please wait..")
                mProgressbar.show()
                Handler().postDelayed({
                    mProgressbar.dismiss()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, OpenId::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
