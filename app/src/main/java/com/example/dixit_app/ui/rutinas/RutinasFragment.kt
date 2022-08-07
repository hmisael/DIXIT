package com.example.dixit_app.ui.rutinas

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dixit_app.R
import com.example.dixit_app.RutinasActivity
import com.example.dixit_app.databinding.FragmentRutinasBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.repository.RutinaRepository
import com.example.dixit_app.ui.frases.FrasesFragmentDirections
import com.example.dixit_app.viewmodel.RutinaViewModel
import com.example.dixit_app.viewmodel.RutinaViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RutinasFragment : Fragment(R.layout.fragment_rutinas),
    SearchView.OnQueryTextListener,
    RutinaClickInterface,
    RutinaClickDeleteInterface {

    private var _binding: FragmentRutinasBinding? = null
    private val binding get() = _binding!!

    private lateinit var rutinaViewModel: RutinaViewModel
    private lateinit var rutinaAdapter: RutinaAdapter

    var idRutinaBD = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View? {
        _binding = FragmentRutinasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as RutinasActivity).supportActionBar?.title = getString(R.string.rutinas)

        val application = requireNotNull(this.activity).application

        val rutinaRepository = RutinaRepository(DixitDatabase(application))

        val viewModelProviderFactory = RutinaViewModelFactory(application, rutinaRepository)

        rutinaViewModel = ViewModelProvider(
            this, viewModelProviderFactory)
            .get(RutinaViewModel::class.java)

        setUpRecyclerView()
        binding.fabAddRutina.setOnClickListener {
            agregarRutina()
        }
    }

    private fun setUpRecyclerView() {
        rutinaAdapter = RutinaAdapter(this, this)

        binding.listaRutinasRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = rutinaAdapter
        }

        activity?.let {
            rutinaViewModel.getAllRutinas().observe(
                viewLifecycleOwner, { rutinas ->
                    rutinaAdapter.differ.submitList(rutinas)
                    updateUI(rutinas)
            })
        }
    }

    private fun agregarRutina(){
        val inflater = LayoutInflater.from(this.context)
        val v =inflater.inflate(R.layout.agregar_dialog, null)
        //setear vista
        v.findViewById<TextView>(R.id.tv_descripcion).setText("Nueva Rutina")
        val nombreRutina = v.findViewById<EditText>(R.id.et_nombre_elemento)
        val addDialog = AlertDialog.Builder(activity!!)
        addDialog.setView(v)

        addDialog.setPositiveButton("Guardar", DialogInterface.OnClickListener{
            dialog, id ->
                val titulo = nombreRutina.text.toString()
                saveRutina(this.requireView(), titulo)
                dialog.dismiss()
        })
        addDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
            dialog, id -> dialog.dismiss()
        })
        addDialog.create()
        addDialog.show()
    }

    private fun saveRutina(view: View, titulo: String) {
        if (titulo.isNotEmpty()) {
            //Inicializar con un  valor por defecto
            val rutina = Rutina(0, titulo)

            lifecycleScope.launch {
                //Guardar la Rutina en BD ,capturando el idRutina generado
                idRutinaBD = rutinaViewModel.insertRutina(rutina)
                //Instanciar una Rutina con el idRutina generado para seguir navegando
                val rutinaBD = Rutina(idRutinaBD, titulo)

                Snackbar.make(view, "Rutina guardada exitosamente",
                    Snackbar.LENGTH_SHORT).show()

                //Luego de guardar, es necesario ir al Detalle de la Categoría
                val direction =
                    RutinasFragmentDirections.actionRutinasFragmentToRutinaDetalleFragment(rutinaBD)
                view.findNavController().navigate(direction)
            }
            //Notificar datos actualizados al Adapter
            rutinaAdapter.notifyDataSetChanged()
        } else {
            activity?.toast("Por favor ingrese nombre de Rutina")
        }
    }

    private fun updateUI(rutinas: List<Rutina>) {
        if (rutinas.isNotEmpty()) {
            binding.imageView.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.listaRutinasRecyclerView.visibility = View.VISIBLE
        } else {
            binding.imageView.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.listaRutinasRecyclerView.visibility = View.GONE
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
            buscarRutina(query)
        }
        return false
    }

    private fun buscarRutina(query: String?) {
        val searchQuery = "%$query%"
        rutinaViewModel.searchRutina(searchQuery).observe(
            this, { list ->
                rutinaAdapter.differ.submitList(list)
            }
        )
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarRutina(newText)
        }
        return true
    }









    override fun onRutinaClick(rutina: Rutina) {
        //Abre el Fragment para modificar la rutina seleccionada

        val direction =
            RutinasFragmentDirections.actionRutinasFragmentToRutinaDetalleFragment(rutina)
        view?.findNavController()?.navigate(direction)


    }

    override fun onDeleteRutinaClick(rutina: Rutina) {
        val addDialog = AlertDialog.Builder(activity!!)
        addDialog.setTitle("Eliminar Rutina")
        addDialog.setMessage("¿Desea eliminar la rutina seleccionada?")
        addDialog.setPositiveButton("Eliminar", DialogInterface.OnClickListener{
                dialog, id ->
            //Se usa el método del ViewModel para eliminar la nota seleccionada
            rutinaViewModel.deleteRutina(rutina)

            //Informar al usuario
            Toast.makeText(context, "Rutina ${rutina.nombreRutina} eliminada", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        })
        addDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
                dialog, id ->
            dialog.dismiss()
        })
        addDialog.create()
        addDialog.show()
    }











    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}