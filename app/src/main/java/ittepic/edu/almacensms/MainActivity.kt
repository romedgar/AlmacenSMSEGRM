package ittepic.edu.almacensms

import android.R
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.SparseBooleanArray
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import ittepic.edu.almacensms.databinding.ActivityMainBinding
import java.io.File
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    val siPermiso = 1
    val siPermisoReceiver = 2
    val siPermisoStorage = 3
    var listaIDs = ArrayList<String>()
    var conjunto_mensajes = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileName = "/data/data/ittepic.edu.almacensms/files/mensajes.txt"
        var file = File(fileName)
        var fileExists = file.exists()
        if(fileExists){
            AlertDialog.Builder(this)
                .setMessage("Using previous file...")
        } else {
            AlertDialog.Builder(this)
                .setMessage("Writing a new file...")
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val consulta = FirebaseDatabase.getInstance().getReference().child("mensajes")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var datos = ArrayList<String>()
                listaIDs.clear()

                for(data in snapshot.children!!){
                    val id = data.key
                    listaIDs.add(id!!)
                    val mensaje = data.getValue<Mensaje>()!!.mensaje
                    val telefono = data.getValue<Mensaje>()!!.telefono
                    val fechahora = data.getValue<Mensaje>()!!.fechahora
                    datos.add("Telefono ${telefono}\nMensaje: ${mensaje}\n")
                    //Se emotio la fecha y la hora porque no cabía bien :)
                }
                mostrarLista(datos)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        consulta.addValueEventListener(postListener)

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReceiver)

        binding.descargar.setOnClickListener {

            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),siPermisoStorage)

            conjunto_mensajes = ""
           var tama = binding.lista.count
           var checked = binding.lista.checkedItemPositions

            for (i in 0..tama){
                if (checked.get(i)){
                    var item = binding.lista.getItemAtPosition(i).toString()
                    Toast.makeText(this,item,Toast.LENGTH_SHORT)
                       // .show()
                    conjunto_mensajes+=item+","
                }
        }
            SaveInFile(conjunto_mensajes)
        }

        /*binding.button.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
            }else{
                envioSMS()
            }
        }*/


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == siPermiso){
            //envioSMS()
        }
        if(requestCode == siPermisoReceiver){
            mensajeRecibir()
        }
    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this)
            .setMessage("Se otorgo recibir")
            .show()
    }
/*
    private fun envioSMS() {
        SmsManager.getDefault().sendTextMessage(binding.celular.text.toString(),null,binding.mensaje.text.toString(),null,null)
        Toast.makeText(this,"Se envio el sms", Toast.LENGTH_LONG)
            .show()

    }*/

    private fun SaveInFile(cadena_mensajes:String){
        try {
            val file = OutputStreamWriter(this.openFileOutput("mensajes.txt", 0))

            file.write(cadena_mensajes)
            Toast.makeText(this,"Se creó el archivo y está listo para descargar",Toast.LENGTH_SHORT)
                .show()
            file.flush()
            file.close()

        } catch (e: Exception) {
            AlertDialog.Builder(this)
                .setTitle("Error Saving")
                .setMessage(e.message.toString())
                .show()
        }

    }

    private fun mostrarLista(datos: ArrayList<String>) {
        binding.lista.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_multiple_choice,datos)
        binding.lista.choiceMode = ListView.CHOICE_MODE_MULTIPLE

    }
}