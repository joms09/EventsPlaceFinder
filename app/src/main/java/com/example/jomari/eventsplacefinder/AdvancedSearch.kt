package com.example.jomari.eventsplacefinder

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.advance_search.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter
import android.widget.Spinner

private const val PERMISSION_REQUEST = 10

class AdvancedSearch : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    lateinit var getLocation: Button
    lateinit var pickStartDate: Button
    lateinit var pickEndDate: Button
    lateinit var pickStartTime: Button
    lateinit var pickEndTime: Button
    lateinit var capacity: EditText
    lateinit var eventType: Spinner
    lateinit var miniBudget: EditText
    lateinit var mProgressbar: ProgressDialog
    lateinit var maxBudget: EditText

    var placeid : String = ""
    var name : String = ""
    var status : String = ""
    var type : String = ""
    var address : String = ""
    var count : Int = 0
    var image : String = ""

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    val PERMISSION_CODE = 1000
    var image_uri: Uri? = null
    private val IMAGE_CAPTURE_CODE = 1001
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advance_search)

        mProgressbar = ProgressDialog(this)

        placeid = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        status = intent.getStringExtra("status")
        type = intent.getStringExtra("type")
        address = intent.getStringExtra("address")
        count = intent.getIntExtra("count", 0)
        image = intent.getStringExtra("image")

