package ittepic.edu.almacensms

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Mensaje (val telefono : String?=null, val fechahora: String ?=null, val mensaje: String ?=null) {
}