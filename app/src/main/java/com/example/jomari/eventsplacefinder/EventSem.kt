package com.example.jomari.eventsplacefinder

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_event__sem.*

class EventSem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event__sem)

        val lv = findViewById(R.id.mListView) as ListView
        val searchBar = findViewById(R.id.searchBar) as MaterialSearchBar

        searchBar.setHint("Search...")
        searchBar.setSpeechMode(true)

        var galaxies = arrayOf("Sombrero", "Cartwheel", "Robin", "Nica")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, galaxies)
        lv.setAdapter(adapter)


        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter.getFilter().filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })


        lv.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                Toast.makeText(this@EventSem, adapter.getItem(i)!!.toString(), Toast.LENGTH_SHORT).show()
            }
        })




        val myStrings = arrayOf("One", "Two", "Three")
        mySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, myStrings)
        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(this@EventSem, myStrings[p2], Toast.LENGTH_LONG).show()
            }
        }
    }
}
