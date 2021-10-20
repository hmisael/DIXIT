package com.example.dixit_app.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dixit_app.R
import com.example.dixit_app.RutinasActivity
import com.example.dixit_app.databinding.FragmentRutinaModificarBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.viewmodel.RutinaViewModel

class RutinaModificarFragment : Fragment(){

//(R.layout.fragment_rutina_modificar) {

/*
    private val args: RutinaModificarFragmentArgs by navArgs()

    private var _binding: FragmentRutinaModificarBinding? = null
    private val binding get() = _binding!!



    private lateinit var currentRutina: Rutina
    private lateinit var rutinaViewModel: RutinaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRutinaModificarBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rutinaViewModel = (activity as RutinasActivity).rutinaViewModel
        currentRutina = args.rutina!!

        //binding.etNoteBodyUpdate.setText(currentNote.noteBody)
        binding.rutinaModTxt.setText(currentRutina.nombreRutina)

        binding.fabDone.setOnClickListener {
            val title = binding.rutinaModTxt.text.toString().trim()
            //val body = binding.etNoteBodyUpdate.text.toString().trim()

            if (title.isNotEmpty()) {
                //val note = Note(currentNote.id, title, body)
                val rutina = Rutina(currentRutina.idRutina, title)
                rutinaViewModel.updateRutina(rutina)

                //Esto es correcto, pero falta Fragment Modificar
                //view.findNavController().navigate(R.id.action_rutinaModificarFragment_to_rutinasFragment)

            } else {
                activity?.toast("Ingrese un nombre de Categoría por favor")
            }
        }
    }

    private fun deleteRutina() {
        AlertDialog.Builder(activity).apply {
            setTitle("Eliminar Rutina")
            setMessage("¿Está seguro que desea eliminar esta Rutina?")
            setPositiveButton("ELIMINAR") { _, _ ->
                rutinaViewModel.deleteRutina(currentRutina)
                //Esto es correcto, pero falta Fragment Modificar
                /*view?.findNavController()?.navigate(
                    R.id.action_rutinaModificarFragment_to_rutinasFragment
                )*/
            }
            setNegativeButton("CANCELAR", null)
        }.create().show()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_borrar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteRutina()
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