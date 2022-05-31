package com.example.tfg.ui.miNube

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase

import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import android.net.Uri
import android.util.Log
import com.example.tfg.HacerTest
import com.example.tfg.HacerTestNube
import com.example.tfg.adapter.CustomAdapterNube
import com.example.tfg.databinding.FragmentNubeBinding
import com.example.tfg.model.ModeloListarArchivos
import com.example.tfg.model.putPDF
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.*


class MiNubeFragment : Fragment() {

    private var _binding: FragmentNubeBinding? = null

    private val binding get() = _binding!!

    private val archivos = mutableListOf<ModeloListarArchivos>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        //dashboardViewModel = ViewModelProvider(this).get(MiNubeViewModel::class.java)

        _binding = FragmentNubeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listarDocumentos()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun uploadPDF() {
//        val mStorage = FirebaseStorage.getInstance().reference
//        println(mStorage.listAll())
//        var files = mStorage.child("pdfs")
//        //var url = files.downloadUrl
//        var archivos = files.listAll()
//        println(archivos)
//    }

    private fun listarDocumentos() {
//        val files = directory.listFiles()
//        if (files != null) {
//            for (file in files) {
//                if (file != null) {
//                    var extension = file.extension
//                    if (file.isFile && extension=="pdf") {
//                        val aux = ModeloListarArchivos(
//                            file.name,
//                            "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/PDF_file_icon.svg/1200px-PDF_file_icon.svg.png",
//                            file.absolutePath
//                        )
//                        archivos.add(aux)
//                    }
//
//                }
//            }
//        }
        val mStorage = Firebase.storage
        val lista = mStorage.reference.child("uri")

//        val mDatabase = FirebaseDatabase.getInstance().reference;
//        mDatabase.child("pruebasGenerales").get().addOnSuccessListener {
//            // var pdfList = it.getValue(ArrayList<putPDF.class>)
//            var lista = it.children
//            lista.forEach { element ->
//                println("Hijo: " + element.value)
//
//                val aux = ModeloListarArchivos(
//                    element.child("name").value.toString(),
//                    "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/PDF_file_icon.svg/1200px-PDF_file_icon.svg.png",
//                    element.child("uri").value.toString()
//                )
//                archivos.add(aux)
//            }
//
//            initRecyclerView()
//            println("SIZE "+ archivos.size)
//
//        }.addOnFailureListener {
//            Log.e("firebase", "Error getting data", it)
//        }


        println("FUNCIONA?")
        lista.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    //println(prefix)
                }

                items.forEach { item ->

                    var ruta = item.toString().split("/")
                    var name = ruta[ruta.size-1]
                    var uriItem = item.downloadUrl
                    println("URI Item")
                    println(uriItem.toString())

//                    var directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    var file = File.createTempFile(name[0], "pdf", directory)
//                    var file = File(name[0])
                    var file = File.createTempFile(name, "pdf")
                    item.getFile(file)

//                    println("Name")
//                    println(name[0]);
//                    println("Path")
//                    println(file.absolutePath);

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
                initRecyclerView()
                println("SIZE "+ archivos.size)
            }
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
        println("SIZE2 "+ archivos.size)
        println("FUNCIONA?")



//        System.out.println("XXXXXXXXXXXXX");
//        //System.out.println(lista.result.prefixes);
//        System.out.println("XXXXXXXXXXXXX");
//
//        var islandRef = mStorage.child("pdfs/Prueba.pdf")
//
//        val ONE_MEGABYTE: Long = 9999 * 9999
//        val task = islandRef.getBytes(ONE_MEGABYTE);
//        System.out.println("HOLA "+ task);

//        val ONE_MEGABYTE: Long = 1024 * 1024
//        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
//            // Data for "images/island.jpg" is returned, use this as needed
//            System.out.println()
//        }.addOnFailureListener {
//            // Handle any errors
//        }

    }

    fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        var adapter = CustomAdapterNube(archivos)
        binding.recyclerView.adapter = adapter
        //Controlar la pulsación
        adapter.setOnItemClickListener(object : CustomAdapterNube.onItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(binding.recyclerView.context, HacerTestNube::class.java)
                intent.putExtra("ruta", archivos[position].ruta)
                intent.putExtra("imagen", archivos[position].imagen)
                intent.putExtra("titulo", archivos[position].titulo)
                startActivity(intent)
            }
        })
        //Controlar el deslizamiento
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Eliminar permanentemente")
                    builder.setMessage("Si acepta eliminara el test de forma permanente y no podrá recuperarlo")
                    builder.setPositiveButton("Eliminar",
                        DialogInterface.OnClickListener { dialog, id ->
                            archivos.removeAt(viewHolder.adapterPosition);
                            adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        })
                    builder.setNegativeButton("Cancelar",
                        DialogInterface.OnClickListener { dialog, id ->
                            var oneArchive = archivos[viewHolder.adapterPosition]
                            var pos = viewHolder.adapterPosition
                            archivos.removeAt(pos);
                            adapter.notifyItemRemoved(pos)

                            archivos.add(pos, oneArchive)
                            adapter.notifyItemInserted(pos)
                        })
                    builder.show()
                }

            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }
}