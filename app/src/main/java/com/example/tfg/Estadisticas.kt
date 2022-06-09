package com.example.tfg

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal
import java.math.RoundingMode


class Estadisticas : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    lateinit var tVveces: TextView
    lateinit var tVpreguntas: TextView
    lateinit var tVaciertos: TextView
    lateinit var tVfallos: TextView
    lateinit var tVtiempo: TextView

    lateinit var tVaciertosUltimo: TextView
    lateinit var tVfallosUltimo: TextView
    lateinit var tVnoContestadas: TextView
    lateinit var tVpreguntasUltimo: TextView
    lateinit var tVtiempoUltimo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        tVveces = findViewById<TextView>(R.id.veces)
        tVpreguntas = findViewById<TextView>(R.id.preguntas)
        tVaciertos = findViewById<TextView>(R.id.aciertos)
        tVfallos = findViewById<TextView>(R.id.fallos)
        tVtiempo = findViewById<TextView>(R.id.tiempo)

        tVaciertosUltimo = findViewById<TextView>(R.id.aciertosUltimo)
        tVfallosUltimo = findViewById<TextView>(R.id.fallosUltimo)
        tVnoContestadas = findViewById<TextView>(R.id.noContestadas)
        tVpreguntasUltimo = findViewById<TextView>(R.id.preguntasUltimo)
        tVtiempoUltimo = findViewById<TextView>(R.id.tiempoUltimo)

        fieldDatabase()

    }

    fun hacerTest(v: View) {

        val extras = intent.extras
        val ruta = extras.getString("ruta")
        val imagen = extras.getString("imagen")
        val titulo = extras.getString("titulo")
        val metodo = extras.getString("metodo")

        val intent = Intent(this, HacerTestNube::class.java)
        intent.putExtra("ruta", ruta)
        intent.putExtra("imagen", imagen)
        intent.putExtra("titulo", titulo)
        intent.putExtra("metodo", metodo)
        startActivity(intent)
    }

    fun fieldDatabase(){
        val myPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val email: String = myPreferences.getString("email", "pruebas")
        val nombrePDF : String = myPreferences.getString("nombrePDF", "empty")

        var splitEmail = email.split("@")
        var finalEmail = splitEmail[0]


        database = Firebase.database.reference
        if (nombrePDF != null) {

            database.child(finalEmail).child(nombrePDF).child("vecesHecho").get().addOnSuccessListener {
                if(it.value == null){
                    tVveces.append("0")
                }else{
                    tVveces.append(it.value.toString())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("aciertosPorcentaje").get().addOnSuccessListener{
                if(it.value == null){
                    tVaciertos.append("0%")
                }else{
                    tVaciertos.append(it.value.toString() + "%")
                }
            }

            database.child(finalEmail).child(nombrePDF).child("fallosPorcentaje").get().addOnSuccessListener{
                if(it.value == null){
                    tVfallos.append("0%")
                }else{
                    tVfallos.append(it.value.toString() + "%")
                }
            }

            database.child(finalEmail).child(nombrePDF).child("tiempoMedia").get().addOnSuccessListener{
                if(it.value == null){
                    tVtiempo.append("0")
                }else{
                    val time = it.value.toString().toDouble()
                    if(time>=60){
                        val min = (time/60).toInt()
                        val seg = (time%60).toInt()
                        tVtiempo.append("$min min $seg seg")
                    }else{
                        val seg = time.toInt()
                        tVtiempo.append("$seg seg")
                    }
                }
            }

            database.child(finalEmail).child(nombrePDF).child("preguntas").get().addOnSuccessListener{
                if(it.value == null){
                    tVpreguntas.append("0")
                    tVpreguntasUltimo.append("0")
                }else{
                    tVpreguntas.append(it.value.toString())
                    tVpreguntasUltimo.append(it.value.toString())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("correctas").get().addOnSuccessListener{
                if(it.value == null){
                    tVaciertosUltimo.append("0")
                }else{
                    tVaciertosUltimo.append(it.value.toString())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("incorrectas").get().addOnSuccessListener{
                if(it.value == null){
                    tVfallosUltimo.append("0")
                }else{
                    tVfallosUltimo.append(it.value.toString())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("noContestadas").get().addOnSuccessListener{
                if(it.value == null){
                    tVnoContestadas.append("0")
                }else{
                    tVnoContestadas.append(it.value.toString())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("tiempo").get().addOnSuccessListener{
                if(it.value == null){
                    tVtiempoUltimo.append("0")
                }else{
                    val time = it.value.toString().toDouble()
                    if(time>=60){
                        val min = (time/60).toInt()
                        val seg = (time%60).toInt()
                        tVtiempoUltimo.append("$min min $seg seg")
                    }else{
                        val seg = time.toInt()
                        tVtiempoUltimo.append("$seg seg")
                    }
                }
            }
        }
    }
}