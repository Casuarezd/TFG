package com.example.tfg.ui.miNube

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import android.preference.PreferenceManager
import com.example.tfg.Estadisticas
import com.example.tfg.adapter.CustomAdapterNube
import com.example.tfg.databinding.FragmentNubeBinding
import com.example.tfg.model.ModeloListarArchivos
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
        val myPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val email: String = myPreferences.getString("email", "empty")

//        val splitEmail = email.split("@").toTypedArray()
//        val finalEmail = splitEmail[0]
        if (email != "empty") {
            val mStorage = Firebase.storage
            val lista = mStorage.reference.child(email)

            lista.listAll()
                .addOnSuccessListener { (items, prefixes) ->
                    prefixes.forEach { prefix ->
                        //println(prefix)
                    }

                    items.forEach { item ->

                        var ruta = item.toString().split("/")
                        var name = ruta[ruta.size - 1]

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
                    initRecyclerView()
                    println("SIZE " + archivos.size)
                }
                .addOnFailureListener {
                    // Uh-oh, an error occurred!
                }
        }
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

                val intent = Intent(binding.recyclerView.context, Estadisticas::class.java)
                intent.putExtra("ruta", archivos[position].ruta)
                intent.putExtra("imagen", archivos[position].imagen)
                intent.putExtra("titulo", archivos[position].titulo)
                intent.putExtra("metodo", "nube")
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
                            var oneArchive = archivos[viewHolder.adapterPosition]

                            val myPreferences =
                                PreferenceManager.getDefaultSharedPreferences(context)
                            val email: String = myPreferences.getString("email", "pruebas")

                            val mStorage = Firebase.storage
                            mStorage.reference.child(email).child(oneArchive.titulo).delete()


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