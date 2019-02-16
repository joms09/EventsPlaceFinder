package com.example.jomari.eventsplacefinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

class EventResto : AppCompatActivity() {

    lateinit var mrecylerview : RecyclerView
    lateinit var ref: Query
    lateinit var refCount: DatabaseReference
    lateinit var show_progress: ProgressBar

    lateinit var mAlert : ImageButton

    lateinit var cardView: CardView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_event)

        ref = FirebaseDatabase.getInstance().getReference("event").orderByChild("type").equalTo("Resto")
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

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Model, com.example.jomari.eventsplacefinder.MyViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.example.jomari.eventsplacefinder.MyViewHolder {
                val itemView = LayoutInflater.from(this@EventResto).inflate(R.layout.card_view, parent, false)
                return com.example.jomari.eventsplacefinder.MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: com.example.jomari.eventsplacefinder.MyViewHolder, position: Int, model: Model) {
                val placeid = getRef(position).key.toString()
                val event = FirebaseDatabase.getInstance().getReference("event").child(placeid)

                show_progress.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
                holder.txt_name.text = model.Name
                Picasso.get().load(model.Image).into(holder.img_vet)

                holder.img_vet.setOnClickListener {

                    event.child("count").setValue(model.Count + 1).addOnCompleteListener {
                        val intent = Intent(this@EventResto, SoloDetails::class.java)
                        intent.putExtra("id", placeid)
                        intent.putExtra("name", model.Name)
                        intent.putExtra("status", model.Status)
                        intent.putExtra("type", model.Type)
                        intent.putExtra("count", model.Count + 1)
                        intent.putExtra("image", model.Image)
                        startActivity(intent)
                    }
                }
            }
        }
        mrecylerview.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        internal var txt_name: TextView = itemView!!.findViewById<TextView>(R.id.Display_title)
        internal var img_vet: ImageButton = itemView!!.findViewById<ImageButton>(R.id.Display_img)
    }
}