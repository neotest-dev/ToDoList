package com.app.todolist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Base_Theme_ToDoList)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                createAccount(emailEditText.text.toString(), passwordEditText.text.toString())
            } else {
                Toast.makeText(this, "Rellene los campos", Toast.LENGTH_SHORT).show()
            }
        }
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(emailEditText.text.toString(), passwordEditText.text.toString())
            } else {
                Toast.makeText(this, "Rellene los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Bienvenido",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Usuario ya existente",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Bienvenido",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Datos incorrectos",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            val emailEditText = findViewById<EditText>(R.id.emailEditText)
            val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
            emailEditText.text.clear()
            passwordEditText.text.clear()
            emailEditText.requestFocus()
        }
    }

    private fun reload() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updatedUser = FirebaseAuth.getInstance().currentUser
                updateUI(updatedUser)
            } else {
                Toast.makeText(this, "Error al recargar la información del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}