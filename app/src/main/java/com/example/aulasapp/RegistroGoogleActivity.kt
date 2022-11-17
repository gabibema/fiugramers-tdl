package com.example.aulasapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import com.example.aulasapp.classes.Home
import com.example.aulasapp.classes.Error
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistroGoogleActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var email:String
    private lateinit var nombre: String
    private lateinit var aceptar:Button
    private lateinit var alumno: RadioButton
    private lateinit var profesor: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_google)

        email = intent.getStringExtra("email").toString()
        nombre = intent.getStringExtra("nombre").toString()
        alumno = findViewById(R.id.alumno)
        profesor = findViewById(R.id.profesor)
        aceptar = findViewById(R.id.aceptar)

        val home = Home(email)
        aceptar.setOnClickListener{
            if (profesor.isChecked) {
                guardarBaseDatos(1)
                home.ingresar(this)
            } else if (alumno.isChecked) {
                guardarBaseDatos(2)
                home.ingresar(this)
            }else{
                val error = Error()
                error.mostrar("Debe seleccionar un rol",this)
            }
        }
    }

    private fun guardarBaseDatos(rol: Int){
        db.collection("usuarios")
            .document(email).set(
                hashMapOf(
                    "nombre" to nombre,
                    "apellido" to "apellido",
                    "rol" to rol
                )
            )
    }
}