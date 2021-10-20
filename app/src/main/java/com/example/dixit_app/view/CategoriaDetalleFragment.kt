package com.example.dixit_app.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.CategoriasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriaDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory


class CategoriaDetalleFragment : Fragment(R.layout.fragment_categoria_detalle) ,
    SearchView.OnQueryTextListener {
    //Patrón binding layout de diseño para este Fragment
    private var _binding: FragmentCategoriaDetalleBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: CategoriaDetalleFragmentArgs by navArgs()
    //Inicializo var Categoria para instanciar la categoría seleccionada previamente
    private lateinit var currentCategoria: Categoria

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel
    //para mostrar recycler view con la lista de pictogramas

    private lateinit var pictogramaAdapter: PictogramaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoriaDetalleBinding.inflate(inflater, container, false)
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

        val viewModelProviderFactory =
                PictogramaViewModelFactory(
                        application, pictogramaRepository
                )

        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory
        ).get(PictogramaViewModel::class.java)

        //Capturar la entidad Categoria de la elección de la lista Categorias
        currentCategoria = args.categoria!!

        //---------CARGO DATOS EN RECYCLER VIEW--------
        setUpRecyclerViewPictogramas()
        //---------------
        //CARGO CAJAS DE TEXTO SEGÚN EL ARGUMENTO DE ENTRADA
        //binding.categoriaModTxt.setText(currentCategoria.nombreCategoria)
        //------------------------------------

        binding.fabDone.setOnClickListener {
            //Enviar nombreCategoria al PictogramaNuevoFragment
            val direction = CategoriaDetalleFragmentDirections
                    .actionCategoriaDetalleFragmentToPictogramaNuevoFragment(currentCategoria.nombreCategoria)
            view.findNavController().navigate(direction)
            //Sin argumento it's like...
            // it.findNavController().navigate(R.id.action_categoriaDetalleFragment_to_pictogramaNuevoFragment)
        }
    }

    private fun setUpRecyclerViewPictogramas() {
        pictogramaAdapter = PictogramaAdapter()
        binding.listaPictogramasRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramaAdapter
        }


        activity?.let {
            pictogramaViewModel.getPictogramasByCategoria(currentCategoria.nombreCategoria)
                .observe(viewLifecycleOwner, { pictogramas ->
                pictogramaAdapter.differ.submitList(pictogramas)
                updateUI(pictogramas)
            })
        }
    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.cardView3.visibility = View.GONE
            binding.listaPictogramasRecyclerView.visibility = View.VISIBLE
        } else {
            binding.cardView3.visibility = View.VISIBLE
            binding.listaPictogramasRecyclerView.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_buscar, menu)
        val menuBusqueda = menu.findItem(R.id.menu_buscar).actionView as SearchView
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
        pictogramaViewModel.searchPictograma(searchQuery).observe(
            this, { list ->
                pictogramaAdapter.differ.submitList(list)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}