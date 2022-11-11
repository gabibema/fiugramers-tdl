package com.example.aulasapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aulasapp.Aula
import com.example.aulasapp.CostumAdapter
import com.example.aulasapp.MainActivity
import com.example.aulasapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        aulas = arrayListOf()

        adapter =
            CostumAdapter(aulas, onClickListener = { id, posicion,boton -> reservarAula(id, posicion,boton) })

        recyclerView.adapter = adapter

        logout = view.findViewById(R.id.logout)

        generarAulas()

        ingresarHome()
    }

    private fun reservarAula(id: String, posicion: Int,boton: Button) {
        val aula = db.collection("aulas").document(id)
        aula.update("estado", false)
        aulas[posicion].estado = "Ocupado"
        boton.visibility = View.INVISIBLE
        adapter.notifyItemChanged(posicion)
        //adapter.notifyDataSetChanged()

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

    private fun ingresarHome() {
        logout.setOnClickListener {
            //generarAulas() // genera las aulas en la bd
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, MainActivity::class.java))
        }
    }
}