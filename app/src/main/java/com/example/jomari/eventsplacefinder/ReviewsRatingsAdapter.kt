package com.example.jomari.eventsplacefinder

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewsRatingsAdapter(val mCtx: Context, val layoutResId: Int, val heroList: List<ReviewsRatings>) :
    ArrayAdapter<ReviewsRatings>(mCtx, layoutResId, heroList) {

    lateinit var textViewUpdate: TextView

    val acct = GoogleSignIn.getLastSignedInAccount(this.mCtx)
    //val user = FirebaseAuth.getInstance()
    val acctFbTwEm = user.currentUser
    val email = acctFbTwEm!!.email

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val textViewName = view.findViewById<TextView>(R.id.textViewName)

        val textViewDisplayName = view.findViewById<TextView>(R.id.displayName)

        textViewUpdate = view.findViewById(R.id.textViewUpdate)

        val count = view.findViewById<TextView>(R.id.count)
        val hero = heroList[position]

        textViewName.text = hero.reviews
        textViewDisplayName.text = hero.account
        count.text = hero.rating.toString()

        textViewUpdate.setOnClickListener {
            showUpdateDialog(hero)
        }

        return view
    }

    private fun showUpdateDialog(rr: ReviewsRatings) {
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Edit Reviews and Ratings")

        val inflater = LayoutInflater.from(mCtx)

        val view = inflater.inflate(R.layout.layout_update_hero, null)

        val editText = view.findViewById<EditText>(R.id.editTextName)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        editText.setText(rr.reviews)
        ratingBar.rating = rr.rating.toFloat()

        builder.setView(view)

        builder.setPositiveButton("View") { p0, p1 ->
            val dbHero = FirebaseDatabase.getInstance().getReference("event").child(id).child("ratings&reviews")
            val user = FirebaseDatabase.getInstance().getReference("event").child(id)
                .child("ratings&reviews").child(rr.id.toString()).child("email")


            val reviews = editText.text.toString().trim()

            if (reviews.isEmpty()) {
                editText.error = "Please enter a name"
                editText.requestFocus()
                return@setPositiveButton
            }



            user.addValueEventListener(object : ValueEventListener, AppCompatActivity() {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val emailPrev = p0.value.toString()
                    if (acct != null) {
                        try {
                            if (acct.email == emailPrev) {
                                val hero = ReviewsRatings(
                                    rr.id,
                                    acct.displayName.toString(),
                                    acct.email!!,
                                    reviews,
                                    ratingBar.rating.toInt()
                                )
                                dbHero.child(rr.id.toString()).setValue(hero)
                                Toast.makeText(mCtx, "Updated", Toast.LENGTH_LONG).show()
                            }
                            else {
                                Toast.makeText(mCtx, "Credentials Mismatch", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(mCtx, "" + e, Toast.LENGTH_LONG).show()
                        }
                    } else if (acctFbTwEm != null) {
                        try {
                            if(email == emailPrev) {
                                val hero = ReviewsRatings(
                                    rr.id,
                                    acctFbTwEm.displayName.toString(),
                                    acctFbTwEm.email!!,
                                    reviews,
                                    ratingBar.rating.toInt()
                                )
                                dbHero.child(rr.id.toString()).setValue(hero)
                                Toast.makeText(mCtx, "Updated", Toast.LENGTH_LONG).show()
                            }
                            else {
                                Toast.makeText(mCtx, "Credentials Mismatch", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(mCtx, "" + e, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            })
        }

        builder.setNegativeButton("No")
        { p0, p1 ->

        }

        val alert = builder.create()
        alert.show()

    }
}