package com.example.ambulance

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView

class SosActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sospage)

        val closebtn = findViewById<Button>(R.id.closebutton)
        closebtn.setOnClickListener {
            val myIntent = Intent(this@SosActivity, HomeActivity::class.java)
            startActivity(myIntent)


        }
    }
}