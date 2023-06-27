package com.example.dixit_app.view.categorias

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.view.CategoriasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriaDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Categoria
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.repository.PictogramaRepository
import com.example.dixit_app.view.pictogramas.PictogramaAdapter
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory


class CategoriaDetalleFragment : Fragment() ,
    SearchView.OnQueryTextListener {

    //Patrón binding layout de diseño para este Fragment
    private lateinit var binding: FragmentCategoriaDetalleBinding

    //Instancia contexto de navegación de este Fragment
    private val args: CategoriaDetalleFragmentArgs by navArgs()
    //Inicializo var Categoria para instanciar la categoría seleccionada previamente
    private lateinit var actualCategoria: Categoria

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel
    //para mostrar recycler view con la lista de pictogramas

    private lateinit var pictogramaAdapter: PictogramaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoriaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as CategoriasActivity).supportActionBar?.title = getString(R.string.detalle_categorias)

        val application = requireNotNull(this.activity).application

        val pictogramaRepository = PictogramaRepository(
                DixitDatabase(application)
        )

        val viewModelProviderFactory = PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory).get(PictogramaViewModel::class.java)

        //Capturar la entidad Categoria de la elección de la lista Categorias
        actualCategoria = args.categoria!!

        //Carga de datos en el RecyclerView
        setUpRecyclerViewPictogramas()

        binding.fabDone.setOnClickListener {
            val categoriaDialog = AlertDialog.Builder(this.requireContext())
            categoriaDialog.setTitle("Cargar pictogramas:")
            val pictureDialogItem = arrayOf("Carga múltiple", "Carga individual", "Cancelar")

            //Abrir dialog con dos opciones: abrir cámara o seleccionar de galería
            categoriaDialog.setItems(pictureDialogItem){ dialog, which->
                when (which){
                    0 -> cargaMultiple(dialog)
                    1 -> cargaIndividual(dialog)
                    2 -> dialog.dismiss()
                }
            }
            categoriaDialog.show()
        }
    }

    private fun cargaMultiple(dialog : DialogInterface){
        val direction =
            CategoriaDetalleFragmentDirections.actionCategoriasDetalleFragmentToCategoriaPredeterminadaFragment(
                actualCategoria)
        view?.findNavController()?.navigate(direction)
        dialog.dismiss()
    }

    private fun cargaIndividual(dialog : DialogInterface) {
        //Enviar nombreCategoria al PictogramaNuevoFragment
        val direction =
            CategoriaDetalleFragmentDirections.actionCategoriaDetalleFragmentToPictogramaNuevoFragment(
                actualCategoria
            )
        view?.findNavController()?.navigate(direction)
        //Sin parámetro:
        // it.findNavController().navigate(R.id.action_categoriaDetalleFragment_to_pictogramaNuevoFragment)
        dialog.dismiss()
    }

    private fun setUpRecyclerViewPictogramas() {
        pictogramaAdapter = PictogramaAdapter()

        binding.listaPictogramasRecyclerView.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                GridLayoutManager(requireContext(), 3)
            } else {
                // Orientación horizontal
                GridLayoutManager(requireContext(), 5)
            }
            setHasFixedSize(true)
            adapter = pictogramaAdapter
        }

        activity?.let {
            pictogramaViewModel.getPictogramasByCategoria(actualCategoria.nombreCategoria)
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramaAdapter.differ.submitList(pictogramas)
                    updateUI(pictogramas)
                }
        }

    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.clCategoriaDetalle.visibility = View.GONE
            binding.listaPictogramasRecyclerView.visibility = View.VISIBLE
        } else {
            binding.clCategoriaDetalle.visibility = View.VISIBLE
            binding.listaPictogramasRecyclerView.visibility = View.GONE
        }
    }

    //TODO editar implementación

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_buscar, menu)
        val menuBusqueda = menu.findItem(R.id.menu_buscar_elemento).actionView as SearchView
        menuBusqueda.isSubmitButtonEnabled = false
        menuBusqueda.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        /*if (query != null) {
            searchNote(query)
        }*/
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarPictograma(newText)
        }
        return true
    }

    private fun buscarPictograma(query: String?) {
        val searchQuery = "%$query%"
        //Acá, además del término buscado, debe ir la Categoría a la que pertenece el Pictograma
        pictogramaViewModel.searchPictograma(searchQuery).observe(this) { list ->
            pictogramaAdapter.differ.submitList(list)
        }
    }
/*
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/
}