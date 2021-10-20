package com.example.dixit_app.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dixit_app.CategoriasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriaNuevaBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.repository.CategoriaRepository
import com.example.dixit_app.viewmodel.CategoriaViewModel
import com.example.dixit_app.viewmodel.CategoriaViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar


class CategoriaNuevaFragment : DialogFragment(R.layout.fragment_categoria_nueva) {

    private var _binding: FragmentCategoriaNuevaBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoriaViewModel: CategoriaViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoriaNuevaBinding.inflate(

            inflater,
            container,
            false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.nueva_categoria)

        //-----CAMBIO VIEWMODEL--------------------
        //categoriaViewModel = (activity as CategoriasActivity).categoriaViewModel


        val application = requireNotNull(this.activity).application

        val categoriaRepository = CategoriaRepository(
                DixitDatabase(application)
        )

        val viewModelProviderFactory =
                CategoriaViewModelFactory(
                        application, categoriaRepository
                )

        categoriaViewModel = ViewModelProvider(
                this,
                viewModelProviderFactory)
                .get(CategoriaViewModel::class.java)

        //----------------------


        mView = view
    }

    /*
    private fun saveCategoria(view: View) {
        val nombreCategoria = binding.categoriaTxt.text.toString().trim()

        if (nombreCategoria.isNotEmpty()) {
            val categoria = Categoria(0, nombreCategoria)
            //val note = Note(0, noteTitle)

            categoriaViewModel.insertCategoria(categoria)
            Snackbar.make(
                view, "Categoría guardada exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()
            view.findNavController().navigate(R.id.action_categoriaNuevaFragment_to_categoriasFragment)

        } else {
            activity?.toast("Por favor ingrese nombre de Categoría")
        }
    }*/


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        //inflate layout XML
        inflater.inflate(R.menu.menu_guardar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                //saveCategoria(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}