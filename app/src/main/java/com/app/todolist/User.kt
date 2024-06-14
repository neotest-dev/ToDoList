package com.app.todolist

data class User(
    val idTarea: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val fecha_hora: String = "",
    val prioridad: String = "",
    val usuario_id: String = ""
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (idTarea != other.idTarea) return false
        if (nombre != other.nombre) return false
        if (descripcion != other.descripcion) return false
        if (fecha_hora != other.fecha_hora) return false
        if (prioridad != other.prioridad) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idTarea.hashCode()
        result = 31 * result + nombre.hashCode()
        result = 31 * result + descripcion.hashCode()
        result = 31 * result + fecha_hora.hashCode()
        result = 31 * result + prioridad.hashCode()
        return result
    }
}
