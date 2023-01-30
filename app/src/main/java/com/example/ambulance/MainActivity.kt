package com.example.ambulance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var user : FirebaseAuth?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance()

        if(user == null){
            setContentView(R.layout.mainactivity)

        }
        else
        {
            val myIntent = Intent(this@MainActivity, NavigationActivity::class.java)
            startActivity(myIntent)
            finish()
        }


    }


}