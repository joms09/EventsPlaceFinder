package com.example.jomari.eventsplacefinder

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.solo_details.*

var id: String = ""


class SoloDetails : AppCompatActivity() {

    lateinit var listView: ListView

    lateinit var heroList: MutableList<ReviewsRatings>
    lateinit var ref: Query
    lateinit var chatBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solo_details)

        //val user = FirebaseAuth.getInstance()

        val intent = intent
        id = intent.getStringExtra("id")
        val name = intent.getStringExtra("eventname")
        val type = intent.getStringExtra("eventtype")
        val address = intent.getStringExtra("address")
        val count = intent.getIntExtra("count", 0)
        val image = intent.getStringExtra("image")
        val description = intent.getStringExtra("eventDescription")
        val amenities = intent.getStringExtra("amenities")
        val maxPeople = intent.getStringExtra("maxPeople")
        val minPeople = intent.getStringExtra("minPeople")
        val minPrice = intent.getStringExtra("minPrice")
        val bHours = intent.getStringExtra("bHours")
        val city = intent.getStringExtra("city")


        name_details.text = name
        type_details.text = type
        address_details.text = address
        count_details.text = "" + count
        Picasso.get().load(image).into(image_details)
        description_details.text = description
        amenities_details.text = amenities
        city_details.text = city
        max_people_details.text = maxPeople
        min_people_details.text = minPeople
        min_price_details.text = minPrice
        bHours_details.text = bHours



        heroList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("event").child(id).child("ratings&reviews")

        listView = findViewById(R.id.listView)
        chatBtn = findViewById(R.id.liveChatBtn)

        rate_review.setOnClickListener {
            val intent = Intent(this@SoloDetails, EnterRatingsReviews::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

        chatBtn.setOnClickListener {
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            val acctFbTwEm = user.currentUser

            if (acct != null) {
                val intentAct = Intent(this@SoloDetails, LatestMessagesActivity::class.java)
                intentAct.putExtra("nameOfComp", name)
                startActivity(intentAct)
            } else if (acctFbTwEm != null) {
                val intentAct = Intent(this@SoloDetails, LatestMessagesActivity::class.java)
                intentAct.putExtra("nameOfComp", name)
                startActivity(intentAct)
            } else {
                Toast.makeText(applicationContext, "Invalid Account", Toast.LENGTH_LONG).show()
                val intentActLogin = Intent(this@SoloDetails, OpenId::class.java)
                startActivity(intentActLogin)
            }
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    heroList.clear()

                    for (h in p0.children) {
                        val hero = h.getValue(ReviewsRatings::class.java)
                        heroList.add(hero!!)
                    }

                    val adapter = ReviewsRatingsAdapter(this@SoloDetails, R.layout.reviews, heroList)
                    listView.adapter = adapter
                }
            }
        })
    }
}