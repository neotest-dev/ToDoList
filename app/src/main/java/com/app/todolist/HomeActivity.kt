@file:Suppress("DEPRECATION")
package com.app.todolist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@Suppress("NAME_SHADOWING")
class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var myAdapter: MyAdapter
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        userArrayList = arrayListOf()
        recyclerView = findViewById(R.id.recyclerTask)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        checkTask()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val fab: FloatingActionButton = findViewById(R.id.floating_agregar)
        fab.setOnClickListener {
            showAddDB()
        }
        val adapter = MyAdapter(userArrayList,this, resources, recyclerView)
        recyclerView.adapter = adapter


        cargarDatos()
    }

    private fun checkTask() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("tareas")
                .whereEqualTo("usuario_id", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val numTasks = querySnapshot.size()
                    Log.d(TAG, "Número de tareas del usuario: $numTasks")
                    val logoImageView = findViewById<ImageView>(R.id.logoImageView)
                    if (numTasks > 0) {
                        logoImageView.visibility = View.GONE
                    } else {
                        logoImageView.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener las tareas del usuario: $exception")
                }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val filtrarItem = menu?.findItem(R.id.filtrar)
        if (filtrarItem != null) {
            val filtrarTitle = SpannableString(filtrarItem.title)
            filtrarTitle.setSpan(ForegroundColorSpan(Color.BLACK), 0, filtrarTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            filtrarTitle.setSpan(StyleSpan(Typeface.BOLD), 0, filtrarTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            filtrarTitle.setSpan(AbsoluteSizeSpan(18, true), 0, filtrarTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            filtrarItem.title = filtrarTitle
        }

        val logoutItem = menu?.findItem(R.id.exit)
        if (logoutItem != null) {
            val exitTitle = SpannableString(logoutItem.title)
            exitTitle.setSpan(ForegroundColorSpan(Color.RED), 0, exitTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            exitTitle.setSpan(StyleSpan(Typeface.BOLD), 0, exitTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            exitTitle.setSpan(AbsoluteSizeSpan(18, true), 0, exitTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            logoutItem.title = exitTitle
        }

        val bajaItem = menu?.findItem(R.id.baja)
        val mediaItem = menu?.findItem(R.id.media)
        val urgenteItem = menu?.findItem(R.id.urgente)
        val todasItem = menu?.findItem(R.id.todas)

        // Obtener colores desde colors.xml
        val bajaColor = ContextCompat.getColor(this, R.color.green)
        val mediaColor = ContextCompat.getColor(this, R.color.orange)
        val urgenteColor = ContextCompat.getColor(this, R.color.red)
        val todasColor = ContextCompat.getColor(this, R.color.blue_electric)

        // Configurar estilos de texto
        bajaItem?.let { setMenuItemStyle(it, getString(R.string.baja), bajaColor, Typeface.BOLD) }
        mediaItem?.let { setMenuItemStyle(it, getString(R.string.media), mediaColor, Typeface.BOLD) }
        urgenteItem?.let { setMenuItemStyle(it, getString(R.string.urgente), urgenteColor, Typeface.BOLD) }
        todasItem?.let { setMenuItemStyle(it, getString(R.string.Todas), todasColor, Typeface.BOLD) }

        // Configurar el SearchView
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text : String?): Boolean {
                search(text.toString())
                return true
            }
        })

        return true
    }

    private fun setMenuItemStyle(item: MenuItem, title: String, color: Int, style: Int) {
        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(ForegroundColorSpan(color), 0, spannableTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        spannableTitle.setSpan(StyleSpan(style), 0, spannableTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        item.title = spannableTitle
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.exit -> {
                showAlert()
                return true
            }
            R.id.baja -> {
                cargarDatosConPrioridadBaja()
                true
            }
            R.id.media -> {
                cargarDatosConPrioridadMedia()
                true
            }
            R.id.urgente -> {
                cargarDatosConPrioridadUrgente()
                true
            }
            R.id.todas -> {
                cargarDatos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun search(searchText: String? = null) {
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
                            if (searchText.isNullOrEmpty() || it.nombre.contains(searchText, ignoreCase = true)) {
                                Log.d(TAG, "Nombre: ${it.nombre}")
                                Log.d(TAG, "Descripción: ${it.descripcion}")
                                Log.d(TAG, "Fecha y Hora: ${it.fecha_hora}")
                                Log.d(TAG, "Prioridad: ${it.prioridad}")
                                userArrayList.add(it)
                            }
                        }
                    }
                    userArrayList.sortBy { it.fecha_hora }
                    recyclerView.adapter = MyAdapter(userArrayList, this, resources, recyclerView)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar datos", exception)
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosConPrioridadBaja(){
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("tareas")
                .whereEqualTo("usuario_id", userId)
                .whereEqualTo("prioridad", "Baja")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userArrayList = mutableListOf<User>()
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            Log.d(TAG, "Nombre: ${it.nombre}")
                            Log.d(TAG, "Descripción: ${it.descripcion}")
                            Log.d(TAG, "Fecha y Hora: ${it.fecha_hora}")
                            Log.d(TAG, "Prioridad: ${it.prioridad}")
                            userArrayList.add(it)
                        }
                    }
                    userArrayList.sortBy { it.fecha_hora }
                    findViewById<ImageView>(R.id.logoImageView)
                    recyclerView.adapter = MyAdapter(userArrayList, this, resources, recyclerView)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar datos", exception)
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosConPrioridadMedia(){
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("tareas")
                .whereEqualTo("usuario_id", userId)
                .whereEqualTo("prioridad", "Media")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userArrayList = mutableListOf<User>()
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            Log.d(TAG, "Nombre: ${it.nombre}")
                            Log.d(TAG, "Descripción: ${it.descripcion}")
                            Log.d(TAG, "Fecha y Hora: ${it.fecha_hora}")
                            Log.d(TAG, "Prioridad: ${it.prioridad}")
                            userArrayList.add(it)
                        }
                    }
                    userArrayList.sortBy { it.fecha_hora }
                    findViewById<ImageView>(R.id.logoImageView)
                    recyclerView.adapter = MyAdapter(userArrayList,this, resources, recyclerView)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar datos", exception)
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatosConPrioridadUrgente(){
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("tareas")
                .whereEqualTo("usuario_id", userId)
                .whereEqualTo("prioridad", "Urgente")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userArrayList = mutableListOf<User>()
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            Log.d(TAG, "Nombre: ${it.nombre}")
                            Log.d(TAG, "Descripción: ${it.descripcion}")
                            Log.d(TAG, "Fecha y Hora: ${it.fecha_hora}")
                            Log.d(TAG, "Prioridad: ${it.prioridad}")
                            userArrayList.add(it)
                        }
                    }
                    userArrayList.sortBy { it.fecha_hora }
                    findViewById<ImageView>(R.id.logoImageView)
                    recyclerView.adapter = MyAdapter(userArrayList, this, resources, recyclerView)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar datos", exception)
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarDatos() {
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
                            Log.d(TAG, "Nombre: ${it.nombre}")
                            Log.d(TAG, "Descripción: ${it.descripcion}")
                            Log.d(TAG, "Fecha y Hora: ${it.fecha_hora}")
                            Log.d(TAG, "Prioridad: ${it.prioridad}")
                            userArrayList.add(it)
                        }
                    }
                    userArrayList.sortBy { it.fecha_hora }
                    findViewById<ImageView>(R.id.logoImageView)
                    recyclerView.adapter = MyAdapter(userArrayList,this, resources, recyclerView)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al cargar datos", exception)
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
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
                    val selectedDateTime = Calendar.getInstance()
                    selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    selectedDateTimeString = dateFormat.format(selectedDateTime.time)
                    dateTextView.text = selectedDateTimeString
                }, currentHour, currentMinute, true)
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

                        cargarDatos()
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
        // Generar un ID único para la tarea
        val idTarea = UUID.randomUUID().toString()

        // Asignar el ID único a la tarea
        tarea["idTarea"] = idTarea

        // Agregar la tarea a la colección "tareas"
        db.collection("tareas")
            .document(idTarea)  // Usar el ID generado como ID del documento
            .set(tarea)
            .addOnSuccessListener {
                Log.d(TAG, "Tarea agregada con ID: $idTarea")
                Toast.makeText(this, "Tarea agregada", Toast.LENGTH_SHORT).show()
                checkTask()
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
        const val TAG = "MiApp"
    }
}
