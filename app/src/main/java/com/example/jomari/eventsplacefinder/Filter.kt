package com.example.jomari.eventsplacefinder

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_event.*

class Filter : AppCompatActivity() {

    lateinit var mSearchText: MaterialSearchBar
    lateinit var mRecyclerView: RecyclerView

    lateinit var mDatabase: DatabaseReference

    lateinit var FirebaseRecyclerAdapter: FirebaseRecyclerAdapter<Model, ViewHolder>

    var placeid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        mSearchText = findViewById(R.id.searchBar)
        mRecyclerView = findViewById(R.id.mListView)

        mDatabase = FirebaseDatabase.getInstance().getReference("event")

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mSearchText.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val searchText = mSearchText.text.toString().trim()

                loadFirebaseData(searchText)
            }
        })

    }

    private fun loadFirebaseData(searchText: String) {

        val firebaseSearchQuery = mDatabase.orderByChild("eventname").startAt(searchText).endAt(searchText + "\uf8ff")

        val option = FirebaseRecyclerOptions.Builder<Model>()
            .setQuery(firebaseSearchQuery, Model::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Model, ViewHolder>(option) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
                val itemView = LayoutInflater.from(this@Filter).inflate(R.layout.layout_list, p0, false)
                return ViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Model) {
                placeid = getRef(position).key.toString()
                val event = FirebaseDatabase.getInstance().getReference("event").child(placeid)


                event.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val status = p0.child("eventStatus").value
                        if (status == "Verified") {
                            holder.txt_name.text = model.eventname
                            holder.txt_event.text = model.eventtype
                            Picasso.get().load(model.Image).into(holder.img_vet)
                            holder.txt_name.setOnClickListener {
                                val intent = Intent(this@Filter, SoloDetails::class.java)
                                intent.putExtra("id", placeid)
                                intent.putExtra("eventname", model.eventname)
                                intent.putExtra("eventtype", model.eventtype)
                                intent.putExtra("address", model.Address)
                                intent.putExtra("count", model.Count + 1)
                                intent.putExtra("image", model.Image)
                                intent.putExtra("eventDescription", model.EventDescription)
                                intent.putExtra("amenities", model.Amenities)
                                intent.putExtra("city", model.city)
                                intent.putExtra("maxPeople", model.MaxPeople)
                                intent.putExtra("minPeople", model.MinPeople)
                                intent.putExtra("minPrice", model.MinPrice)
                                intent.putExtra("bHours", model.bHours)
                                startActivity(intent)

                            }
                        } else {
                            holder.txt_name.visibility = View.GONE
                            holder.img_vet.visibility = View.GONE
                            holder.txt_event.visibility = View.GONE
                            if (holder.equals(0)) {
                                holder.img_vet.visibility = View.GONE
                                holder.txt_event.visibility = View.GONE
                                noresult.text = "No Results Found"
                            }
                        }
                    }
                })
            }
        }
        mRecyclerView.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        internal var txt_name: TextView = itemView!!.findViewById(R.id.userName)
        internal var img_vet: CircleImageView = itemView!!.findViewById(R.id.img)
        internal var txt_event: TextView = itemView!!.findViewById(R.id.typeEvent)

    }
}