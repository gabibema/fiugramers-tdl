package com.example.aulasapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var btn_logout = findViewById<Button>(R.id.logout)
        ingresarHome(btn_logout);
    }

    private fun ingresarHome(logout:Button){
        mostrarAulas()
        logout.setOnClickListener{
            //generarAulas() // genera las aulas en la bd
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun mostrarAulas(){
        val db = Firebase.firestore

        db.collection("aulas")
            .get()
            .addOnSuccessListener { aulas ->
                for(aula in aulas){
                    println("AULA: ${aula.id} - ESTADO: ${aula.data.get("estado")}")
                }
            }.addOnFailureListener { exception ->
                println("Error")
            }
    }

    private fun generarAulas(){
        val db = Firebase.firestore

        for (i in 102..110){
            val aula = hashMapOf(
                "estado" to true
            )

            db.collection("aulas")
                .document(i.toString())
                .set(aula)
        }
    }
}