package com.example.jomari.eventsplacefinder

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.ratings_reviews.*

class EnterRatingsReviews : AppCompatActivity() {

    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ratings_reviews)

        //val user = FirebaseAuth.getInstance()

        val id = intent.getStringExtra("id")
        ref = FirebaseDatabase.getInstance().getReference("event").child(id).child("ratings&reviews")

        buttonSave.setOnClickListener {
            val reviews = editTextName.text.toString().trim()

            when {
                reviews.isEmpty() -> {
                    editTextName.error = "Please enter a review"
                }
                else -> {

                    val acct = GoogleSignIn.getLastSignedInAccount(this)
                    val acctFbTwEm = user.currentUser

                    when {
                        acct != null -> {
                            try {

                                val rrId = ref.push().key

                                val account = acct.displayName.toString()

                                val email = acct.email

                                val hero = ReviewsRatings(rrId, account, email!!, reviews, ratingBar.rating.toInt())

                                ref.child(rrId.toString()).setValue(hero).addOnCompleteListener {
                                    Toast.makeText(
                                        applicationContext,
                                        "Reviews and Ratings Submitted",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    val intent = Intent(this@EnterRatingsReviews, HomePage::class.java)
                                    startActivity(intent)
                                }

                                editTextName.text.clear()
                                ratingBar.numStars to 0
                            }catch (e:Exception){
                                Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show()
                            }
                        }

                        acctFbTwEm != null -> {
                            try {
                                val rrId = ref.push().key

                                val account = acctFbTwEm.displayName.toString()

                                val email = acctFbTwEm.email

                                val hero = ReviewsRatings(rrId, account, email!!, reviews, ratingBar.rating.toInt())

                                ref.child(rrId.toString()).setValue(hero).addOnCompleteListener {
                                    Toast.makeText(
                                        applicationContext,
                                        "Reviews and Ratings Submitted",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    val intent = Intent(this@EnterRatingsReviews, HomePage::class.java)
                                    startActivity(intent)
                                }

                                editTextName.text.clear()
                                ratingBar.numStars to 0
                            } catch (e: Exception) {
                                Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show()
                            }
                        }
                        else -> {
                            Toast.makeText(applicationContext, "Please Login first", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@EnterRatingsReviews, OpenId::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}