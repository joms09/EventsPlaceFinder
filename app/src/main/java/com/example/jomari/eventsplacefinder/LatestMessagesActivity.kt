package com.example.jomari.eventsplacefinder

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.jomari.eventsplacefinder.NewMessageActivity.Companion.USER_KEY
import models.ChatMessage
import models.User
import views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    private val latestMessagesMap = HashMap<String, ChatMessage>()

    companion object {
        var currentUser: User? = null
        val TAG = LatestMessagesActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        verifyUserIsLoggedIn()
        
        supportActionBar!!.title = "Messenger"

        saveUserToDatabase()

        recyclerview_latest_messages.adapter = adapter

        swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))

        fetchCurrentUser()
        listenForLatestMessages()

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        new_message_fab.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        swiperefresh.setOnRefreshListener {
            verifyUserIsLoggedIn()
            fetchCurrentUser()
            listenForLatestMessages()
        }
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
        swiperefresh.isRefreshing = false
    }

    private fun listenForLatestMessages() {
        swiperefresh.isRefreshing = true
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "has children: " + dataSnapshot.hasChildren())
                if (!dataSnapshot.hasChildren()) {
                    swiperefresh.isRefreshing = false
                }
            }

        })


        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    latestMessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    latestMessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentUser = dataSnapshot.getValue(User::class.java)
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val intentFromSignIn = intent
        val company = intentFromSignIn.getStringExtra("company")
        if (company != null) {
            saveUserToDatabase()
        }
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            Toast.makeText(this, "Invalid Account",Toast.LENGTH_LONG).show()
            val intent = Intent(this, OpenId::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun saveUserToDatabase() {

        val intent = Intent()
        val currentUser = intent.getStringExtra("company")
        val uid = user.uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        if (currentUser != null) {
            val user = User(uid, currentUser)

            ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("", "Finally we saved the user to Firebase Database")
                }
                .addOnFailureListener {
                    Log.d("", "Failed to set value to database: ${it.message}")
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.menu_sign_out -> {
                val mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Signing Out!")
                mProgressbar.setMessage("Please wait..")
                mProgressbar.show()
                Handler().postDelayed({
                    mProgressbar.dismiss()
                    user.signOut()
                    mGoogleSignInClient.signOut()
                    val intent = Intent(this, OpenId::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
                super.onOptionsItemSelected(item)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
