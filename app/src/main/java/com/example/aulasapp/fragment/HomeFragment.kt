package com.example.aulasapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aulasapp.Aula
import com.example.aulasapp.adapter.CostumAdapter
import com.example.aulasapp.MainActivity
import com.example.aulasapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var aulas: ArrayList<Aula>
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CostumAdapter
    private lateinit var logout: Button
    private lateinit var email:String
    private var rol:Number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        email = arguments?.get("email").toString()
        db = FirebaseFirestore.getInstance()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        recyclerView = view.findViewById(R.id.recyclerViewHome)

        recyclerView.layoutManager = LinearLayoutManager(context)

        aulas = arrayListOf()

        val card = R.layout.card_layout_home

        db.collection("usuarios").document(email).get().addOnSuccessListener {
            rol = it.data?.get("rol") as Number
            verificarTitulo()
            adapter =
                CostumAdapter(aulas, rol, onClickDelete = { id -> reservarAula(id) }, card, "Home")
            recyclerView.adapter = adapter
            logout = view.findViewById(R.id.logout)

            generarAulas()
            ingresarHome()
        }
    }

    private fun verificarTitulo() {
        val titulo = view?.findViewById<TextView>(R.id.home_title)
        if (!esProfesor(rol))
            titulo!!.text = "AULAS DISPONIBLES"
        else titulo!!.text = "RESERVA TU AULA"
    }

    private fun esProfesor(rol: Number): Boolean {
        return rol.toInt() == 1
    }

    private fun reservarAula(id: String) {
        if(esProfesor(rol)) {
            val aula = db.collection("aulas").document(id)
            aula.update("estado", false)
            aula.update("reservadoPor", email)
            var posicion = 0

            for (aulaAux in aulas) {
                if (aulaAux.id == id) {
                    break
                }
                posicion++
            }

            aulas.removeAt(posicion)
            adapter.notifyItemRemoved(posicion)
        }
    }

    private fun agregarAula(aula: QueryDocumentSnapshot){
        if (aula.data["estado"] == true) {
            aulas.add(Aula(aula.id, "Disponible"))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun generarAulas() {

         db.collection("aulas")
        .get()
        .addOnSuccessListener { result ->
            for (aula in result) {
                agregarAula(aula)
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun ingresarHome() {
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, MainActivity::class.java))
        }
    }
}