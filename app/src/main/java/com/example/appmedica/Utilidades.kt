package com.example.appmedica.com.example.appmedica

import android.content.Context
import android.util.Log
import com.example.appmedica.DatabaseHandler
import java.text.SimpleDateFormat
import java.util.*

object Utilidades {

    fun generateUniqueRequestCode(citaId: String): Int {
        return citaId.hashCode() // Esto generará un requestCode único basado en el ID de la consulta
    }

    // Función para convertir cadenas de fecha y hora en un objeto Calendar
    fun obtenerFechaHora(fecha: String, hora: String): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        Log.d("CalendarDebug", "Fecha y hora recibidas: $fecha , $hora")

        val date = dateFormat.parse(fecha)
        val time = timeFormat.parse(hora)

        val calendar = Calendar.getInstance()
        if (date != null && time != null) {
            calendar.time = date
            //Obtener horas y minutos de la hora parseada
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = time
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            Log.d("CalendarDebug", "Fecha y hora configurada: ${calendar.time}")
        }

        return calendar
    }

    // Función para obtener la fecha actual en formato yyyy-MM-dd
    fun obtenerFechaActual(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Función para convertir una fecha en formato yyyy-M-d a un objeto Date
    fun convertirFecha(fecha: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        return dateFormat.parse(fecha)
    }

    //Función que convierte un objeto yyyy-mm-dd en calendar
    fun stringToCalendar(dateStr: String): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        try {
            // Usar el operador de "desempaquetado seguro" (?)
            val date = dateFormat.parse(dateStr) ?: return calendar // Si el parseo falla, retorna el calendario vacío
            calendar.time = date
        } catch (e: Exception) {
            e.printStackTrace() // Maneja la excepción si el formato es inválido
        }
        return calendar
    }

    //Sumar las horas según la frecuencia
    fun addHoursToDate(dateStr: String, hoursToAdd: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        try {
            // Parsear la fecha y verificar si es null
            val date = dateFormat.parse(dateStr) ?: return "" // Si no se puede parsear, retornar cadena vacía
            calendar.time = date
            calendar.add(Calendar.HOUR_OF_DAY, hoursToAdd) // Sumar las horas
        } catch (e: Exception) {
            e.printStackTrace() // Maneja la excepción si el formato es inválido
        }
        return dateFormat.format(calendar.time) // Retorna la nueva fecha como String
    }


    // Función para convertir una hora en formato HH:mm a un objeto Date
    fun convertirHora(hora: String): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        // Obtener el Calendar con la fecha y hora actuales
        val time = timeFormat.parse(hora)
        val calendar = Calendar.getInstance()
        if (time != null) {
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = time
            // Asignar la hora y minutos del 'time' al Calendar actual
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))  // Mantener la hora actual
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))  // Establecer los minutos parseados
        }

        return simpleDateFormat.format(calendar.time)
    }

    // Función para agregar días a una fecha
    fun agregarDias(fecha: String, dias: Int): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Parsear la fecha proporcionada a un objeto Date
        val date = simpleDateFormat.parse(fecha)

        if (date != null) {
            // Crear un objeto Calendar y establecer la fecha
            val calendar = Calendar.getInstance()
            calendar.time = date

            // Sumar los días al Calendar
            calendar.add(Calendar.DAY_OF_MONTH, dias)

            // Retornar la nueva fecha formateada
            return simpleDateFormat.format(calendar.time)
        } else {
            // Si la fecha no es válida
            throw IllegalArgumentException("Fecha no válida")
        }
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
        Log.d("CalendarDebug", "Fecha y hora reconfigurada -1 dia: ${calendar.time}")
        return calendar
    }

    // Función para restar 2 horas a una fecha y devolver el objeto Calendar
    fun restarDosHoras(fecha: String, hora: String): Calendar {
        val calendar = obtenerFechaHora(fecha, hora)
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        Log.d("CalendarDebug", "Fecha y hora reconfigurada -2 horas: ${calendar.time}")
        return calendar
    }

    fun restarCincoMinutos(fecha: String, hora: String): Calendar {
        val calendar = obtenerFechaHora(fecha, hora)
        calendar.add(Calendar.MINUTE, -5)
        Log.d("CalendarDebug", "Fecha y hora reconfigurada -5 mins: ${calendar.time}")
        return calendar
    }

    //Función que devuelve una fecha prudente para el recordatorio
    fun obtenerFechaRecordatorio(fecha: String, hora: String): Pair<Calendar, String> {
        val calendar = obtenerFechaHora(fecha, hora)

        // Intentar restar 3 días
        val recordatorioTresDias = calendar.clone() as Calendar
        recordatorioTresDias.add(Calendar.DAY_OF_YEAR, -3)
        if (!recordatorioTresDias.before(Calendar.getInstance())) {
            Log.d("CalendarDebug", "Fecha y hora reconfigurada -3 dias: ${calendar.time}")
            return Pair(recordatorioTresDias, "otro")
        }

        // Si ya pasó, intentar restar 1 día
        val recordatorioUnDia = calendar.clone() as Calendar
        recordatorioUnDia.add(Calendar.DAY_OF_YEAR, -1)
        if (!recordatorioUnDia.before(Calendar.getInstance())) {
            Log.d("CalendarDebug", "Fecha y hora reconfigurada -1 dia: ${calendar.time}")
            return Pair(recordatorioUnDia, "3dias")
        }

        // Si también pasó, restar 2 horas
        val recordatorioDosHoras = calendar.clone() as Calendar
        recordatorioDosHoras.add(Calendar.HOUR_OF_DAY, -2)
        if(!recordatorioDosHoras.before(Calendar.getInstance())){
            Log.d("CalendarDebug", "Fecha y hora reconfigurada -2 hrs: ${calendar.time}")
            return Pair(recordatorioDosHoras, "1dia")
        }

        //SI también ya pasaron, restar 5 mins en consecuencia
        val recordatorioCincoMins =  calendar.clone() as Calendar
        recordatorioCincoMins.add(Calendar.MINUTE, -5)
        if(!recordatorioCincoMins.before(Calendar.getInstance())){
            Log.d("CalendarDebug", "Fecha y hora reconfigurada -5 mins: ${calendar.time}")
            return Pair(recordatorioCincoMins, "2hrs") //manda el actual para devolver el mensaje siguiente
        }

        //Si no hay 5 mins de holgura notifica a la hora
        val recordatorioAct = calendar.clone() as Calendar
        return Pair(recordatorioAct, "5mins")

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
                            "Tu cita ${cita} está registrada para hoy a las a las $tiempo en $clinica.\nClick aquí para ver detalles.",
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

    //Función para extraer los días de una cadena (opciones del menú)
    fun extractDays(input: String): String {
        val regex = Regex("""^\d{1,3}\s+días$""") // Acepta 1 a 3 dígitos seguidos de "días"
        return if (regex.matches(input)) {
            input.split(" ")[0] // Extrae el número antes del espacio y convierte a Int
        } else {
            "0" // Devuelve null si el formato no es válido
        }
    }

    //Función para extraer las horas de una cadena (opciones del menú)
    fun extractHours(input: String): String? {
        val regex = Regex("""^Cada\s+(\d+)\s+hrs$""") // Valida el formato exacto "cada ## hrs"
        val matchResult = regex.find(input)
        return matchResult?.groups?.get(1)?.value // Extrae el número si coincide
    }

    fun genMensajeMed(tipo: String, dosis: String, nombre: String): String{
        return when(tipo){
            "capsula" -> {
                "Es hora! Recuerda tomar $dosis de $nombre !!!\n" +
                        " Entra para ver más detalles."
            }

            "tableta" -> {
                "Es hora! Recuerda tomar $dosis de $nombre !!!\n" +
                        " Entra para ver más detalles."
            }

            "bebible" -> {
                "Es hora! Recuerda beber $dosis de $nombre !!!\n" +
                        " Entra para ver más detalles."
            }

            "gotas" -> {
                "Es hora! Recuerda colocarte $dosis de $nombre !!!\n" +
                        " Entra para ver más detalles."
            }

            "inyectable" -> {
                "Es hora! Recuerda inyectarte $dosis de $nombre !!!\n" +
                        " Entra para ver más detalles."
            }

            else -> "Recuerda tomar tu mendicamento!!!!\n" +
                    " Entra para ver más detalles."
        }
    }

}
