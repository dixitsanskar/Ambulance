package com.example.ambulance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Button

class SosPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sospage)
        val onClickListener = OnClickListener(findViewById<Button>(R.id.closebutton))


    }


}