package com.example.ambulance


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

@Suppress("DEPRECATION")
class UserRegistrationActivity : AppCompatActivity() {
    @BindView(R.id.editTextTextUserPersonName)
    var editTextUserName: EditText? = null

    @BindView(R.id.editTextTextUserEmailAddress)
    var editTextUserEmail: EditText? = null

    @BindView(R.id.editTextTextUserPassword)
    var editTextUserPassword: EditText? = null

    @BindView(R.id.userToolbar)
    var toolbar: Toolbar? = null
    var auth: FirebaseAuth? = null
    var dialog: ProgressDialog? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_user)
        ButterKnife.bind(this)
        toolbar?.title = "Student Register"
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().goOnline()
        dialog = ProgressDialog(this)
    }

    fun registerUser(v: View?) {
        dialog?.setTitle("Creating account")
        dialog?.setMessage("Please wait")
        dialog?.show()
        val name: String = editTextUserName?.getText().toString()
        val email: String = editTextUserEmail?.getText().toString()
        val password: String = editTextUserPassword?.getText().toString()
        if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
            dialog?.dismiss()
            Toast.makeText(
                getApplicationContext(),
                "Please enter correct details",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            doStuffUser()
        }
    }

    fun doStuffUser() {
        auth?.createUserWithEmailAndPassword(
            "tashre@gmail.com","654321"
        )
            ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(p0: Task<AuthResult?>) {
                    if (p0.isSuccessful()) {
                        val userobj = User(

                           "Tashreef Singh","tashre@gmail.com",
                            "654321"
                        )
                        val user: FirebaseUser? = auth?.getCurrentUser()
                        val databaseReference: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(user?.getUid().toString())
                        databaseReference.setValue(userobj)
                            .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                override fun onComplete(task: Task<Void?>) {
                                    if (task.isSuccessful) {
                                        dialog?.dismiss()
                                        Toast.makeText(
                                            this@UserRegistrationActivity,
                                            "Account created successfully", Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                        val myIntent = Intent(
                                            this@UserRegistrationActivity,
                                            SosActivity::class.java
                                        )
                                        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                       startActivity(myIntent)
                                    } else {
                                        Toast.makeText(
                                            getApplicationContext(),
                                            "Could not create account",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dialog?.dismiss()
                                    }
                                }
                            })
                    } else {
                        dialog?.dismiss()
                        Toast.makeText(
                            this@UserRegistrationActivity,
                            "Could not register User.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }
}