@file:Suppress("DEPRECATION")
package com.app.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Suppress("NAME_SHADOWING")
class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logOutButton = findViewById<ImageButton>(R.id.logOutButton)
        logOutButton.setOnClickListener {
            showAlert()
        }

        val floatingAdd = findViewById<FloatingActionButton>(R.id.floating_agregar)
        floatingAdd.setOnClickListener {
            showAddDB()
        }
    }

    private fun showAddDB() {
        val dialog = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.dialogo_agregar, null)
        val calendarButton = dialogLayout.findViewById<ImageButton>(R.id.calendarButton)
        val dateTextView = dialogLayout.findViewById<TextView>(R.id.dateTextView)

        // Variables para almacenar la fecha y hora seleccionadas
        var selectedDateTimeString = ""

        // Configurar el diálogo para seleccionar fecha y hora
        calendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val datePickerDialog = DatePickerDialog(this, R.style.DatePickerDialogStyle, { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(this, R.style.TimePickerDialogStyle, { _, hourOfDay, minute ->
                    // Construir la fecha y hora seleccionadas
                    val selectedDateTime = Calendar.getInstance()
                    selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)

                    // Formatear la fecha y hora seleccionadas
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    selectedDateTimeString = dateFormat.format(selectedDateTime.time)

                    // Actualizar el contenido del TextView con la fecha y hora seleccionadas
                    dateTextView.text = selectedDateTimeString
                }, currentHour, currentMinute, true)

                // Mostrar el selector de hora después de seleccionar la fecha
                timePickerDialog.show()
            }, currentYear, currentMonth, currentDay)

            // Mostrar el selector de fecha
            datePickerDialog.show()
        }

        val prioritySpinner = dialogLayout.findViewById<Spinner>(R.id.prioritySpinner)
        val priorities = resources.getStringArray(R.array.SpinnerItems)
        var selectedPriority: String = priorities[0] // Variable para almacenar la prioridad seleccionada
        val adapter = object : ArrayAdapter<String>(this, R.layout.spinner_layout, priorities) {
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
                // Maneja la situación en la que no se selecciona nada
            }
        }

        val nameEditText = dialogLayout.findViewById<EditText>(R.id.nameEditText)
        val descEditText = dialogLayout.findViewById<EditText>(R.id.descEditText)

        dialog.setView(dialogLayout)
            .setTitle("Agregar Tarea")
            .setPositiveButton("Agregar") { dialog, _ ->
                val nombre = nameEditText.text.toString()
                val descripcion = descEditText.text.toString()

                if (nombre.isNotBlank() && descripcion.isNotBlank() && selectedDateTimeString.isNotBlank()) {
                    // Verificar que el nombre, la descripción y la fecha/hora no estén vacíos

                    // Obtener el usuario actualmente autenticado
                    val usuario = FirebaseAuth.getInstance().currentUser

                    if (usuario != null) {
                        // Crear un HashMap para la tarea
                        val tarea = hashMapOf(
                            "nombre" to nombre,
                            "descripcion" to descripcion,
                            "prioridad" to selectedPriority,  // Agregar la prioridad seleccionada
                            "fecha_hora" to selectedDateTimeString, // Agregar la fecha y hora seleccionadas
                            "usuario_id" to usuario.uid  // Guardar el ID del usuario
                        )

                        // Agregar la tarea a Firestore
                        agregarTarea(tarea)
                    } else {
                        // No se pudo obtener el usuario autenticado
                        Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                } else {
                    // Mostrar un mensaje de error si algún campo está vacío
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun agregarTarea(tarea: HashMap<String, String>) {
        // Agregar la tarea a la colección "tareas"
        db.collection("tareas")
            .add(tarea)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Tarea agregada con ID: ${documentReference.id}")
                Toast.makeText(this, "Tarea agregada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar tarea", e)
                Toast.makeText(this, "Error al agregar tarea", Toast.LENGTH_SHORT).show()
            }
    }



    private fun showAlert() {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Estás seguro de que quieres cerrar sesión?")
        builder.setPositiveButton("Sí") { _, _ ->
            signOut()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            revokeGoogleAccess()
        }
    }

    private fun revokeGoogleAccess() {
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            Toast.makeText(this, "Hasta pronto", Toast.LENGTH_SHORT).show()
            // Redirige a AuthActivity después de revocar el acceso
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val TAG = "MiApp"
    }
}