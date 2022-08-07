package com.example.dixit_app.ui.categorias

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dixit_app.CategoriasActivity
import com.example.dixit_app.R
//import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriasBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.repository.CategoriaRepository
import com.example.dixit_app.viewmodel.CategoriaViewModel
import com.example.dixit_app.viewmodel.CategoriaViewModelFactory
import com.google.android.material.snackbar.Snackbar


class CategoriasFragment : Fragment(R.layout.fragment_categorias),
    SearchView.OnQueryTextListener,
    CategoriaClickInterface,
    CategoriaClickDeleteInterface {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoriaViewModel: CategoriaViewModel
    private lateinit var categoriaAdapter: CategoriaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Inicializa y crea todos los objetos
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.categorias)

        val application = requireNotNull(this.activity).application

        val categoriaRepository = CategoriaRepository(DixitDatabase(application))

        val viewModelProviderFactory = CategoriaViewModelFactory(application, categoriaRepository)

        categoriaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory)
            .get(CategoriaViewModel::class.java)


        categoriaAdapter = CategoriaAdapter(this, this)

        binding.listaRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = categoriaAdapter
        }

        activity?.let {
            categoriaViewModel.getAllCategorias().observe(
                viewLifecycleOwner
            ) { categorias ->
                categoriaAdapter.differ.submitList(categorias)

                //Si no hay categorías, falta la predeterminada.
                updateUI(categorias)
                //setPictogramasPredeterminados(categorias)

                if (categorias.isEmpty()) {
                    val categoriaDialog = AlertDialog.Builder(this.requireContext())
                    categoriaDialog.setTitle("Cargar pictogramas")
                    //categoriaDialog.setMessage("La aplicación actualmente no cuenta con pictogramas ¿Desea agregarlos desde la galería?")
                    val pictureDialogItem = arrayOf("Abrir galería", "Cancelar")

                    //Abrir dialog con dos opciones: abrir cámara o seleccionar de galería
                    categoriaDialog.setItems(pictureDialogItem){ dialog, which->
                        when (which){
                            0 -> crearCategoriaPredeterminada(dialog)
                            1 -> dialog.dismiss()
                        }
                    }
                    categoriaDialog.show()
                }


            }
        }


        binding.fabAddCategoria.setOnClickListener {
            agregarCategoria()
        }
    }


    //NEW
    private fun setPictogramasPredeterminados(categorias: List<Categoria>){


    }

    //NEW
    private fun crearCategoriaPredeterminada(dialog : DialogInterface){
        val categoria = Categoria(0, "Predeterminado")
        categoriaViewModel.insertCategoria(categoria)

        val direction = CategoriasFragmentDirections.actionCategoriasFragmentToCategoriaPredeterminadaFragment(
            categoria)
        view?.findNavController()?.navigate(direction)
        dialog.dismiss()
    }

    private fun setUpRecyclerView() {
    }

    private fun agregarCategoria(){
        val inflater = LayoutInflater.from(this.context)
        val v =inflater.inflate(R.layout.agregar_dialog, null)
        //setear vista
        val nombreCategoria = v.findViewById<EditText>(R.id.et_nombre_elemento)
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setView(v)

        addDialog.setPositiveButton("Guardar", DialogInterface.OnClickListener{
            dialog, id ->
                val titulo = nombreCategoria.text.toString()
                saveCategoria(this.requireView(), titulo)
                dialog.dismiss()
        })
        addDialog.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
            dialog, id ->
            dialog.dismiss()
        })
        addDialog.create()
        addDialog.show()
    }

    private fun saveCategoria(view: View, titulo: String) {
        if (titulo.isNotEmpty()) {
            val categoria = Categoria(0, titulo)

            categoriaViewModel.insertCategoria(categoria)

            Snackbar.make(view, "Categoría guardada exitosamente", Snackbar.LENGTH_SHORT).show()
            //Código de Nuevo Fragmento , regresando a Categorias Fragment (esta clase)
            //view.findNavController().navigate(R.id.action_categoriaNuevaFragment_to_categoriasFragment)

            //Pero en esta clase, luego de guardar, es necesario ir al Detalle de la Categoría

            val direction =
                CategoriasFragmentDirections.actionCategoriasFragmentToCategoriaDetalleFragment(
                    categoria
                )
            view.findNavController().navigate(direction)

            //Notificar datos actualizados al Adapter
            categoriaAdapter.notifyDataSetChanged()

        } else {
            activity?.toast("Por favor ingrese nombre de Categoría")
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
        categoriaViewModel.searchCategoria(searchQuery).observe(
            this, { list ->
                categoriaAdapter.differ.submitList(list)
            }
        )
    }

    override fun onCategoriaClick(categoria: Categoria) {
        //Abre el Fragment para modificar la categoría seleccionada

        val direction =
            CategoriasFragmentDirections.actionCategoriasFragmentToCategoriaDetalleFragment(
                categoria)
        view?.findNavController()?.navigate(direction)
    }

    override fun onDeleteCategoriaClick(categoria: Categoria) {
        val addDialog = AlertDialog.Builder(requireActivity())
        addDialog.setTitle("Eliminar Categoría")
        addDialog.setMessage("¿Desea eliminar la categoría seleccionada?")
        addDialog.setPositiveButton("Eliminar", DialogInterface.OnClickListener{
                dialog, id ->
            //Se usa el método del ViewModel para eliminar la nota seleccionada
            categoriaViewModel.deleteCategoria(categoria)

            //Informar al usuario
            Toast.makeText(context, "Categoria ${categoria.nombreCategoria} eliminada", Toast.LENGTH_LONG).show()
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