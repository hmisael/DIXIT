package com.example.dixit_app.view.preguntas

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dixit_app.view.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPreguntasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pregunta
import com.example.dixit_app.model.entities.Respuesta
import com.example.dixit_app.model.repository.PreguntaRepository
import com.example.dixit_app.viewmodel.PreguntaViewModelFactory
import com.example.dixit_app.viewmodel.PreguntaViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PreguntasFragment :  Fragment(), SearchView.OnQueryTextListener,
                        PreguntaClickInterface, PreguntaClickDeleteInterface {

    private lateinit var binding: FragmentPreguntasBinding

    private lateinit var preguntaViewModel: PreguntaViewModel
    private lateinit var preguntaAdapter: PreguntaAdapter

    var idPreguntaBD = 0L
    var idRespuestaBD = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPreguntasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as PreguntasActivity).supportActionBar?.title = getString(R.string.preguntas)

        val application = requireNotNull(this.activity).application

        //Inicializar repositorio y viewmodel de Pregunta
        val preguntaRepository = PreguntaRepository(DixitDatabase(application))

        val viewModelProviderFactory1 = PreguntaViewModelFactory(application, preguntaRepository)

        preguntaViewModel = ViewModelProvider(this, viewModelProviderFactory1)
            .get(PreguntaViewModel::class.java)

        //Botón FAB para agregar una nueva pregunta
        setUpRecyclerView()

        binding.fabAddPregunta.setOnClickListener {
            agregarPregunta()
        }
    }

    private fun setUpRecyclerView() {
        preguntaAdapter = PreguntaAdapter(this, this)

        binding.listaPreguntasRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = preguntaAdapter
        }

        activity?.let {
            preguntaViewModel.getAllPreguntas().observe(viewLifecycleOwner) { preguntas ->
                preguntaAdapter.differ.submitList(preguntas)
                updateUI(preguntas)
            }
        }
    }

    private fun agregarPregunta(){
        val inflater = LayoutInflater.from(this.context)
        val v =inflater.inflate(R.layout.agregar_dialog, null)
        //setear vista
        v.findViewById<TextView>(R.id.tv_descripcion).setText("Nueva Pregunta:")
        val nombrePregunta = v.findViewById<EditText>(R.id.et_nombre_elemento)
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setView(v)

        addDialog.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = nombrePregunta.text.toString()
            savePregunta(this.requireView(), titulo)
            dialog.dismiss()
        }

        addDialog.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        addDialog.create()
        addDialog.show()
    }

    private fun savePregunta(view: View, titulo: String) {
        if (titulo.isNotEmpty()) {
            //Inicializar con un  valor por defecto para Pregunta
            val pregunta = Pregunta(0, titulo)

            //También necesito crear la Respuesta (vacía) asociada a esta Pregunta
            val respuesta = Respuesta(0, titulo)

            lifecycleScope.launch {
                //Guardar la Pregunta en BD ,capturando el idPregunta generado
                idPreguntaBD = preguntaViewModel.insertPregunta(pregunta)
                //Instanciar una Pregunta con el idPregunta generado para seguir navegando
                val preguntaBD = Pregunta(idPreguntaBD, titulo)

                //Guardo la Respuesta asociada a esta Pregunta
                //No necesito su id, ya que me importa la Pregunta para nav al Fragment
                idRespuestaBD = preguntaViewModel.insertRespuesta(respuesta)
                //val respuestaBD = Respuesta(idRespuestaBD, titulo)

                Snackbar.make(view, "Pregunta guardada exitosamente", Snackbar.LENGTH_SHORT).show()

                //Luego de guardar, es necesario ir al Detalle de la Categoría
                val direction = PreguntasFragmentDirections
                    .actionPreguntasFragmentToPreguntaDetalleFragment(preguntaBD)
                view.findNavController().navigate(direction)
            }
            //Notificar datos actualizados al Adapter
            //preguntaAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(context, "Por favor ingrese nombre de la Pregunta" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(preguntas: List<Pregunta>) {
        if (preguntas.isNotEmpty()) {
            binding.imageWarningPreguntas.visibility = View.GONE
            binding.textWarningPreguntas.visibility = View.GONE
            binding.listaPreguntasRecyclerView.visibility = View.VISIBLE
        } else {
            binding.imageWarningPreguntas.visibility = View.VISIBLE
            binding.textWarningPreguntas.visibility = View.VISIBLE
            binding.listaPreguntasRecyclerView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_buscar, menu)
        val busquedaMenu = menu.findItem(R.id.menu_buscar_elemento).actionView as SearchView
        busquedaMenu.isSubmitButtonEnabled = false
        busquedaMenu.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            buscarPregunta(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarPregunta(newText)
        }
        return true
    }

    private fun buscarPregunta(query: String?) {
        val searchQuery = "%$query%"
        preguntaViewModel.searchPregunta(searchQuery).observe(this) { list ->
            preguntaAdapter.differ.submitList(list)
        }
    }

    override fun onPreguntaClick(pregunta: Pregunta) {
        //Abre el Fragment para modificar la rutina seleccionada
        val direction = PreguntasFragmentDirections
            .actionPreguntasFragmentToPreguntaDetalleFragment(pregunta)
        view?.findNavController()?.navigate(direction)
    }

    override fun onDeletePreguntaClick(pregunta: Pregunta) {
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setTitle("Eliminar Pregunta")
        addDialog.setMessage("¿Desea eliminar la pregunta seleccionada?")
        addDialog.setPositiveButton("Eliminar") { dialog, _ ->
            //Se usa el método del ViewModel para eliminar la nota seleccionada
            preguntaViewModel.deletePregunta(pregunta)

            //Informar al usuario
            Toast.makeText(context, "Pregunta ${pregunta.nombrePregunta} eliminada", Toast.LENGTH_LONG)
                .show()
            dialog.dismiss()
        }

        addDialog.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

    override fun onPause() {
        super.onPause()
    }

}