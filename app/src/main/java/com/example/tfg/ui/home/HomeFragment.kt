package com.example.tfg.ui.home

import android.Manifest
import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tfg.adapter.CustomAdapter
import com.example.tfg.HacerTest
import com.example.tfg.databinding.FragmentHomeBinding
import com.example.tfg.model.ModeloListarArchivos
import io.reactivex.disposables.Disposable
import java.io.File
import java.util.*


class HomeFragment : Fragment() {

    //private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val requestCode = 100
    private var disposable: Disposable? = null
    private lateinit var newArray: ArrayList<ModeloListarArchivos>
    private val archivos = mutableListOf<ModeloListarArchivos>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity?.let {
                ContextCompat.checkSelfPermission(it,  Manifest.permission.READ_EXTERNAL_STORAGE)
            } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
        } else {
            listarDocumentos(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
            initRecyclerView()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == this.requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                listarDocumentos(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                initRecyclerView()
            } else {
            }
        }
    }

    override fun onPause() {
        super.onPause()
        this.disposable?.dispose()
    }

    fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        var adapter = CustomAdapter(archivos)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : CustomAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val aux = File(archivos[position].ruta)
                if(aux.isDirectory){

                }else if(aux.extension=="pdf"){
                    val intent = Intent(binding.recyclerView.context, HacerTest::class.java)
                    intent.putExtra("ruta", archivos[position].ruta)
                    intent.putExtra("imagen", archivos[position].imagen)
                    intent.putExtra("titulo", archivos[position].titulo)
                    startActivity(intent)
                }

            }
        })
    }


    private fun listarDocumentos(directory: File) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file != null) {
                    val aux: ModeloListarArchivos
                    var extension = file.extension
                    if (file.isDirectory) {
                        aux = ModeloListarArchivos(
                            file.name,
                            "https://i.pinimg.com/originals/ac/da/32/acda326868c9cba424c8059d1853c3bb.png",
                            file.absolutePath
                        )
                    } else if (file.isFile && extension=="pdf") {
                        aux = ModeloListarArchivos(
                            file.name,
                            "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/PDF_file_icon.svg/1200px-PDF_file_icon.svg.png",
                            file.absolutePath
                        )

                    } else {
                        aux = ModeloListarArchivos(
                            file.name,
                            "https://upload.wikimedia.org/wikipedia/commons/6/66/Android_robot.png",
                            file.absolutePath
                        )
                    }
                    archivos.add(aux)
                }
            }
        }
    }


}



