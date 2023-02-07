@file:Suppress("DEPRECATION")

package com.example.ambulance

import android.os.Bundle


import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import io.reactivex.annotations.Nullable

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.ambulance.AFragment

class HomeActivity: AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_view)


        drawerLayout= findViewById(R.id.drawerr)
        navigationView= findViewById(R.id.navigationVies)
        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toogle: ActionBarDrawerToggle =  ActionBarDrawerToggle(this,drawerLayout , R.string.drawer_open, R.string.drawer_close )
        drawerLayout?.addDrawerListener(toogle)
        toogle.syncState()

navigationView?.setNavigationItemSelectedListener(this)

}
    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.driver_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.driversettings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun displayScreen(id: Int){

        // val fragment =  when (id){

        when (id){
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.container, Fragment() ).commit()
            }


        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        displayScreen(item.itemId)

        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
}

