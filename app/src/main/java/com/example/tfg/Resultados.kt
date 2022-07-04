package com.example.tfg

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round


class Resultados : AppCompatActivity() {

    private lateinit var database: DatabaseReference

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

        tVcorrectas.append(" $correctas")
        tVincorrectas.append(" $incorrectas")
        tVnoContestadas.append(" $noContestadas")

        tVpuntos.append("$correctas")
        tVpuntos3.append(" $total")

    }

    private fun fieldDatabase(total: Int, correctas: Int, incorrectas: Int, noContestadas: Int, tiempo: Double) {

        val myPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val email = myPreferences.getString("email", "empty")
        val nombrePDF= myPreferences.getString("nombrePDF", "empty")

        val splitEmail = email.split("@")
        val finalEmail = splitEmail[0]

        database = Firebase.database.reference
        if (nombrePDF != "empty" && email != "empty") {

            var vecesHecho : Int =1

            database.child(finalEmail).child(nombrePDF).child("vecesHecho").get().addOnSuccessListener {

                if(it.value == null){
                    database.child(finalEmail).child(nombrePDF).child("vecesHecho").setValue(1)
                }else{
                    vecesHecho = it.value.toString().toInt()
                    vecesHecho += 1
                    database.child(finalEmail).child(nombrePDF).child("vecesHecho").setValue(vecesHecho)
                }
            }

            database.child(finalEmail).child(nombrePDF).child("aciertosTotal").get().addOnSuccessListener{
                if(it.value == null){
                    database.child(finalEmail).child(nombrePDF).child("aciertosTotal").setValue(correctas)
                    val porcentaje: Double = ((correctas.toDouble()*100)/total)
                    val roundPorcentaje = BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_EVEN)
                    database.child(finalEmail).child(nombrePDF).child("aciertosPorcentaje").setValue(roundPorcentaje.toDouble())
                }else{
                    var aciertosTotal = it.value.toString().toInt()
                    aciertosTotal += correctas
                    database.child(finalEmail).child(nombrePDF).child("aciertosTotal").setValue(aciertosTotal)

                    val totalPosiblesAciertos = vecesHecho*total
                    val porcentaje: Double = ((aciertosTotal.toDouble()*100)/totalPosiblesAciertos)
                    val roundPorcentaje = BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_EVEN)
                    database.child(finalEmail).child(nombrePDF).child("aciertosPorcentaje").setValue(roundPorcentaje.toDouble())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("fallosTotal").get().addOnSuccessListener{
                if(it.value == null){
                    database.child(finalEmail).child(nombrePDF).child("fallosTotal").setValue(incorrectas)
                    val porcentaje: Double = ((incorrectas.toDouble()*100)/total)
                    val roundPorcentaje = BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_EVEN)
                    database.child(finalEmail).child(nombrePDF).child("fallosPorcentaje").setValue(roundPorcentaje.toDouble())
                }else{
                    var fallosTotal = it.value.toString().toInt()
                    fallosTotal += incorrectas
                    database.child(finalEmail).child(nombrePDF).child("fallosTotal").setValue(fallosTotal)

                    val totalPosiblesfallos = vecesHecho*total
                    val porcentaje: Double = ((fallosTotal.toDouble()*100)/totalPosiblesfallos)
                    val roundPorcentaje = BigDecimal(porcentaje).setScale(2, RoundingMode.HALF_EVEN)
                    database.child(finalEmail).child(nombrePDF).child("fallosPorcentaje").setValue(roundPorcentaje.toDouble())
                }
            }

            database.child(finalEmail).child(nombrePDF).child("tiempoMedia").get().addOnSuccessListener{
                if(it.value == null){
                    database.child(finalEmail).child(nombrePDF).child("tiempoTotal").setValue(tiempo)
                    database.child(finalEmail).child(nombrePDF).child("tiempoMedia").setValue(tiempo)
                }else{
                    var tiempoTotal = it.value.toString().toDouble()
                    tiempoTotal += tiempo
                    var tiempoMedia = tiempoTotal/vecesHecho
                    database.child(finalEmail).child(nombrePDF).child("tiempoTotal").setValue(tiempoTotal)
                    database.child(finalEmail).child(nombrePDF).child("tiempoMedia").setValue(tiempoMedia)
                }
            }

            database.child(finalEmail).child(nombrePDF).child("preguntas").get().addOnSuccessListener {
                database.child(finalEmail).child(nombrePDF).child("preguntas").setValue(total)
            }
            database.child(finalEmail).child(nombrePDF).child("correctas").get().addOnSuccessListener {
                database.child(finalEmail).child(nombrePDF).child("correctas").setValue(correctas)
            }
            database.child(finalEmail).child(nombrePDF).child("incorrectas").get().addOnSuccessListener {
                database.child(finalEmail).child(nombrePDF).child("incorrectas").setValue(incorrectas)
            }
            database.child(finalEmail).child(nombrePDF).child("noContestadas").get().addOnSuccessListener {
                database.child(finalEmail).child(nombrePDF).child("noContestadas").setValue(noContestadas)
            }
            database.child(finalEmail).child(nombrePDF).child("tiempo").get().addOnSuccessListener {
                database.child(finalEmail).child(nombrePDF).child("tiempo").setValue(tiempo)
            }
        }
    }

    @Throws(FileNotFoundException::class)
    private fun subirArchivo() {
        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val email = myPreferences.getString("email", "empty")
        if (email != null && email != "empty") {
            val rutaGlobal = myPreferences.getString("rutaGlobal", "empty")
            val nombrePDF = myPreferences.getString("nombrePDF", "empty")

            val archivo = File(rutaGlobal)
            val mStorage = FirebaseStorage.getInstance().reference

            val uri = mStorage.child(email).child(nombrePDF)
            val filepath = Uri.fromFile(archivo)
            uri.putFile(filepath)
        }
    }

    fun volver(v: View){

        val extras = intent.extras
        val total = extras.getInt("total")
        val correctas = extras.getInt("correctas")
        val incorrectas = extras.getInt("incorrectas")
        val noContestadas = extras.getInt("noContestadas")
        val tiempo = extras.getDouble("tiempo")
        val metodo = extras.getString("metodo")

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val email = myPreferences.getString("email", "empty")
        if (email != null && email != "empty") {
            if(metodo != null && metodo=="local"){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("¿Subir pdf?")
                builder.setMessage("Si acepta se subira el pdf a su nube, y tendrá acceso a estadísticas")
                builder.setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        subirArchivo()
                        fieldDatabase(total, correctas, incorrectas, noContestadas, tiempo)
                        backHome()
                    })
                builder.setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        backHome()
                    })
                builder.show()
            }else if (metodo=="nube"){
                fieldDatabase(total, correctas, incorrectas, noContestadas, tiempo)
                backHome()
            }
            else{
                backHome()
            }
        }else{
            backHome()
        }
    }

    private fun backHome(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}