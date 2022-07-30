package com.example.dixit_app.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dixit_app.FrasesActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentFrasesBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.repository.FraseRepository
import com.example.dixit_app.viewmodel.FraseViewModel
import com.example.dixit_app.viewmodel.FraseViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FrasesFragment : Fragment(R.layout.fragment_frases),
    SearchView.OnQueryTextListener {

    private var _binding: FragmentFrasesBinding? = null
    private val binding get() = _binding!!

    private lateinit var fraseViewModel: FraseViewModel
    private lateinit var fraseAdapter: FraseAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFrasesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as FrasesActivity).supportActionBar?.title = getString(R.string.frases)

        val application = requireNotNull(this.activity).application


        val fraseRepository = FraseRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory =
            FraseViewModelFactory(
                application, fraseRepository
            )

        fraseViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory)
            .get(FraseViewModel::class.java)

        //----------------------------------

        setUpRecyclerView()
        binding.fabAddFrase.setOnClickListener {
            //it.findNavController().navigate(R.id.action_categoriasFragment_to_categoriaNuevaFragment)
            agregarFrase()
        }
    }

    private fun setUpRecyclerView() {
        fraseAdapter = FraseAdapter()
        binding.listaFrasesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = fraseAdapter
        }

        activity?.let {
            fraseViewModel.getAllFrases().observe(viewLifecycleOwner, { frases ->
                fraseAdapter.differ.submitList(frases)
                updateUI(frases)
            })
        }
    }

    private fun agregarFrase(){
        val inflater = LayoutInflater.from(this.context)
        val v =inflater.inflate(R.layout.categoria_add_item, null)

        //setear vista

        val nombreFrase = v.findViewById<EditText>(R.id.et_categoria)
        val addDialog = AlertDialog.Builder(activity!!)
        addDialog.setView(v)

        addDialog.setPositiveButton("Guardar", DialogInterface.OnClickListener{
                dialog, id ->
            val titulo = nombreFrase.text.toString()
            saveFrase(this.requireView(), titulo)
            dialog.dismiss()
        })
        addDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
                dialog, id ->
            dialog.dismiss()
        })
        addDialog.create()
        addDialog.show()
    }

    private fun saveFrase(view: View, titulo: String) {
        //val nombreCategoria = binding.categoriaTxt.text.toString().trim()

        if (titulo.isNotEmpty()) {
            val frase = Frase(0, titulo)
            //val note = Note(0, noteTitle)

            fraseViewModel.insertFrase(frase)
            Snackbar.make(
                view, "Frase guardada exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()
            //Código de Nuevo Fragmento , regresando a Categorias Fragment (esta clase)
            //view.findNavController().navigate(R.id.action_categoriaNuevaFragment_to_categoriasFragment)

            //Pero en esta clase, luego de guardar, es necesario ir al Detalle de la Categoría

            val direction = FrasesFragmentDirections
                .actionFrasesFragmentToFraseDetalleFragment(frase)
            view.findNavController().navigate(direction)

            //Notificar datos actualizados al Adapter
            fraseAdapter.notifyDataSetChanged()

        } else {
            activity?.toast("Por favor ingrese nombre de la Frase")
        }
    }

    private fun updateUI(frases: List<Frase>) {
        if (frases.isNotEmpty()) {
            binding.cardViewFrases.visibility = View.GONE
            binding.listaFrasesRecyclerView.visibility = View.VISIBLE
        } else {
            binding.cardViewFrases.visibility = View.VISIBLE
            binding.listaFrasesRecyclerView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_buscar, menu)
        val busquedaMenu = menu.findItem(R.id.menu_buscar).actionView as SearchView
        busquedaMenu.isSubmitButtonEnabled = false
        busquedaMenu.setOnQueryTextListener(this)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            buscarFrase(query)
        }
        return false
    }

    private fun buscarFrase(query: String?) {
        val searchQuery = "%$query%"
        fraseViewModel.searchFrase(searchQuery).observe(
            this, { list ->
                fraseAdapter.differ.submitList(list)
            }
        )
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarFrase(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}