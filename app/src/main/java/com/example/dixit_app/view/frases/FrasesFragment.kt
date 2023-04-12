package com.example.dixit_app.view.frases

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
import com.example.dixit_app.view.FrasesActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentFrasesBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Frase
import com.example.dixit_app.model.repository.FraseRepository
import com.example.dixit_app.viewmodel.FraseViewModel
import com.example.dixit_app.viewmodel.FraseViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FrasesFragment : Fragment(), SearchView.OnQueryTextListener,
    FraseClickInterface, FraseClickDeleteInterface {

    private lateinit var binding: FragmentFrasesBinding

    private lateinit var fraseViewModel: FraseViewModel
    private lateinit var fraseAdapter: FraseAdapter

    var idFraseBD = 0L

    // Inicializa y crea todos los objetos
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        binding = FragmentFrasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as FrasesActivity).supportActionBar?.title = getString(R.string.frases)

        val application = requireNotNull(this.activity).application

        val fraseRepository = FraseRepository(DixitDatabase(application))

        val viewModelProviderFactory = FraseViewModelFactory(application, fraseRepository)

        fraseViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(FraseViewModel::class.java)

        setUpRecyclerView()

        binding.fabAddFrase.setOnClickListener {
            agregarFrase()
        }
    }

    private fun setUpRecyclerView() {
        fraseAdapter = FraseAdapter(this, this)

        binding.listaFrasesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = fraseAdapter
        }

        activity?.let {
            fraseViewModel.getAllFrases().observe(viewLifecycleOwner) { frases ->
                fraseAdapter.differ.submitList(frases)
                updateUI(frases)
            }
        }
    }

    private fun agregarFrase(){
        val inflater = LayoutInflater.from(this.context)
        val v =inflater.inflate(R.layout.agregar_dialog, null)
        //setear vista
        v.findViewById<TextView>(R.id.tv_descripcion).setText("Nueva Frase")
        val nombreFrase = v.findViewById<EditText>(R.id.et_nombre_elemento)
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setView(v)

        addDialog.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = nombreFrase.text.toString()
            saveFrase(this.requireView(), titulo)
            dialog.dismiss()
        }

        addDialog.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        addDialog.create()
        addDialog.show()
    }

    private fun saveFrase(view: View, titulo: String) {
        if (titulo.isNotEmpty()) {
            //Inicializar con un  valor por defecto
            val frase = Frase(0, titulo)

            lifecycleScope.launch {
                //Guardar la Frase en BD ,capturando el idFrase generado
                idFraseBD = fraseViewModel.insertFrase(frase)
                //Instanciar una Frase con el idFrase generado para seguir navegando
                val fraseBD = Frase(idFraseBD, titulo)

                Snackbar.make(view, "Frase guardada exitosamente", Snackbar.LENGTH_SHORT).show()

                //Luego de guardar, es necesario ir al Detalle de la Categoría
                val direction = FrasesFragmentDirections
                    .actionFrasesFragmentToFraseDetalleFragment(fraseBD)
                view.findNavController().navigate(direction)
            }

            Snackbar.make(
                requireView(), "Frase creada exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(context, "Por favor ingrese nombre de Frase" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(frases: List<Frase>) {
        if (frases.isNotEmpty()) {
            binding.imageView.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.listaFrasesRecyclerView.visibility = View.VISIBLE
        } else {
            binding.imageView.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.listaFrasesRecyclerView.visibility = View.GONE
        }
    }


    //TODO editar implementación
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
            buscarFrase(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarFrase(newText)
        }
        return true
    }

    private fun buscarFrase(query: String?) {
        val searchQuery = "%$query%"
        fraseViewModel.searchFrase(searchQuery).observe(this) { list ->
            fraseAdapter.differ.submitList(list)
        }
    }

    override fun onFraseClick(frase: Frase) {
        //Abre el Fragment para modificar la frase seleccionada
        val direction = FrasesFragmentDirections
            .actionFrasesFragmentToFraseDetalleFragment(frase)
        view?.findNavController()?.navigate(direction)
    }

    override fun onDeleteFraseClick(frase: Frase) {
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setTitle("Eliminar Frase")
        addDialog.setMessage("¿Desea eliminar la frase seleccionada?")
        addDialog.setPositiveButton("Eliminar") { dialog, _ ->
            //Se usa el método del ViewModel para eliminar la nota seleccionada
            fraseViewModel.deleteFrase(frase)
            //Informar al usuario
            Toast.makeText(context, "Frase ${frase.nombreFrase} eliminada", Toast.LENGTH_LONG)
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