//        getLocationBtn.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
//                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
//                ) {
//                    //permission was not enabled
//                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    requestPermissions(permission, PERMISSION_CODE)
//                } else {
//                    //permission already granted
//                    openCamera()
//                }
//            } else {
//                //system os is < marshmallow
//                openCamera()
//            }
//        }


        getLocation = findViewById(R.id.btn_get_location)
        pickStartDate = findViewById(R.id.pickStartDateBtn)
        pickStartTime = findViewById(R.id.timeStartBtn)
        capacity = findViewById(R.id.capacity)
        eventType = findViewById(R.id.eventType)
        miniBudget = findViewById(R.id.miniBudget)

        submitBtn.setOnClickListener {
            submitForm()
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        pickStartDate.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
                pickStartDate.text = "$mDay/${mMonth.plus(1)}/$mYear"
            }, year, month, day)
            dpd.show()
        }

        pickStartTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                pickStartTime.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
            ).show()
        }

        val spinner: Spinner?

        spinner = this.eventType
        spinner.onItemSelectedListener = this

        val eventtypes = arrayOf(
            "Birthday",
            "Wedding",
            "Corporate",
            "Party",
            "Resto",
            "Sports",
            "Workshop",
            "Shoots",
            "Seminar",
            "Others"
        )


        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventtypes)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner.adapter = aa

        miniBudget.addTextChangedListener(onTextChangedListener())
        capacity.addTextChangedListener(this.onTextChangedListener2())


        disableView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }


    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    private fun disableView() {
        btn_get_location.isEnabled = false
        btn_get_location.alpha = 0.5F
    }

    private fun enableView() {
        btn_get_location.isEnabled = true
        btn_get_location.alpha = 1F
        btn_get_location.setOnClickListener {
            when {
                tv_result.text.isEmpty() -> {
                    btn_get_location.isEnabled = true
                    getLocation()
                    btn_get_location.isEnabled = false
                }
                else -> {
                    btn_get_location.isEnabled = false
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            mProgressbar.setMessage("Please wait..")
                            mProgressbar.show()
                            tv_result.append("\nGPS ")
                            tv_result.append("\nLatitude : " + locationGps!!.latitude)
                            tv_result.append("\nLongitude : " + locationGps!!.longitude)
                            Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                            Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                            mProgressbar.dismiss()
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 600000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            mProgressbar.setMessage("Please wait..")
                            mProgressbar.show()
                            tv_result.append("\nNetwork ")
                            tv_result.append("\nLatitude : " + locationNetwork!!.latitude)
                            tv_result.append("\nLongitude : " + locationNetwork!!.longitude)
                            Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                            Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                            mProgressbar.dismiss()
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if (locationGps != null && locationNetwork != null) {
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                    mProgressbar.setMessage("Please wait..")
                    mProgressbar.show()
                    tv_result.append("\nNetwork ")
                    tv_result.append("\nLatitude : " + locationNetwork!!.latitude)
                    tv_result.append("\nLongitude : " + locationNetwork!!.longitude)
                    Log.d("CodeAndroidLocation", " Network Latitude : " + locationNetwork!!.latitude)
                    Log.d("CodeAndroidLocation", " Network Longitude : " + locationNetwork!!.longitude)
                    mProgressbar.dismiss()
                } else {
                    mProgressbar.setMessage("Please wait..")
                    mProgressbar.show()
                    tv_result.append("\nGPS ")
                    tv_result.append("\nLatitude : " + locationGps!!.latitude)
                    tv_result.append("\nLongitude : " + locationGps!!.longitude)
                    Log.d("CodeAndroidLocation", " GPS Latitude : " + locationGps!!.latitude)
                    Log.d("CodeAndroidLocation", " GPS Longitude : " + locationGps!!.longitude)
                    mProgressbar.dismiss()
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Location")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()

        }

        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission pop up was granted
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view
            //image_view.setImageURI(image_uri)
        }
    }


    private fun onTextChangedListener(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                miniBudget.removeTextChangedListener(this)


                try {
                    var originalString = s.toString()

                    val longval: Long?
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = java.lang.Long.parseLong(originalString)

                    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString = formatter.format(longval)

                    //setting text after format to EditText
                    miniBudget.setText(formattedString)
                    miniBudget.setSelection(miniBudget.text.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                miniBudget.addTextChangedListener(this)
            }
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

    private fun onTextChangedListener2(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                capacity.removeTextChangedListener(this)


                try {
                    var originalString = s.toString()

                    val longval: Long?
                    if (originalString.contains(",")) {
                        originalString = originalString.replace(",".toRegex(), "")
                    }
                    longval = java.lang.Long.parseLong(originalString)

                    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString = formatter.format(longval)

                    //setting text after format to EditText
                    capacity.setText(formattedString)
                    capacity.setSelection(capacity.text.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                capacity.addTextChangedListener(this)
            }
        }
    }


    private fun submitForm() {
        val location = tv_result.text.toString().trim()
        val pickstartdate = pickStartDate.text.toString().trim()
        val pickstarttime = pickStartTime.text.toString().trim()
        val capacity1 = capacity.text.toString().trim()
        val event = eventType.selectedItem.toString().trim()
        val minibudget = miniBudget.text.toString().trim()

        when {
            location.isEmpty() -> {
                tv_result.text = getString(R.string.plsgeturlocation)
                tv_result.setTextColor(Color.RED)
                return
            }
            capacity1.isEmpty() -> {
                capacity.error = "Please enter capacity"
                return
            }
            minibudget.isEmpty() -> {
                miniBudget.error = "Please enter minimum budget"
                return
            }
            else -> {
                val intent = Intent(this, AdvancedSearchResult::class.java)

                intent.putExtra("id", placeid)
                intent.putExtra("name", name)
                intent.putExtra("status", status)
                intent.putExtra("type", type)
                intent.putExtra("address", address)
                intent.putExtra("count", count)
                intent.putExtra("image", image)

                intent.putExtra("location", location)
                intent.putExtra("pickstartdate", pickstartdate)
                intent.putExtra("pickstarttime", pickstarttime)
                intent.putExtra("capacity1", capacity1)
                intent.putExtra("event", event)
                intent.putExtra("minibudget", minibudget)
                val mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Searching!")
                mProgressbar.setMessage("Please wait..")
                mProgressbar.show()
                Handler().postDelayed({
                }, 1500)
                mProgressbar.dismiss()
                startActivity(intent)
            }
        }
    }
}


