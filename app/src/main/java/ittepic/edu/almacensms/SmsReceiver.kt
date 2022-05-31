package ittepic.edu.almacensms

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.Instant

class SmsReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if(extras != null){
            var sms = extras.get("pdus") as Array<Any>

            for (indice in sms.indices){
                var formato = extras.getString("format")
                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)

                }
                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                //Toast.makeText(context, "Entro Contenido ${contenidoSMS}", Toast.LENGTH_LONG)
                  //  .show()

                var baseDatos = Firebase.database.reference

                val mensaje = Mensaje(celularOrigen.toString(),Instant.now().toString(),contenidoSMS)
                baseDatos.child("mensajes").push().
                setValue(mensaje)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Insertado en Firebase", Toast.LENGTH_LONG)
                            .show()
                    }.addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG)
                            .show()
                    }
            }
        }

    }
}