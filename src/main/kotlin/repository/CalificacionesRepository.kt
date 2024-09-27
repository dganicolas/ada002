package org.example.repository

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class CalificacionesRepository {

    //Una función que reciba el fichero de calificaciones y devuelva una lista de diccionarios, donde cada diccionario contiene la información de los exámenes y la asistencia de un alumno. La lista tiene que estar ordenada por apellidos.
    fun crearListaDiccionarios(ficheroCalificaciones: Path): MutableMap<String, MutableMap<String, String>> {
        val listaDiccionario: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

        if (ficheroCalificaciones.exists()) {
            val br: BufferedReader = Files.newBufferedReader(ficheroCalificaciones)
            br.use {
                br.forEachLine {
                    val lista = it.split(";")
                    //Apellidos;Nombre;Asistencia;Parcial1;Parcial2;Ordinario1;Ordinario2;Practicas;OrdinarioPracticas
                    listaDiccionario[lista[0]] = mutableMapOf(
                        "Apellidos" to lista[0],
                        "Nombre" to lista[1],
                        "Asistencia" to lista[2],
                        "Parcial1" to lista[3],
                        "Parcial2" to lista[4],
                        "Ordinario1" to lista[5],
                        "Ordinario2" to lista[6],
                        "Practicas" to lista[7],
                        "OrdinarioPracticas" to lista[8],

                        )
                }
            }
        }
        listaDiccionario.toSortedMap()
        return listaDiccionario
    }

    //Una función que reciba una lista de diccionarios como la que devuelve la función anterior y añada a cada diccionario un nuevo par con la nota final del curso. El peso de cada parcial de teoría en la nota final es de un 30% mientras que el peso del examen de prácticas es de un 40%.
    // examen 1 y examen 2 cuenta un 60% y el practicas y demas cuentan un 40%

    fun anadirNotaFinalDiccionario(diccionario: MutableMap<String, MutableMap<String, String>>): MutableMap<String, MutableMap<String, String>> {
        diccionario.forEach {
            val valores = it.value

            val primerExamen =
                if (valores["Ordinario1"] == "") valores["Parcial1"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f
                else valores["Ordinario1"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            val segundoExamen =
                if (valores["Ordinario2"] == "") valores["Parcial2"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f
                else valores["Ordinario2"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            val examenPracticas =
                if (valores["OrdinarioPracticas"] == "") valores["Practicas"]?.replace(",", ".")?.toFloatOrNull()
                    ?: 0.0f
                else valores["OrdinarioPracticas"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            val media = primerExamen * 0.3f + segundoExamen * 0.3f + examenPracticas * 0.4f
            valores["NotaFinal"] = media.toString().replace(".", ",")
        }
        return diccionario
    }

    //Una función que reciba una lista de diccionarios como la que devuelve la función anterior y devuelva dos listas,
    // una con los alumnos aprobados y otra con los alumnos suspensos. Para aprobar el curso, la asistencia tiene que
    // ser mayor o igual que el 75%, la nota de los exámenes parciales y de prácticas mayor o igual que 4 y la nota final
    // mayor o igual que 5.
    fun alumnoEstaAprobado(diccionario: MutableMap<String, MutableMap<String, String>>): MutableMap<String, MutableList<String>> {
        val mapAprobados = mutableMapOf<String, MutableList<String>>()
        mapAprobados["Aprobados"] = emptyList<String>().toMutableList()
        mapAprobados["Suspendidos"] = emptyList<String>().toMutableList()
        diccionario.forEach {
            val valores = it.value

            val primerExamen =
                if (valores["Ordinario1"] == "") valores["Parcial1"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f
                else valores["Ordinario1"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            val segundoExamen =
                if (valores["Ordinario2"] == "") valores["Parcial2"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f
                else valores["Ordinario2"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            val examenPracticas =
                if (valores["OrdinarioPracticas"] == "") valores["Practicas"]?.replace(",", ".")?.toFloatOrNull()
                    ?: 0.0f
                else valores["OrdinarioPracticas"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            val asistencia = valores["Asistencia"]?.replace("%", "")?.toIntOrNull() ?: 0
            val notaFinal = valores["NotaFinal"]?.replace(",", ".")?.toFloatOrNull() ?: 0.0f

            //la asistencia tiene que ser mayor o igual que el 75%
            if (asistencia >= 75) {
                //la nota de los exámenes parciales y de prácticas mayor o igual que 4
                if (primerExamen >= 4 && segundoExamen >= 4 && examenPracticas >= 4) {
                    //la nota final mayor o igual que 5.
                    if (notaFinal >= 5.0f) {
                        mapAprobados["Aprobados"]?.add("${valores["Nombre"]} ${valores["Apellidos"]}")
                    } else {
                        mapAprobados["Suspendidos"]?.add("${valores["Nombre"]} ${valores["Apellidos"]}")
                    }
                } else {
                    mapAprobados["Suspendidos"]?.add("${valores["Nombre"]} ${valores["Apellidos"]}")
                }
            } else {
                mapAprobados["Suspendidos"]?.add("${valores["Nombre"]} ${valores["Apellidos"]}")
            }
        }
        return mapAprobados
    }
}