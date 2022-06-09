package com.example.tfg.ui.search

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg.Estadisticas
import com.example.tfg.HacerTestNube
import com.example.tfg.adapter.CustomAdapterNube
import com.example.tfg.databinding.FragmentSearchBinding
import com.example.tfg.model.ModeloListarArchivos
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import java.io.File




class SearchFragment : Fragment(){

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val archivos = mutableListOf<ModeloListarArchivos>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var editText = binding.textEmail
        var button = binding.searchButton
        button.setOnClickListener {
            if(editText.text.isNotEmpty()){
                listarDocumentos(editText.text.toString())
            }else{
                showEmpty()
            }
        }

//    val textView: TextView = binding.textNotifications
//    notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })
        return root
    }

    private fun listarDocumentos(email: String) {

        val mStorage = Firebase.storage
        val lista = mStorage.reference.child(email)

        lista.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    //println(prefix)
                }

                items.forEach { item ->

                    var ruta = item.toString().split("/")
                    var name = ruta[ruta.size-1]

                    var file = File.createTempFile(name, "pdf")
                    item.getFile(file)

                    var uri = Uri.fromFile(file);
                    println("URI FILE")
                    println(uri.path.toString())

                    val aux = ModeloListarArchivos(
                        name,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/PDF_file_icon.svg/1200px-PDF_file_icon.svg.png",
                        uri.path.toString()
                    )
                    archivos.add(aux)
                }
                if(archivos.size==0){
                    showAlert()
                }else{
                    initRecyclerView()
                }
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        var adapter = CustomAdapterNube(archivos)
        binding.recyclerView.adapter = adapter
        //Controlar la pulsación
        adapter.setOnItemClickListener(object : CustomAdapterNube.onItemClickListener {
            override fun onItemClick(position: Int) {

                var myPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val myEditor = myPreferences.edit()
                myEditor.putString("nombrePDF", archivos[position].titulo);
                myEditor.commit();

                val intent = Intent(binding.recyclerView.context, HacerTestNube::class.java)
                intent.putExtra("ruta", archivos[position].ruta)
                intent.putExtra("imagen", archivos[position].imagen)
                intent.putExtra("titulo", archivos[position].titulo)
                intent.putExtra("metodo", "search")
                startActivity(intent)
            }
        })
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Error")
        builder.setMessage("El usuario no existe o no tiene test")
        builder.setPositiveButton("Aceptar", null)
        builder.show()
    }

    fun showEmpty() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Error")
        builder.setMessage("Debe introducir un correo")
        builder.setPositiveButton("Aceptar", null)
        builder.show()
    }
}