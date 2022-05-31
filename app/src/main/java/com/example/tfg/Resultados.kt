package com.example.tfg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Resultados : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados)

        val tVpuntos = findViewById<TextView>(R.id.tVPuntos)
        val tVpuntos3 = findViewById<TextView>(R.id.tVPuntos3)
        val tVcorrectas = findViewById<TextView>(R.id.tVCorrectas)
        val tVincorrectas = findViewById<TextView>(R.id.tVIncorrectas)
        val tVnoContestadas = findViewById<TextView>(R.id.tVNC)

        val extras = intent.extras
        val total = extras.getInt("total")
        val correctas = extras.getInt("correctas")
        val incorrectas = extras.getInt("incorrectas")
        val noContestadas = extras.getInt("noContestadas")

        println(total)
        println(correctas)
        println(incorrectas)
        println(noContestadas)

        tVcorrectas.append(" $correctas")
        tVincorrectas.append(" $incorrectas")
        tVnoContestadas.append(" $noContestadas")

        tVpuntos.append("$correctas")
        tVpuntos3.append(" $total")

    }

    fun volver(v: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}