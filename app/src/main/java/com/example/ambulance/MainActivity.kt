package com.example.ambulance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseAuth
import java.security.Permissions
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var user : FirebaseAuth?= null
    var permissions=  arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance()

        ButterKnife.bind(this)
        checkPermission()
        setContentView(R.layout.mainactivity)

       //if(user!=null)
      //  {
        //    val myIntent = Intent(this@MainActivity, SosActivity::class.java)
          //     startActivity(myIntent)
           // finish()
        //}


    }
    fun registerAsDriver(v: View?)
    {
        val myIntent = Intent(getApplicationContext(), DriverRegistrationActivity::class.java)
        startActivity(myIntent)


    }
    fun login(v: View?)
    {
        val myIntent = Intent(this, LoginActivity::class.java )
        startActivity(myIntent)

    }
    fun registerAsUser(v: View?)
    {
        val myIntent = Intent(getApplicationContext(), UserRegistrationActivity::class.java)
        startActivity(myIntent)

    }
    private fun checkPermission(): Boolean
    {
        var result: Int
        val listPermissionNeeded: MutableList<String> = ArrayList()

        for (p in permissions)
        {
            result = ContextCompat.checkSelfPermission(this, p)
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionNeeded.add(p)

            }
        }
        if(listPermissionNeeded.isNotEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(),100)
            return false

        }
        return true
    }
  override  fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResult: IntArray )
  {
      super.onRequestPermissionsResult(requestCode, permissions, grantResult)
     if (requestCode==100)
     {
         for (i in permissions.indices)
         {
             val grantResult = grantResult[i]
             if(grantResult == PackageManager.PERMISSION_DENIED)
             {
                 if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
                 {
                     requestPermissions(
                         arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                         Manifest.permission.READ_EXTERNAL_STORAGE), 100
                     )
                 }
             }

         }

      }

  }


}