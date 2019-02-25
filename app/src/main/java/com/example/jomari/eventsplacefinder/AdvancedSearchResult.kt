package com.example.jomari.eventsplacefinder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jomari.eventsplacefinder.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.advance_search_result.*
import kotlinx.android.synthetic.main.filter_results.view.*

class AdvancedSearchResult : AppCompatActivity() {

    val uid = FirebaseAuth.getInstance().uid
    var minibudget: String = ""
    var capacity1: String = ""
    var amenities: String = ""
    val adapter = GroupAdapter<ViewHolder>()
    var keyId: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advance_search_result)

        supportActionBar?.title = "Search Results"

        val placeid = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val status = intent.getStringExtra("status")
        val address = intent.getStringExtra("address")
        val count = intent.getIntExtra("count", 0)
        val image = intent.getStringExtra("image")
        amenities = intent.getStringExtra("amenities")
        val location = intent.getStringExtra("location")
        val pickstarttime = intent.getStringExtra("pickstarttime")
        val pickstartdate = intent.getStringExtra("pickstartdate")
        capacity1 = intent.getStringExtra("capacity1")
        val type = intent.getStringExtra("event")
        minibudget = intent.getStringExtra("minibudget")

        locationLabel.text = location
        pickstarttimeLabel.text = pickstarttime
        pickstartdateLabel.text = pickstartdate
        capacity1Label.text = capacity1
        eventLabel.text = type
        minibudgetLabel.text = minibudget

        val reff = FirebaseDatabase.getInstance().getReference("event").orderByChild("type").equalTo(type)

        reff.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    keyId = data.key!!
                    Log.d("tag", keyId)
                }
            }
        })

        val resultRef = FirebaseDatabase.getInstance().getReference("event").child(keyId)

        resultRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                dataSnapshot.children.forEach { data ->
                    data.getValue(Model::class.java)?.let { model ->
                        val typeFromDb = model.eventtype
                        val maxPeopleFromDb = model.MaxPeople
                        val minPeopleFromDb = model.MinPeople
                        val minPriceFromDb = model.MinPrice
                        val eventStatus = model.eventStatus
                        if (eventStatus == "Verified") {
                            if (typeFromDb == type) {
                                if (maxPeopleFromDb.toString() >= capacity1 && minPeopleFromDb.toString() <= capacity1) {
                                    if (minPriceFromDb.toString() <= minibudget) {
                                        val item = SearchItem(model)
                                        item.result.Id = data.key
                                        adapter.add(item)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

        adapter.setOnItemClickListener { item, view ->
            val searchItem = item as SearchItem
            val intent = Intent(view.context, SoloDetailsBySearch::class.java)
            intent.putExtra(USER_KEY, searchItem.result)
            intent.putExtra("id", placeid)
            intent.putExtra("name", name)
            intent.putExtra("status", status)
            intent.putExtra("type", type)
            intent.putExtra("address", address)
            intent.putExtra("count", count)
            intent.putExtra("image", image)
            startActivity(intent)
            finish()
        }
        recyclerview_view_result.adapter = adapter

    }
}

class SearchItem(val result: Model) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_event_type.text = result.eventtype
        viewHolder.itemView.textview_event_name.text = result.eventname
        if (!result.Image!!.isEmpty()) {
            val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)

            Glide.with(viewHolder.itemView.imageview_event.context)
                .load(result.Image)
                .thumbnail(0.1f)
                .apply(requestOptions)
                .into(viewHolder.itemView.imageview_event)
        }
    }

    override fun getLayout(): Int {
        return R.layout.filter_results
    }

}