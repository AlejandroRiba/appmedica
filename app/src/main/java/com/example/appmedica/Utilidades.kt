package com.example.appmedica.com.example.appmedica

import java.text.SimpleDateFormat
import java.util.*

object Utilidades {
    // Función para convertir cadenas de fecha y hora en un objeto Calendar
    fun obtenerFechaHora(fecha: String, hora: String): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val date = dateFormat.parse(fecha)
        val time = timeFormat.parse(hora)

        val calendar = Calendar.getInstance()
        if (date != null && time != null) {
            calendar.time = date
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = time
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        return calendar
    }

    // Función para obtener la fecha actual en formato yyyy-MM-dd
    fun obtenerFechaActual(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Función para convertir una fecha en formato yyyy-M-d a un objeto Date
    fun convertirFecha(fecha: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        return dateFormat.parse(fecha)
    }

    // Función para convertir una hora en formato HH:mm a un objeto Date
    fun convertirHora(hora: String): Date? {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.parse(hora)
    }

    // Función para agregar días a una fecha
    fun agregarDias(fecha: Date, dias: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = fecha
        calendar.add(Calendar.DAY_OF_YEAR, dias)
        return calendar.time
    }

    // Función para obtener el nombre del día de la semana a partir de una fecha
    fun obtenerNombreDiaSemana(fecha: Date): String {
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dateFormat.format(fecha)
    }

    // Función para restar 3 días a una fecha y devolver el objeto Calendar
    fun restarTresDias(fecha: String, hora: String): Calendar {
        val calendar = obtenerFechaHora(fecha, hora)
        calendar.add(Calendar.DAY_OF_YEAR, -3)
        return calendar
    }
    fun restarDia(fecha: String, hora: String): Calendar {
        val calendar = obtenerFechaHora(fecha, hora)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return calendar
    }

    // Función para restar 2 horas a una fecha y devolver el objeto Calendar
    fun restarDosHoras(fecha: String, hora: String): Calendar {
        val calendar = obtenerFechaHora(fecha, hora)
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        return calendar
    }

    fun restarCincoMinutos(fecha: String, hora: String): Calendar {
        val calendar = obtenerFechaHora(fecha, hora)
        calendar.add(Calendar.MINUTE, -5)
        return calendar
    }

    //Función que devuelve una fecha prudente para el recordatorio
    fun obtenerFechaRecordatorio(fecha: String, hora: String): Pair<Calendar, String> {
        val calendar = obtenerFechaHora(fecha, hora)

        // Intentar restar 3 días
        val recordatorioTresDias = calendar.clone() as Calendar
        recordatorioTresDias.add(Calendar.DAY_OF_YEAR, -3)
        if (!recordatorioTresDias.before(Calendar.getInstance())) {
            return Pair(recordatorioTresDias, "otro")
        }

        // Si ya pasó, intentar restar 1 día
        val recordatorioUnDia = calendar.clone() as Calendar
        recordatorioUnDia.add(Calendar.DAY_OF_YEAR, -1)
        if (!recordatorioUnDia.before(Calendar.getInstance())) {
            return Pair(recordatorioUnDia, "3dias")
        }

        // Si también pasó, restar 2 horas
        val recordatorioDosHoras = calendar.clone() as Calendar
        recordatorioDosHoras.add(Calendar.HOUR_OF_DAY, -2)
        if(!recordatorioDosHoras.before(Calendar.getInstance())){
            return Pair(recordatorioDosHoras, "1dia")
        }

        //SI también ya pasaron, restar 5 mins en consecuencia
        val recordatorioCincoMins =  calendar.clone() as Calendar
        recordatorioCincoMins.add(Calendar.MINUTE, -5)
        return Pair(recordatorioCincoMins, "2hrs") //manda el actual para devolver el mensaje siguiente

    }

    fun obtenerMensajeCita(
        actualReminder: String,
        cita: String,
        clinica: String,
        tiempo: String,//hora
        fecha: String
    ): Pair<Pair<String, String>, String> {
        val ahora = Calendar.getInstance()

        return when (actualReminder) {
            // La cita siguiente
            "5mins" -> {
                Pair(
                    "CITA PENDIENTE AHORA MISMO !" to
                            "Tu cita ${cita} está registrada para hoy a esta hora.\nClick aquí para ver detalles.",
                    "ahora"
                )
            }

            // La cita es hoy en 5 mins
            "2hrs" -> {
                Pair(
                    "CITA PENDIENTE EN 5 MINUTOS !" to
                            "Tu cita $cita está registrada para hoy a las $tiempo en $clinica.\nClick aquí para ver detalles.",
                    "5mins"
                )
            }

            // La cita es hoy en 2 horas
            "1dia" -> {
                Pair(
                    "CITA PENDIENTE EN 2 HRS !" to
                            "Tu cita $cita está registrada para hoy a las $tiempo en ${clinica}.\nClick aquí para ver detalles.",
                    "2hrs"
                )
            }

            // La cita es en 3 días
            "otro" -> {
                Pair(
                    "CITA PENDIENTE EN TRES DÍAS !" to
                            "Tu cita $cita está registrada para ${fecha} a las $tiempo en ${clinica}.\nClick aquí para ver detalles.",
                    "3dias"
                )
            }

            // La cita es en 1 día
            "3dias" -> {
                Pair(
                    "CITA PENDIENTE MAÑANA !" to
                            "Tu cita $cita está registrada para mañana a las $tiempo en ${clinica}.\nClick aquí para ver detalles.",
                    "1dia"
                )
            }

            // Default
            else -> Pair("CITA SIN DETALLES" to "No se pudo determinar un mensaje para la cita.", "none")
        }
    }


}
