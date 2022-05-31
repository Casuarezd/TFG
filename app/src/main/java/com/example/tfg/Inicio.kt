package com.example.tfg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
    }

    fun omitir(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun registrar(v: View) {
        val email = findViewById<TextView>(R.id.emailTV)
        val password = findViewById<TextView>(R.id.passwordTV)

        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome()
                    } else {
                        showAlert()
                    }
                }
        }
    }

    fun login(v: View) {
        val email = findViewById<TextView>(R.id.emailTV)
        val password = findViewById<TextView>(R.id.passwordTV)

        if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome()
                    } else {
                        showAlert()
                    }
                }
        }
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}