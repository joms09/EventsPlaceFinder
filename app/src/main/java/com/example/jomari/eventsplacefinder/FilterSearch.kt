package com.example.jomari.eventsplacefinder

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.country_child.view.*
import kotlinx.android.synthetic.main.filter_search.*

class FilterSearch : AppCompatActivity() {

    var countries:MutableList<String> = ArrayList()
    var displayList:MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        loadData()
//        country_list.layoutManager = LinearLayoutManager(this)
        country_list.layoutManager = GridLayoutManager(this,2)
        country_list.adapter = CountryAdapter(displayList,this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main1,menu)
        val searchItem = menu.findItem(R.id.menu_search)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            val editext = searchView.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
            editext.hint = "Search here..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    displayList.clear()
                    if(newText!!.isNotEmpty()){
                        val search = newText.toLowerCase()
                        countries.forEach {
                            if(it.toLowerCase().contains(search)){
                                displayList.add(it)
                            }
                        }
                    }else{
                        displayList.addAll(countries)
                    }
                    country_list.adapter?.notifyDataSetChanged()
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }


    class CountryAdapter(items : List<String>,ctx:Context) : RecyclerView.Adapter<CountryAdapter.ViewHolder>(){
        private var list = items
        private var context = ctx

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            p0?.name?.text = list[p1]
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.country_child,p0,false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
            val name = v.country_name!!
        }
    }

    private fun loadData(){
        countries.add("Afghanistan")
        countries.add("Albania")
        countries.add("Algeria")
        countries.add("Andorra")
        countries.add("Angola")
        countries.add("Antigua and Barbuda")
        countries.add("Argentina")
        countries.add("Armenia")
        countries.add("Australia")
        countries.add("Austria")
        countries.add("Azerbaijan")
        countries.add("Bahamas")
        countries.add("Bahrain")
        countries.add("Bangladesh")
        countries.add("Barbados")
        countries.add("Belarus")
        countries.add("Belgium")
        countries.add("Belize")
        countries.add("Benin")
        countries.add("Bhutan")
        countries.add("Bolivia")
        countries.add("Bosnia and Herzegovina")
        countries.add("Botswana")
        countries.add("Brazil")
        countries.add("Brunei")
        countries.add("Bulgaria")
        countries.add("Burkina Faso")
        countries.add("Burundi")
        countries.add("Cabo Verde")
        countries.add("Cambodia")
        countries.add("Cameroon")
        countries.add("Canada")
        countries.add("Central African Republic (CAR)")
        countries.add("Chad")
        countries.add("Chile")
        countries.add("China")
        countries.add("Colombia")
        countries.add("Comoros")
        countries.add("Democratic Republic of the Congo")
        countries.add("Republic of the Congo")
        countries.add("Costa Rica")
        countries.add("Cote d'Ivoire")
        countries.add("Croatia")
        countries.add("Cuba")
        countries.add("Cyprus")
        countries.add("Czech Republic")
        countries.add("Denmark")
        countries.add("Djibouti")
        countries.add("Dominica")
        countries.add("Dominican Republic")
        countries.add("Ecuador")
        countries.add("Egypt")
        countries.add("El Salvador")
        countries.add("Equatorial Guinea")
        countries.add("Eritrea")
        countries.add("Estonia")
        countries.add("Ethiopia")
        countries.add("Fiji")
        countries.add("Finland")
        countries.add("France")
        countries.add("Gabon")
        countries.add("Gambia")
        countries.add("Georgia")
        countries.add("Germany")
        countries.add("Ghana")
        countries.add("Greece")
        countries.add("Grenada")
        countries.add("Guatemala")
        countries.add("Guinea")
        countries.add("Guinea-Bissau")
        countries.add("Guyana")
        countries.add("Haiti")
        countries.add("Honduras")
        countries.add("Hungary")
        countries.add("Iceland")
        countries.add("India")
        countries.add("Indonesia")
        countries.add("Iran")
        countries.add("Iraq")
        countries.add("Ireland")
        countries.add("Israel")
        countries.add("Italy")
        countries.add("Jamaica")
        countries.add("Japan")
        countries.add("Jordan")
        countries.add("Kazakhstan")
        countries.add("Kenya")
        countries.add("Kiribati")
        countries.add("Kosovo")
        countries.add("Kuwait")
        countries.add("Kyrgyzstan")
        countries.add("Laos")
        countries.add("Latvia")
        countries.add("Lebanon")
        countries.add("Lesotho")
        countries.add("Liberia")
        countries.add("Libya")
        countries.add("Liechtenstein")
        countries.add("Lithuania")
        countries.add("Luxembourg")
        countries.add("Macedonia (FYROM)")
        countries.add("Madagascar")
        countries.add("Malawi")
        countries.add("Malaysia")
        countries.add("Maldives")
        countries.add("Mali")
        countries.add("Malta")
        countries.add("Marshall Islands")
        countries.add("Mauritania")
        countries.add("Mauritius")
        countries.add("Mexico")
        countries.add("Micronesia")
        countries.add("Moldova")
        countries.add("Monaco")
        countries.add("Mongolia")
        countries.add("Montenegro")
        countries.add("Morocco")
        countries.add("Mozambique")
        countries.add("Myanmar (Burma)")
        countries.add("Namibia")
        countries.add("Nauru")
        countries.add("Nepal")
        countries.add("Netherlands")
        countries.add("New Zealand")
        countries.add("Nicaragua")
        countries.add("Niger")
        countries.add("Nigeria")
        countries.add("North Korea")
        countries.add("Norway")
        countries.add("Oman")
        countries.add("Pakistan")
        countries.add("Palau")
        countries.add("Palestine")
        countries.add("Panama")
        countries.add("Papua New Guinea")
        countries.add("Paraguay")
        countries.add("Peru")
        countries.add("Philippines")
        countries.add("Poland")
        countries.add("Portugal")
        countries.add("Qatar")
        countries.add("Romania")
        countries.add("Russia")
        countries.add("Rwanda")
        countries.add("Saint Kitts and Nevis")
        countries.add("Saint Lucia")
        countries.add("Saint Vincent and the Grenadines")
        countries.add("Samoa")
        countries.add("San Marino")
        countries.add("Sao Tome and Principe")
        countries.add("Saudi Arabia")
        countries.add("Senegal")
        countries.add("Serbia")
        countries.add("Seychelles")
        countries.add("Sierra Leone")
        countries.add("Singapore")
        countries.add("Slovakia")
        countries.add("Slovenia")
        countries.add("Solomon Islands")
        countries.add("Somalia")
        countries.add("South Africa")
        countries.add("South Korea")
        countries.add("South Sudan")
        countries.add("Spain")
        countries.add("Sri Lanka")
        countries.add("Sudan")
        countries.add("Suriname")
        countries.add("Swaziland")
        countries.add("Sweden")
        countries.add("Switzerland")
        countries.add("Syria")
        countries.add("Taiwan")
        countries.add("Tajikistan")
        countries.add("Tanzania")
        countries.add("Thailand")
        countries.add("Timor-Leste")
        countries.add("Togo")
        countries.add("Tonga")
        countries.add("Trinidad and Tobago")
        countries.add("Tunisia")
        countries.add("Turkey")
        countries.add("Turkmenistan")
        countries.add("Tuvalu")
        countries.add("Uganda")
        countries.add("Ukraine")
        countries.add("United Arab Emirates (UAE)")
        countries.add("United Kingdom (UK)")
        countries.add("United States of America (USA)")
        countries.add("Uruguay")
        countries.add("Uzbekistan")
        countries.add("Vanuatu")
        countries.add("Vatican City (Holy See)")
        countries.add("Venezuela")
        countries.add("Vietnam")
        countries.add("Yemen")
        countries.add("Zambia")
        countries.add("Zimbabwe")
        displayList.addAll(countries)
    }
}