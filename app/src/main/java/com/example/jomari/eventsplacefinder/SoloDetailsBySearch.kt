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

class SoloDetailsBySearch : AppCompatActivity() {

    lateinit var listView: ListView

    lateinit var heroList: MutableList<ReviewsRatings>
    lateinit var ref: Query
    lateinit var chatBtn: Button

    private val toResult: Model
        get() = intent.getParcelableExtra(NewMessageActivity.USER_KEY)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.solo_details)
        val event = FirebaseDatabase.getInstance().getReference("event").child(toResult.Id!!)
        event.child("count").setValue(toResult.Count + 1).addOnCompleteListener {


            val intent = intent
            id = intent.getStringExtra("id")

            name_details.text = toResult.Name
            status_details.text = toResult.Status
            type_details.text = toResult.Type
            address_details.text = toResult.Address
            count_details.text = "${toResult.Count}"
            Picasso.get().load(toResult.Image).into(image_details)

        }

        heroList = mutableListOf()
        ref = FirebaseDatabase.getInstance().getReference("event").child(toResult.Id!!).child("ratings&reviews")

        listView = findViewById(R.id.listView)
        chatBtn = findViewById(R.id.liveChatBtn)

        rate_review.setOnClickListener {
            val intent = Intent(this, EnterRatingsReviews::class.java)
            intent.putExtra("id", toResult.Id)
            startActivity(intent)
        }

        chatBtn.setOnClickListener {
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            val acctFbTwEm = user.currentUser

            if (acct != null) {
                val intentAct = Intent(this, LatestMessagesActivity::class.java)
                intentAct.putExtra("nameOfComp", toResult.Name)
                startActivity(intentAct)
            } else if (acctFbTwEm != null) {
                val intentAct = Intent(this, LatestMessagesActivity::class.java)
                intentAct.putExtra("nameOfComp", toResult.Name)
                startActivity(intentAct)
            } else {
                Toast.makeText(applicationContext, "Invalid Account", Toast.LENGTH_LONG).show()
                val intentActLogin = Intent(this, OpenId::class.java)
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

                    val adapter = ReviewsRatingsAdapter(this@SoloDetailsBySearch, R.layout.reviews, heroList)
                    listView.adapter = adapter
                }
            }
        })
    }
}