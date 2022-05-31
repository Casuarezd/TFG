package com.example.tfg.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tfg.R
import com.example.tfg.model.ModeloListarArchivos
import com.squareup.picasso.Picasso

class CustomAdapter(val listaArchivos:List<ModeloListarArchivos>): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener=listener
    }

    class ViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        val itemImage: ImageView
        val itemTitle: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.titulo)
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v =LayoutInflater.from(viewGroup.context).inflate(R.layout.lista, viewGroup, false)
        return ViewHolder(v,mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = listaArchivos[i].titulo
        Picasso.get().load(listaArchivos[i].imagen).into(viewHolder.itemImage)
    }

    override fun getItemCount(): Int {
        return listaArchivos.size
    }

}