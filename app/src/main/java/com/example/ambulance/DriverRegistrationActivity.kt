@file:Suppress("DEPRECATION")

package com.example.ambulance

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class DriverRegistrationActivity : AppCompatActivity() {
    @BindView(R.id.editTextTextPersonName)
    var editTextDriverName: EditText? = null

    @BindView(R.id.editTextTextEmailAddress)
    var editTextDriverEmail: EditText? = null

    @BindView(R.id.editTextTextPassword)
    var editTextDriverPassword: EditText? = null

    @BindView(R.id.editTextTextPersonName2)
    var editTextDriverBus: EditText? = null

    @BindView(R.id.driverToolbar)
    var toolbar: Toolbar? = null

    var auth: FirebaseAuth? = null
    var dialog: ProgressDialog? = null
    var user: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_driver)
        ButterKnife.bind(this)
        toolbar?.title = "Driver Registration"
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(this)

        var btnRegistor = findViewById<Button>(R.id.button3)

        btnRegistor.setOnClickListener {
                dialog?.setTitle("Creating Account")
                dialog?.setMessage("Please wait")
                dialog?.show()
                  val name = editTextDriverName?.text.toString().trim()
                  val email = editTextDriverEmail?.text.toString().trim()
                  val password = editTextDriverPassword?.text.toString().trim()
                if(name.isEmpty() || email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please enter correct details", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss()
                }
                else
                {
                    doAllStuff()
                } }
    }


    private fun doAllStuff() {
        val name: String = editTextDriverName?.text.toString()
        val email: String = editTextDriverEmail?.getText().toString().trim()
        val password: String = editTextDriverPassword?.getText().toString().trim()
        val ambulanceNo: String = editTextDriverBus?.getText().toString().trim()
        auth?.createUserWithEmailAndPassword(
            "kuruto810@gmail.com", "123456")
            ?.addOnCompleteListener( object : OnCompleteListener<AuthResult?>{
                override fun onComplete(p0: Task<AuthResult?>) {

                    if(p0.isSuccessful()) {
                        val driver: com.example.ambulance.Driver =  com.example.ambulance.Driver(
                           "Myby", "kuruto810@gmail.com", "123456", "4321",
                            "33.652037", "73.156598"
                        )
                        user = auth?.currentUser
                        databaseReference =
                            FirebaseDatabase.getInstance().getReference().child("Drivers")
                                .child(user?.getUid().toString())
                        databaseReference?.setValue(driver)
                            ?.addOnCompleteListener(object : OnCompleteListener<Void?>{
                                override fun onComplete(p0: Task<Void?>) {
                                    if (p0.isSuccessful) {
                                        dialog?.dismiss()
                                        Toast.makeText(this@DriverRegistrationActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                        //  val myIntent = Intent(this@DriverRegistrationActivity, SosActivity::class.java)
                                           //  startActivity(myIntent)
                                    } else { Toast.makeText(this@DriverRegistrationActivity, "Could not Register driver", Toast.LENGTH_LONG).show()
                                        dialog?.dismiss()


                                    }
                                }
                            })


                    } else {
                       Toast.makeText(this@DriverRegistrationActivity, "Could not register. "+ p0.exception?.message, Toast.LENGTH_LONG).show()
                      dialog?.dismiss()
                   }
                }
            })
    }
}