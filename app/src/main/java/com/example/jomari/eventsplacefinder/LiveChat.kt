package com.example.jomari.eventsplacefinder

import adapters.MessageAdapter
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_messages.*
import models.Message
import java.util.*

var messageId: String = ""

class LiveChat : AppCompatActivity() {

    var databaseReference: DatabaseReference? = null
    //lateinit var user: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFirebase()
        setContentView(R.layout.activity_messages)

        //user = FirebaseAuth.getInstance()

        val name = intent.getStringExtra("nameOfComp")
        supportActionBar!!.title = name.toString()

        setupSendButton()

        createFirebaseListener()

    }

    /**
     * Setup firebase
     */
    private fun initFirebase() {
        FirebaseApp.initializeApp(applicationContext)
        val uid = FirebaseAuth.getInstance().uid
        databaseReference = FirebaseDatabase.getInstance().getReference("/user-messages/$uid")
    }

    /**
     * Set listener for Firebase
     */
    private fun createFirebaseListener() {

        val google = GoogleSignIn.getLastSignedInAccount(this)
        val acctFbTwEm = user.currentUser

        if (google?.email != null) {

            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val postListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val toReturn: ArrayList<Message> = ArrayList()

                            for (data in dataSnapshot.children) {
                                val messageData = data.getValue<Message>(Message::class.java)

                                //unwrap
                                val message = messageData?.let { it } ?: continue

                                toReturn.add(message)
                            }

                            //sort so newest at bottom
                            toReturn.sortBy { message ->
                                message.timestamp
                            }

                            setupAdapter(toReturn)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //log error
                        }
                    }
                    val ref = databaseReference?.child("messages")?.orderByChild("name")?.equalTo(google.displayName)
                    ref!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            ref.addValueEventListener(postListener)
                        }

                    })
                }
            })
        } else if (acctFbTwEm?.email != null) {
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val postListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val toReturn: ArrayList<Message> = ArrayList()

                            for (data in dataSnapshot.children) {
                                val messageData = data.getValue<Message>(Message::class.java)

                                //unwrap
                                val message = messageData?.let { it } ?: continue

                                toReturn.add(message)
                            }

                            //sort so newest at bottom
                            toReturn.sortBy { message ->
                                message.timestamp
                            }

                            setupAdapter(toReturn)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            //log error
                        }
                    }
                    val ref =
                        databaseReference?.child("messages")?.orderByChild("name")?.equalTo(acctFbTwEm.displayName)
                    ref!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            ref.addValueEventListener(postListener)
                        }

                    })
                }
            })
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

    /**
     * Once data is here - display it
     */
    private fun setupAdapter(data: ArrayList<Message>) {
        val linearLayoutManager = LinearLayoutManager(this)
        messagesRecyclerView.layoutManager = linearLayoutManager
        messagesRecyclerView.adapter = MessageAdapter(data) {
            //Toast.makeText(this, "${it.text} clicked", Toast.LENGTH_SHORT).show()
        }

        //scroll to bottom
        messagesRecyclerView.scrollToPosition(data.size - 1)
    }

    /**
     * OnClick action for the send button
     */
    private fun setupSendButton() {
        mainActivitySendButton.setOnClickListener {
            if (!mainActivityEditText.text.toString().isEmpty() || mainActivityEditText.text.length >= 25) {
                sendData()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Send data to firebase
     */
    private fun sendData() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        val acctFbTwEm = user.currentUser
        if (acct != null) {
            messageId = databaseReference!!.push().key.toString()
            databaseReference?.child("messages")?.child(messageId)
                ?.setValue(Message(mainActivityEditText.text.toString(), acct.displayName!!))

            //clear the text
            mainActivityEditText.setText("")
        }
        else if(acctFbTwEm != null){
            messageId = databaseReference?.push()?.key.toString()
            databaseReference?.child("messages")?.child(messageId)
                ?.setValue(Message(mainActivityEditText.text.toString(), acctFbTwEm.displayName!!))

            //clear the text
            mainActivityEditText.setText("")
        }
    }
}