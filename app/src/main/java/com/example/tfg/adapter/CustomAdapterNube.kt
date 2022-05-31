package com.example.tfg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.model.ModeloListarArchivos

class CustomAdapterNube(val listaArchivos:List<ModeloListarArchivos>): RecyclerView.Adapter<CustomAdapterNube.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener=listener
    }

    class ViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        val itemTitle: TextView

        init {
            itemTitle = itemView.findViewById(R.id.titulo)
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v =LayoutInflater.from(viewGroup.context).inflate(R.layout.lista_nube, viewGroup, false)
        return ViewHolder(v,mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = listaArchivos[i].titulo
    }

    override fun getItemCount(): Int {
        return listaArchivos.size
    }

}