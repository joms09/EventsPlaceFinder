package com.example.jomari.eventsplacefinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event.*

class EventWedding: AppCompatActivity() {

    lateinit var mrecylerview: RecyclerView
    lateinit var ref: Query
    lateinit var refCount: DatabaseReference
    lateinit var show_progress: ProgressBar

    lateinit var mAlert: ImageButton
    lateinit var cardView: CardView

    var placeid : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_event)

        ref = FirebaseDatabase.getInstance().getReference("event").orderByChild("type").equalTo("Wedding")
        mrecylerview = findViewById(R.id.reyclerview)
        mrecylerview.layoutManager = LinearLayoutManager(this)
        show_progress = findViewById(R.id.progress_bar)
        firebaseData()
    }

    fun firebaseData() {
        val option = FirebaseRecyclerOptions.Builder<Model>()
            .setQuery(ref, Model::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Model, MyViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(this@EventWedding).inflate(R.layout.card_view, parent, false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Model) {
                placeid = getRef(position).key.toString()
                val event = FirebaseDatabase.getInstance().getReference("event").child(placeid)

                show_progress.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
                holder.txt_name.text = model.Name
                Picasso.get().load(model.Image).into(holder.img_vet)

                holder.img_vet.setOnClickListener {

                    event.child("count").setValue(model.Count + 1).addOnCompleteListener {
                        val intent = Intent(this@EventWedding, SoloDetails::class.java)
                        intent.putExtra("id", placeid)
                        intent.putExtra("name", model.Name)
                        intent.putExtra("status", model.Status)
                        intent.putExtra("type", model.Type)
                        intent.putExtra("address", model.Address)
                        intent.putExtra("phone", model.Phone)
                        intent.putExtra("cpnumber", model.Cpnumber)
                        intent.putExtra("count", model.Count + 1)
                        intent.putExtra("image", model.Image)
                        intent.putExtra("eventDescription", model.EventDescription)
                        intent.putExtra("amenities", model.Amenities)
                        intent.putExtra("maxPeople", model.MaxPeople)
                        intent.putExtra("minPeople", model.MinPeople)
                        intent.putExtra("minPrice", model.MinPrice)
                        intent.putExtra("bHours", model.bHours)
                        startActivity(intent)
                    }
                }
            }
        }
        mrecylerview.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }
}