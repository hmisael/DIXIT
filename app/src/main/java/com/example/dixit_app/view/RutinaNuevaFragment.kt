package com.example.dixit_app.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.dixit_app.R
import com.example.dixit_app.RutinasActivity
import com.example.dixit_app.databinding.FragmentRutinaNuevaBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.viewmodel.RutinaViewModel
import com.google.android.material.snackbar.Snackbar

class RutinaNuevaFragment : Fragment(){
    /*
    DialogFragment(R.layout.fragment_rutina_nueva) {

    private var _binding: FragmentRutinaNuevaBinding? = null
    private val binding get() = _binding!!
    private lateinit var rutinaViewModel: RutinaViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRutinaNuevaBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rutinaViewModel = (activity as RutinasActivity).rutinaViewModel
        mView = view
    }

    private fun saveRutina(view: View) {
        val nombreRutina = binding.rutinaTxt.text.toString().trim()

        if (nombreRutina.isNotEmpty()) {
            val rutina = Rutina(0, nombreRutina)
            //val note = Note(0, noteTitle)

            rutinaViewModel.insertRutina(rutina)
            Snackbar.make(
                view, "Rutina guardada exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()
            view.findNavController().navigate(R.id.action_rutinaNuevaFragment_to_rutinasFragment)

        } else {
            activity?.toast("Por favor ingrese nombre de Rutina")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        //inflate layout XML
        inflater.inflate(R.menu.rutina_nueva_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //accedo al layout a través de su id
            R.id.rutina_nueva_menu -> {
                saveRutina(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
*/

}