package com.example.ambulance

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
@Suppress( "DEPRECATION")
class LoginActivity : AppCompatActivity(){
    @BindView(R.id.editTextUserEmail)
    var editTextUserEmail: EditText? = null
    @BindView(R.id.editTextUserPassword)
    var editTextUserPassword: EditText? = null
    @BindView(R.id.userToolbar)
    var toolbaar: Toolbar? = null

    var auth: FirebaseAuth? = null
    var dialog: ProgressDialog? = null
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_login)
         ButterKnife.bind(this)
         toolbaar?.title = "Login"
         setSupportActionBar(toolbaar)
         auth = FirebaseAuth.getInstance()
         dialog = ProgressDialog(this)
         val btnLogin = findViewById<Button>(R.id.btnlogin)
         btnLogin.setOnClickListener {
             dialog?.setMessage("Logging in. Please Wait.")
             dialog?.show()
             if (editTextUserEmail?.text.toString()
                     .isEmpty() || editTextUserPassword?.text.toString().isEmpty()
             ) {
                 Toast.makeText(applicationContext, "Blank fields not allowed.", Toast.LENGTH_SHORT)
                     .show()
                 dialog?.dismiss()

             } else {
                 val newEmail: String = editTextUserEmail?.text.toString().trim()
                 auth?.signInWithEmailAndPassword(
                   "kuruto810@gmail.com",
                    "123456"
                 )
                     ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                         override fun onComplete(task: Task<AuthResult?>) {
                             if (task.isSuccessful) {
                                 dialog?.dismiss()


                                 val loginIntent  = Intent(this@LoginActivity, SosActivity:: class.java )
                                 loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                 startActivity(loginIntent)
                                 finish()
                             } else {
                                 Toast.makeText(
                                     applicationContext,
                                     "Wrong email/password combination. Try again",
                                     Toast.LENGTH_SHORT
                                 ).show()
                                 dialog?.dismiss()

                             }
                         }

                     })
             }

         }

     }}
