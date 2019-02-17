package com.example.jomari.eventsplacefinder

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    companion object {
        const val USER_KEY = "USER_KEY"
        private val TAG = NewMessageActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        swiperefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))

        supportActionBar?.title = "Select User"

        fetchUsers()
        //Todo - Add more users and messages for screenshots

        swiperefresh.setOnRefreshListener {
            fetchUsers()
        }
    }

    private fun fetchUsers() {
        swiperefresh.isRefreshing = true

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                dataSnapshot.children.forEach {
                    Log.d(TAG, it.toString())
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    it.getValue(User::class.java)?.let {
                        if (it.uid != FirebaseAuth.getInstance().uid) {
                            adapter.add(UserItem(it))
                        }
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_newmessage.adapter = adapter
                swiperefresh.isRefreshing = false
            }

        })
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.name
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}
