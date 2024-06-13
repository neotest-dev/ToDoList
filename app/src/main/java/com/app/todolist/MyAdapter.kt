package com.app.todolist


import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import java.util.Locale


class MyAdapter(private val userList: MutableList<User>, private val context: Context): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user: User = userList[position]
        holder.name.text = user.nombre
        holder.desc.text = user.descripcion
        holder.hour.text = user.fecha_hora

        holder.priority.apply {
            text = user.prioridad
            when (user.prioridad.lowercase(Locale.getDefault())) {
                "baja" -> {
                    setTextColor(ContextCompat.getColor(context, R.color.green))
                    setTypeface(null, Typeface.BOLD)
                }
                "media" -> {
                    setTextColor(ContextCompat.getColor(context, R.color.orange))
                    setTypeface(null, Typeface.BOLD)
                }
                "urgente" -> {
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    setTypeface(null, Typeface.BOLD)
                }
                else -> {
                    // Por defecto, utiliza el color y el estilo predeterminados
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    setTypeface(null, Typeface.NORMAL)
                }
            }
        }

        when (user.prioridad.lowercase(Locale.getDefault())) {
            "baja" -> holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            "media" -> holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.orange))
            "urgente" -> holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            else -> holder.priorityIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

        holder.edit.setOnClickListener {


        }
        holder.delete.setOnClickListener {


        }


    }


    override fun getItemCount(): Int {
        return userList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val desc: TextView = itemView.findViewById(R.id.tvDesc)
        val hour: TextView = itemView.findViewById(R.id.tvHour)
        val priority: TextView = itemView.findViewById(R.id.tvPriority)
        val priorityIndicator: View = itemView.findViewById(R.id.colorCard)
        val edit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val delete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

}

