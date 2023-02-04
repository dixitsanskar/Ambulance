package com.example.ambulance

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Result
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.maps.model.Marker
import java.text.DecimalFormat

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, LocationListener, OnMarkerClickListener,
    ResultCallback<Any?> {
    var mMap: GoogleMap? = null
    var client: GoogleApiClient? = null
    var request: LocationRequest? = null
    var latLngCurrentuserLocation: LatLng? = null
    var auth: FirebaseAuth? = null
    var hashMap: HashMap<String?, Marker>? = null
    var driver_profile = false
    var user_profile = false
    var updateLatLng: LatLng? = null
    var referenceDrivers: DatabaseReference? = null
    var referenceUsers: DatabaseReference? = null
    var scheduleReference: DatabaseReference? = null
    var textName: TextView? = null
    var textEmail: TextView? = null

    @BindView(R.id.adViewNavigation)
    var adView: AdView? = null
    var requestQueue: RequestQueue? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_navigation)
        ButterKnife.bind(this)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        requestQueue = Volley.newRequestQueue(this)
        adView.loadAd(
            Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, GDPR.getBundleAd(this)).build()
        )
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        val navigationView: NavigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        val header: View = navigationView.getHeaderView(0)
        textName = header.findViewById<View>(R.id.title_text) as TextView
        textEmail = header.findViewById<View>(R.id.email_text) as TextView
        val mapFragment: SupportMapFragment = getSupportFragmentManager()
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        referenceDrivers = FirebaseDatabase.getInstance().getReference().child("Drivers")
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("Users")
        scheduleReference =
            FirebaseDatabase.getInstance().getReference().child("uploads").child("0")
        hashMap = HashMap()
        referenceDrivers.addListenerForSingleValueEvent(object : ValueEventListener() {
            fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: FirebaseUser = auth.getCurrentUser()
                if (dataSnapshot.child(user.getUid()).child("lat").exists()) {
                    driver_profile = true
                    val driver_name: String =
                        dataSnapshot.child(user.getUid()).child("name").getValue(
                            String::class.java
                        )
                    val driver_email: String =
                        dataSnapshot.child(user.getUid()).child("email").getValue(
                            String::class.java
                        )
                    textName.setText(driver_name)
                    textEmail.setText(driver_email)
                    navigationView.getMenu().clear()
                    navigationView.inflateMenu(R.menu.driver_menu)
                } else {
                    user_profile = true
                    referenceUsers.addListenerForSingleValueEvent(object : ValueEventListener() {
                        fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user1: FirebaseUser = auth.getCurrentUser()
                            val user_name: String =
                                dataSnapshot.child(user1.getUid()).child("name").getValue(
                                    String::class.java
                                )
                            val user_email: String =
                                dataSnapshot.child(user1.getUid()).child("email").getValue(
                                    String::class.java
                                )
                            textName.setText(user_name)
                            textEmail.setText(user_email)
                            FirebaseMessaging.getInstance().subscribeToTopic("news")
                            navigationView.getMenu().clear()
                            navigationView.inflateMenu(R.menu.user_menu)
                        }


                        fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(
                                getApplicationContext(),
                                databaseError.getMessage(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }

            fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    getApplicationContext(),
                    databaseError.getMessage(),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        referenceDrivers.addChildEventListener(object : ChildEventListener() {
            fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val name: String = dataSnapshot.child("name").getValue(String::class.java)
                    val lat: String = dataSnapshot.child("lat").getValue(String::class.java)
                    val lng: String = dataSnapshot.child("lng").getValue(String::class.java)
                    val vehicle_number: String = dataSnapshot.child("vehiclenumber").getValue(
                        String::class.java
                    )
                    val latlng = LatLng(lat.toDouble(), lng.toDouble())
                    val markerOptions = MarkerOptions()
                    markerOptions.title(name)
                    markerOptions.snippet("Van number: $vehicle_number")
                    markerOptions.position(latlng)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mynewbusicon))
                    val myMarker: Marker = mMap.addMarker(markerOptions)
                    hashMap!![myMarker.title] = myMarker
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val name: String = dataSnapshot.child("name").getValue().toString()
                    val lat: String = dataSnapshot.child("lat").getValue().toString()
                    val lng: String = dataSnapshot.child("lng").getValue().toString()
                    updateLatLng = LatLng(lat.toDouble(), lng.toDouble())
                    val marker = hashMap!![name]
                    if (marker != null) {
                        marker.position = updateLatLng
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fun onChildRemoved(dataSnapshot: DataSnapshot?) {}
            fun onChildMoved(dataSnapshot: DataSnapshot?, s: String?) {}
            fun onCancelled(databaseError: DatabaseError?) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        // Add a marker in Sydney and move the camera
        client = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addOnConnectionFailedListener(this)
            .addConnectionCallbacks(this)
            .build()
        client.connect()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val marker_Pos: LatLng = marker.position
        val distance = CalculationByDistance(latLngCurrentuserLocation, marker_Pos)
        val df = DecimalFormat("#.##")
        val dist = df.format(distance)
        Toast.makeText(getApplicationContext(), "$dist KM far.", Toast.LENGTH_SHORT).show()

        //      marker.setSnippet(dist + " KM far.");
        val sb: StringBuilder
        val dataTransfer = arrayOfNulls<Any>(5)
        sb = StringBuilder()
        sb.append("https://maps.googleapis.com/maps/api/directions/json?")
        sb.append("origin=" + marker_Pos.latitude + "," + marker_Pos.longitude)
        sb.append("&destination=" + latLngCurrentuserLocation.latitude + "," + latLngCurrentuserLocation.longitude)
        sb.append("&key=" + "AIzaSyCsThl1-hAeG2EscPb69ii0hdSXkUJ6-x0")
        val getDirectionsData = DirectionAsync(getApplicationContext())
        dataTransfer[0] = mMap
        dataTransfer[1] = sb.toString()
        dataTransfer[2] = LatLng(marker_Pos.latitude, marker_Pos.longitude)
        dataTransfer[3] =
            LatLng(latLngCurrentuserLocation.latitude, latLngCurrentuserLocation.longitude)
        dataTransfer[4] = marker
        getDirectionsData.execute(*dataTransfer)
        return true
    }

    private fun CalculationByDistance(start: LatLng?, end: LatLng): Double {
        val Radius = 6371 //radius of earth in Km
        val lat1: Double = start.latitude
        val lat2: Double = end.latitude
        val lon1: Double = start.longitude
        val lon2: Double = end.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        return meter
    }

    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id: Int = item.getItemId()
        if (driver_profile) {
            if (id == R.id.nav_signout) {
                if (auth != null) {
                    auth.signOut()
                    finish()
                    val myIntent = Intent(this@NavigationActivity, MainActivity::class.java)
                    startActivity(myIntent)
                }
            } else if (id == R.id.nav_share_Location) {
                if (isServiceRunning(getApplicationContext(), LocationShareService::class.java)) {
                    Toast.makeText(
                        getApplicationContext(),
                        "You are already sharing your location.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (driver_profile) {
                    val myIntent = Intent(this@NavigationActivity, LocationShareService::class.java)
                    startService(myIntent)
                } else {
                    Toast.makeText(
                        getApplicationContext(),
                        "Only driver can share location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (id == R.id.nav_stop_Location) {
                val myIntent2 = Intent(this@NavigationActivity, LocationShareService::class.java)
                stopService(myIntent2)
            }
            //            else if(id == R.id.nav_send_fcm)
//            {
//                if(driver_profile)
//                {
//             //       openDialog();
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(),"Only drivers can send notifications",Toast.LENGTH_LONG).show();
//                }
//            }
        } else {
            if (id == R.id.nav_signout_user) {
                if (auth != null) {
                    auth.signOut()
                    finish()
                    val myIntent = Intent(this@NavigationActivity, MainActivity::class.java)
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(myIntent)
                }
            }
        }
        val drawer: DrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun isServiceRunning(c: Context, serviceClass: Class<*>): Boolean {
        val activityManager: ActivityManager =
            c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services: List<ActivityManager.RunningServiceInfo> = activityManager.getRunningServices(
            Int.MAX_VALUE
        )
        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.getClassName() == serviceClass.name) {
                return true
            }
        }
        return false
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onConnectionSuspended(i: Int) {}
    @SuppressLint("RestrictedApi")
    override fun onConnected(bundle: Bundle?) {
        request = LocationRequest().create()
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        request.setInterval(5000)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            return
        }
        val builder: LocationSettingsRequest.Builder = Builder()
            .addLocationRequest(request)
        builder.setAlwaysShow(true)
        val result: PendingResult<*> = LocationServices.SettingsApi.checkLocationSettings(
            client,
            builder.build()
        )
        result.setResultCallback(this) // dialog for location
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this)
    }

    fun onLocationChanged(location: Location?) {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this)
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Could not find location", Toast.LENGTH_SHORT)
                .show()
        } else {
            latLngCurrentuserLocation = LatLng(location.getLatitude(), location.getLongitude())
            mMap.addMarker(
                MarkerOptions().position(latLngCurrentuserLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            ).setVisible(true)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrentuserLocation, 15f))
        }
    }

    override fun onResult(result: Result) {
        val status = result.status
        when (status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {}
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                 //  GPS turned off, Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this@NavigationActivity, 202)
                } catch (e: SendIntentException) {

                    //failed to show dialog
                }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
        }
    }
}