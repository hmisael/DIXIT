package com.example.dixit_app.view.categorias

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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dixit_app.view.CategoriasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Categoria
import com.example.dixit_app.model.repository.CategoriaRepository
import com.example.dixit_app.viewmodel.CategoriaViewModel
import com.example.dixit_app.viewmodel.CategoriaViewModelFactory
import com.google.android.material.snackbar.Snackbar

class CategoriasFragment : Fragment(), SearchView.OnQueryTextListener,
                        CategoriaClickInterface, CategoriaClickDeleteInterface {

    private lateinit var binding: FragmentCategoriasBinding

    private lateinit var categoriaViewModel: CategoriaViewModel
    private lateinit var categoriaAdapter: CategoriaAdapter


    // Inicializa y crea todos los objetos
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.categorias)

        val application = requireNotNull(this.activity).application

        val categoriaRepository = CategoriaRepository(DixitDatabase(application))

        val viewModelProviderFactory = CategoriaViewModelFactory(application, categoriaRepository)

        categoriaViewModel =
            ViewModelProvider(this, viewModelProviderFactory)[CategoriaViewModel::class.java]

        setUpRecyclerView()

        binding.fabAddCategoria.setOnClickListener {
            agregarCategoria()
        }
    }

    private fun setUpRecyclerView() {
        //Iniciar Adapter enviando el mismo fragment (distinta implementación interface)
        categoriaAdapter = CategoriaAdapter(this, this)

        binding.listaRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = categoriaAdapter
        }

        activity?.let {
            categoriaViewModel.getAllCategorias().observe(viewLifecycleOwner) { categorias ->
                categoriaAdapter.differ.submitList(categorias)
                //Si no hay categorías, falta la predeterminada.
                updateUI(categorias)
            }
        }
    }

    private fun agregarCategoria(){
        val inflater = LayoutInflater.from(this.context)
        val v =inflater.inflate(R.layout.agregar_dialog, null)
        //setear vista
        v.findViewById<TextView>(R.id.tv_descripcion).setText("Nueva Categoría")
        val nombreCategoria = v.findViewById<EditText>(R.id.et_nombre_elemento)
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setView(v)

        addDialog.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = nombreCategoria.text.toString()
            saveCategoria(this.requireView(), titulo)
            dialog.dismiss()
        }

        addDialog.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        addDialog.create()
        addDialog.show()
    }

    private fun saveCategoria(view: View, titulo: String) {
        if (titulo.isNotEmpty()) {
            val categoria = Categoria(0, titulo)

            categoriaViewModel.insertCategoria(categoria)

            Snackbar.make(view, "Categoría guardada exitosamente", Snackbar.LENGTH_SHORT).show()

            val direction =
                CategoriasFragmentDirections.actionCategoriasFragmentToCategoriaDetalleFragment(
                    categoria
                )
            view.findNavController().navigate(direction)
        } else {
            Toast.makeText(context, "Por favor ingrese nombre de Categoría" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(categorias: List<Categoria>) {
        if (categorias.isNotEmpty()) {
            binding.imageView.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.listaRecyclerView.visibility = View.VISIBLE
        } else {
            binding.imageView.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.listaRecyclerView.visibility = View.GONE
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
        /*if (query != null) {
            searchNote(query)
        }*/
        return false
    }

   override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarCategoria(newText)
        }
        return true
    }

    private fun buscarCategoria(query: String?) {
        val searchQuery = "%$query%"
        categoriaViewModel.searchCategoria(searchQuery).observe(this) { list ->
            categoriaAdapter.differ.submitList(list)
        }
    }

    override fun onCategoriaClick(categoria: Categoria) {
        //Abre el Fragment para modificar la categoría seleccionada
        val direction =
            CategoriasFragmentDirections.actionCategoriasFragmentToCategoriaDetalleFragment(categoria)
        view?.findNavController()?.navigate(direction)
    }

    override fun onDeleteCategoriaClick(categoria: Categoria) {
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setTitle("Eliminar Categoría")
        addDialog.setMessage("¿Desea eliminar la categoría seleccionada?")
        addDialog.setPositiveButton("Eliminar", DialogInterface.OnClickListener{dialog, _ ->
            //Se usa el método del ViewModel para eliminar la nota seleccionada
            categoriaViewModel.deleteCategoria(categoria)
            //Informar al usuario
            Toast.makeText(context, "Categoria ${categoria.nombreCategoria} eliminada", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        })

        addDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener{ dialog, _ ->
            dialog.dismiss()
        })

        addDialog.create()
        addDialog.show()
    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/
}