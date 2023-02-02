package com.example.ambulance

import android.location.LocationListener
import android.location.LocationRequest
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import butterknife.ButterKnife
import butterknife.ButterKnife.bind
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.text.DecimalFormat

@Suppress("DEPRECATION")
class NavigationActivity :AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,  GoogleApiClient.OnConnectionFailedListener, LocationListener,
    GoogleMap.OnMarkerClickListener , ResultCallback<Any?>{
        var nmap: GoogleMap? = null
    var client : GoogleApiClient? = null
    var request: LocationRequest? = null
    var latLngCurrentUserLocation: LatLng? = null
    var auth: FirebaseAuth? = null
    var hashMap: HashMap<String?, Marker>? = null
    var driver_profile =false
    var user_profile=false
    var updateLatLng: LatLng?= null
    var referenceDrivers: DatabaseReference? = null
    var referenceUsers: DatabaseReference?=null
    var scheduleReference: DatabaseReference? = null
    var textName: TextView? =null
    var textEmail: TextView?= null
   protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.my_activity_navigation)
       ButterKnife.bind(this)
       val toolbar= findViewById<View>(R.id.toolbar) as Toolbar
       setSupportActionBar(toolbar)
       auth= FirebaseAuth.getInstance()
      val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
       val toogle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
       drawer.setDrawerListener(toogle)
       toogle.syncState()
       val navigationView: NavigationView = findViewById<View>(R.id.nav_view) as NavigationView
       navigationView.setNavigationItemSelectedListener(this)
       val header: View = navigationView.getHeaderView(0)
       textName = header.findViewById<View>(R.id.title_text) as TextView
       textEmail = header.findViewById<View>(R.id.email_text) as TextView
       val mapFragment: SupportMapFragment = getsupportFragmentManager()
           .findFragmentById(R.id.map) as SupportMapFragment
       mapFragment.getMapAsync(this)
       referenceDrivers = FirebaseDatabase.getInstance().getReference().child("Drivers")
       referenceUsers = FirebaseDatabase.getInstance().getReference().child("Users")
       scheduleReference = FirebaseDatabase.getInstance().getReference().child("uploads").child("0")
       hashMap = HashMap()
       referenceDrivers!!.addListenerForSingleValueEvent(object: ValueEventListener(){
           override fun onDataChange(dataSnapshot: DataSnapshot) {
               val user: FirebaseUser? = auth!!.getCurrentUser()
               if(dataSnapshot.child(user.getUid()).child("lat").exists())
               {
                   driver_profile = true
                   val driver_name: String? =
                       dataSnapshot.child(user.getUid()).child("name").getValue(String::class.java)
                   val driver_email: String? =
                       dataSnapshot.child(user.getUid()).child("email").getValue(String::class.java)
                   textName!!.setText(driver_name)
                   textEmail!!.setText(driver_email)
                   navigationView.getMenu().clear()
                   navigationView.inflateMenu(R.menu.driver_menu)
               }
               else
               {
                   user_profile= true
                   referenceUsers!!.addListenerForSingleValueEvent(object : ValueEventListener(){
                       override fun onDataChange(dataSnapshot: DataSnapshot) {
                           val user1: FirebaseUser? = auth!!.getCurrentUser()
                           val user_name: String? =
                               dataSnapshot.child(user1.getUid()).child("name").getValue(String::class.java)
                           val user_email : String? = auth!!.getCurrentUser()
                           dataSnapshot.child(user1.getUid()).child("eamil").getValue(String::class.java)
                           textName!!.setText(user_name)
                           textEmail!!.setText(user_email)
                           FirebaseMessaging.getInstance().subscribeToTopic("news")
                           navigationView.getMenu().clear()
                           navigationView.inflateMenu(R.menu.user_menu)


                       }
                       fun onCancelled(databaseError: DatabaseError)
                       {
                           Toast.makeText(getApplicationContext(),databaseError.message , Toast.LENGTH_SHORT).show()
                       }
                   })
               }


           }
       })
       referenceDrivers!!.addChildEventListener(object : ChildEventListener()
       {
           override fun onChildAdded(dataSnapshot: DataSnapshot, s : String?)
           {
               try{
                   val name: String? = dataSnapshot.child("name").getValue(String::class.java)
                   val lat: String? = dataSnapshot.child("lat").getValue(String::class.java)
                   val lng: String? = dataSnapshot.child("lng").getValue(String::class.java)
                   val vehicle_number: String? = dataSnapshot.child("vehiclenumber").getValue(
                       String::class.java)
                   val latlng = LatLng(lat.toDouble(), lng.toDouble())
                   val markerOptions  = MarkerOptions()
                   markerOptions.title(name)
                   markerOptions.snippet("Van number: $vehicle_number")
                   markerOptions.position(latlng)
                   markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mynewbusicon))
                   val myMarker: Marker? = nmap.addMarker(markerOptions)
                   hashMap!![myMarker.title] = myMarker

               }
               catch (e: java.lang.Exception){
                   e.printStackTrace()
               }

           }
           override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?)
       {
               try {
                   val name: String = dataSnapshot.child("name").getValue().toString()
                    val lat: String = dataSnapshot.child("lat").getValue().toString()
                   val lng: String = dataSnapshot.child("lng").getValue().toString()
                   updateLatLng = LatLng(lat.toDouble(),lng.toDouble())
                   val marker = hashMap!![name]
                   if(marker!=null){
                       marker.position = updateLatLng
                   }

               }catch (e: Exception){
                   e.printStackTrace()
               }
           }
        override fun onChildRemoved(dataSnapshot: DataSnapshot){}
          override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?){}
          override fun onCancelled(databaseError: DatabaseError){}

       }
       )






    }

    override fun onMapReady(googleMap: GoogleMap){
        nmap= googleMap
        nmap!!.setOnMapClickListener { this }
        client= GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addOnConnectionFailedListener { this }
            . addConnectionCallbacks(this)
            .build()
        client!!.connect()
    }
    override fun onMarkerClick(marker: Marker): Boolean {
        val marker_Pos: LatLng = marker.position
        val distance = CalculationByDistance(latLngCurrentUserLocation, marker_Pos)
        val df = DecimalFormat("#.##")
        val dist = df.format(distance)
        Toast.makeText(getApplicationContext(), "$dist KM far.", Toast.LENGTH_SHORT).show()
        val sb: java.lang.StringBuilder
        val dataTransfer = arrayOfNulls<Any>(5)
        sb = StringBuilder()
        sb.append("https://maps.googleapis.com/maps/api/directions/json?")

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }
}