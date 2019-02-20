package com.example.jomari.eventsplacefinder

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.jomari.eventsplacefinder.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.advance_search_result.*
import kotlinx.android.synthetic.main.filter_results.view.*

class AdvancedSearchResult : AppCompatActivity() {


    val resultRef = FirebaseDatabase.getInstance().getReference("event")


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advance_search_result)

        supportActionBar?.title = "Search Results"
        val db = FirebaseFirestore.getInstance()


        val placeid = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val status = intent.getStringExtra("status")
        val address = intent.getStringExtra("address")
        val count = intent.getIntExtra("count", 0)
        val image = intent.getStringExtra("image")

        val location = intent.getStringExtra("location")
        val pickstarttime = intent.getStringExtra("pickstarttime")
        val pickstartdate = intent.getStringExtra("pickstartdate")
        val capacity1 = intent.getStringExtra("capacity1")
        val type = intent.getStringExtra("event")
        val minibudget = intent.getStringExtra("minibudget")

        locationLabel.text = location
        pickstarttimeLabel.text = pickstarttime
        pickstartdateLabel.text = pickstartdate
        capacity1Label.text = capacity1
        eventLabel.text = type
        minibudgetLabel.text = minibudget

        val filterResult = resultRef.orderByChild("type").equalTo(type)
        filterResult.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val adapter = GroupAdapter<ViewHolder>()

                dataSnapshot.children.forEach { data ->
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    data.getValue(Model::class.java)?.let { model ->
                        val item = SearchItem(model)
                        item.result.Id = data.key
                        adapter.add(item)
                    }
                }

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
        })
    }
}

class SearchItem(val result: Model) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_event_type.text = result.Type
        viewHolder.itemView.textview_event_name.text = result.Name
    }

    override fun getLayout(): Int {
        return R.layout.filter_results
    }

}