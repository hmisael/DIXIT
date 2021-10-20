package com.example.dixit_app.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentRutinaNuevaPictogramasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.*


class RutinaNuevaPictogramas : Fragment(R.layout.fragment_rutina_nueva_pictogramas),
    SearchView.OnQueryTextListener {


    private var _binding: FragmentRutinaNuevaPictogramasBinding? = null
    private val binding get() = _binding!!

    private lateinit var pictogramaViewModel: PictogramaViewModel
    private lateinit var pictogramaSeleccionAdapter: PictogramaSeleccionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentRutinaNuevaPictogramasBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application

        val pictogramaRepository = PictogramaRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory =
            PictogramaViewModelFactory(
                application, pictogramaRepository
            )

        pictogramaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory)
            .get(PictogramaViewModel::class.java)

        //----------------------------------

        setUpRecyclerView()

    }


    private fun setUpRecyclerView() {
        pictogramaSeleccionAdapter = PictogramaSeleccionAdapter()
        binding.listaPictogramasRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramaSeleccionAdapter
        }

        //CONFLICTO


        activity?.let {
            pictogramaViewModel.getAllPictogramas().observe(viewLifecycleOwner, { pictogramas ->
                pictogramaSeleccionAdapter.differ.submitList(pictogramas)
                updateUI(pictogramas)
            })
        }

    }


    private fun updateUI(pictogramas: List<Pictograma>) {

        if (pictogramas.isNotEmpty()) {
            binding.cardView.visibility = View.GONE
            binding.listaPictogramasRecyclerView.visibility = View.VISIBLE

        } else {
            binding.cardView.visibility = View.VISIBLE
            binding.listaPictogramasRecyclerView.visibility = View.GONE

        }
    }


    //Idem método y menú_buscar implementado en los demás Fragments
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_buscar, menu)
        val mMenuSearch = menu.findItem(R.id.menu_buscar).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
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
        pictogramaViewModel.searchPictograma(searchQuery).observe(
            this, { list ->
                pictogramaSeleccionAdapter.differ.submitList(list)
            }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}