package com.app.todolist


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "NAME_SHADOWING")
class MyAdapter(private val userList: MutableList<User>,
                private val context: Context,
                private val resources: android.content.res.Resources,
                private val recyclerView: RecyclerView):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

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
            showEditDB(user)
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
    }

    private fun showEditDB(user: User) {
        val dialog = AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialogo_agregar, null)
        val nameEditText = dialogLayout.findViewById<EditText>(R.id.nameEditText)
        val descEditText = dialogLayout.findViewById<EditText>(R.id.descEditText)
        val dateTextView = dialogLayout.findViewById<TextView>(R.id.dateTextView)
        var selectedDateTimeString: String
        val calendarButton = dialogLayout.findViewById<ImageButton>(R.id.calendarButton)
        val idTarea = user.idTarea

        calendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialogStyle, { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(context, R.style.TimePickerDialogStyle, { _, hourOfDay, minute ->
                    val selectedDateTime = Calendar.getInstance()
                    selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    selectedDateTimeString = dateFormat.format(selectedDateTime.time)
                    dateTextView.text = selectedDateTimeString
                }, currentHour, currentMinute, true)
                timePickerDialog.show()
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        val prioritySpinner = dialogLayout.findViewById<Spinner>(R.id.prioritySpinner)
        val priorities = context.resources.getStringArray(R.array.SpinnerItems)

        var selectedPriority: String = priorities[0] // Variable para almacenar la prioridad seleccionada
        val adapter = object : ArrayAdapter<String>(context, R.layout.spinner_layout, priorities) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as CheckedTextView
                when (position) {
                    0 -> view.setTextColor(ContextCompat.getColor(context, R.color.green)) // Baja
                    1 -> view.setTextColor(ContextCompat.getColor(context, R.color.orange)) // Media
                    2 -> view.setTextColor(ContextCompat.getColor(context, R.color.red)) // Urgente
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as CheckedTextView
                when (position) {
                    0 -> view.setTextColor(ContextCompat.getColor(context, R.color.green)) // Baja
                    1 -> view.setTextColor(ContextCompat.getColor(context, R.color.orange)) // Media
                    2 -> view.setTextColor(ContextCompat.getColor(context, R.color.red)) // Urgente
                }
                return view
            }
        }
        adapter.setDropDownViewResource(R.layout.color_spinner_layout)
        prioritySpinner.adapter = adapter

        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriority = priorities[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        nameEditText.setText(user.nombre)
        descEditText.setText(user.descripcion)
        dateTextView.text = user.fecha_hora
        prioritySpinner.setSelection(priorities.indexOf(user.prioridad))

        dialog.setView(dialogLayout)
            .setTitle("Editar Tarea")
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombre = nameEditText.text.toString()
                val descripcion = descEditText.text.toString()
                val prioridad = prioritySpinner.selectedItem.toString()
                val fechaHora = dateTextView.text.toString()

                if (nombre.isNotBlank() && descripcion.isNotBlank() && fechaHora.isNotBlank()) {
                    // Verificar que los campos no estén vacíos

                    val usuario = FirebaseAuth.getInstance().currentUser

                    if (usuario != null) {
                        // Crear un HashMap con los datos actualizados de la tarea
                        val tarea = hashMapOf<String, Any>(
                            "idTarea" to idTarea, // Incluir el idTarea en el mapa
                            "nombre" to nombre,
                            "descripcion" to descripcion,
                            "prioridad" to prioridad,
                            "fecha_hora" to fechaHora,
                            "usuario_id" to usuario.uid
                        )
                        editarTarea(tarea)
                        cargarDatos()

                    } else {
                        // No se pudo obtener el usuario autenticado
                        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                } else {
                    // Mostrar un mensaje de error si algún campo está vacío
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun editarTarea(tarea: HashMap<String, Any>) {
        val db = FirebaseFirestore.getInstance()

        // Obtener el ID de tarea de la tarea a editar
        val tareaId = tarea["idTarea"] as? String

        // Verificar si el ID de tarea es válido
        if (tareaId != null) {
            // Utilizar el ID de tarea para actualizar el documento en Firestore
            db.collection("tareas").document(tareaId)
                .update(tarea)
                .addOnSuccessListener {
                    // Tarea actualizada exitosamente
                    cargarDatos()
                }
                .addOnFailureListener { e ->
                    // Error al actualizar la tarea
                    Log.e(TAG, "Error al actualizar la tarea", e)
                    Toast.makeText(context, "Error al actualizar la tarea", Toast.LENGTH_SHORT).show()
                }
        } else {
            // El ID de tarea no es válido
            Toast.makeText(context, "ID de tarea no válido", Toast.LENGTH_SHORT).show()
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun cargarDatos() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("tareas")
                .whereEqualTo("usuario_id", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userArrayList = mutableListOf<User>()
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            Log.d(HomeActivity.TAG, "Nombre: ${it.nombre}")
                            Log.d(HomeActivity.TAG, "Descripción: ${it.descripcion}")
                            Log.d(HomeActivity.TAG, "Fecha y Hora: ${it.fecha_hora}")
                            Log.d(HomeActivity.TAG, "Prioridad: ${it.prioridad}")
                            userArrayList.add(it)
                        }
                    }
                    // Actualizar userList en el adaptador con los nuevos datos
                    userList.clear()
                    userList.addAll(userArrayList)
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(HomeActivity.TAG, "Error al cargar datos", exception)
                    Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }



    companion object {
        private const val TAG = "MiApp"
    }

}

