package com.example.aulasapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class HomeActivity : AppCompatActivity() {

    private lateinit var aulas: ArrayList<Aula>
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CostumAdapter
    private lateinit var logout: Button
    private lateinit var reservar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        generarAulas()

        logout = findViewById(R.id.logout)
        recyclerView = findViewById(R.id.recyclerView)


        recyclerView.layoutManager = LinearLayoutManager(this)

        aulas = arrayListOf()

        adapter =
            CostumAdapter(aulas, onClickListener = { id, posicion,boton -> reservarAula(id, posicion,boton) })

        recyclerView.adapter = adapter
        ingresarHome()
    }

    private fun reservarAula(id: String, posicion: Int,boton:Button) {
        val aula = db.collection("aulas").document(id)
        aula.update("estado", false)
        aulas[posicion].estado = "Ocupado"
        boton.visibility = View.INVISIBLE
        adapter.notifyItemChanged(posicion)
        //adapter.notifyDataSetChanged()

    }


    private fun ingresarHome() {
        logout.setOnClickListener {
            //generarAulas() // genera las aulas en la bd
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun generarAulas() {
        db = FirebaseFirestore.getInstance()
        db.collection("aulas")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    for (aula: DocumentChange in value?.documentChanges!!) {
                        if (aula.type == DocumentChange.Type.ADDED) {
                            if (aula.document.data["estado"] == true) {
                                aulas.add(Aula(aula.document.id, "Disponible"))
                            } else {
                                aulas.add(Aula(aula.document.id, "Ocupado"))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                }
            })
    }
}
/*
    private fun generarAulas(descripcionAulas:TextView){
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        var texto: String = ""

        db.collection("aulas")
            .get() //obtengo todos los datos
            .addOnSuccessListener { aulas ->
                for(aula in aulas){
                    if(aula.data.get("estado") == true){
                        texto += "AULA: ${aula.id} - ESTADO: Disponible\n"
                    }else{
                        texto += "AULA: ${aula.id} - ESTADO: Ocupado\n"
                    }

                }
                descripcionAulas?.text = texto
            }

            .addOnFailureListener { exception ->
                println("Error")
            }


    }*/

 /*   private fun generarAulas(){
        val db = FirebaseFirestore.getInstance()

        for (i in 102..110){
            val aula = hashMapOf(
                "estado" to true
            )

            db.collection("aulas")
                .document(i.toString())
                .set(aula)
        }
    }*/