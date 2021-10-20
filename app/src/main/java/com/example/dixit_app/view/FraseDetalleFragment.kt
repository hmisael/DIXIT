package com.example.dixit_app.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.FrasesActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentFraseDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory


//Si está vacío, ir a FraseModificarFragment. Si tiene elementos, mostrar Frase
class FraseDetalleFragment : Fragment(R.layout.fragment_frase_detalle){

/*    class FraseDetalleFragment : Fragment(R.layout.fragment_frase_detalle) ,
        SearchView.OnQueryTextListener {*/

    //Patrón binding layout de diseño para este Fragment
    private var _binding: FragmentFraseDetalleBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: FraseDetalleFragmentArgs by navArgs()

    //Inicializo var Frase para instanciar la categoría seleccionada previamente
    private lateinit var currentFrase: Frase

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

    //IMPORTANTE: ¡Adapter Pictograma adaptado para Frases!
    //para mostrar recycler view con la lista de pictogramas
    private lateinit var pictogramasFraseAdapter: PictogramasFraseAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFraseDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as FrasesActivity).supportActionBar?.title = getString(R.string.detalle_frases)

        val application = requireNotNull(this.activity).application

        //En Frase, al igual que Categoría, necesito mostrar Pictogramas
        val pictogramaRepository = PictogramaRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory =
            PictogramaViewModelFactory(
                application, pictogramaRepository
            )

        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory).get(PictogramaViewModel::class.java)

        currentFrase = args.frase!!

        //---------CARGO DATOS EN RECYCLER VIEW--------
        setUpRecyclerViewPictogramas()

        //Botón FAB para editar la Frase y agregar o modificar Pictogramas
        binding.fabEdit.setOnClickListener {

            val direction = FraseDetalleFragmentDirections
                .actionFraseDetalleFragmentToFraseModificarFragment(currentFrase)
            view.findNavController().navigate(direction)

        }
    }

    private fun setUpRecyclerViewPictogramas() {
        pictogramasFraseAdapter = PictogramasFraseAdapter()
        binding.listaPictogramasRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasFraseAdapter
        }



        //ESTO ES REALMENTE ASI
        activity?.let {
            pictogramaViewModel.getPictogramasByFrase(currentFrase.nombreFrase)
                .observe(viewLifecycleOwner, { pictogramas ->

                    //if(!pictogramas.isNullOrEmpty()) {
//ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI
                    //ESTO ES REALMENTE ASI

                        pictogramasFraseAdapter.differ.submitList(pictogramas[0].pictogramas)
                        updateUI(pictogramas[0].pictogramas)
                    //}
                })
        }
    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.cardView3.visibility = View.GONE
            binding.chipGroup.visibility = View.GONE
            binding.listaPictogramasRecyclerView.visibility = View.VISIBLE
        } else {
            binding.cardView3.visibility = View.VISIBLE
            binding.chipGroup.visibility = View.VISIBLE
            binding.listaPictogramasRecyclerView.visibility = View.GONE
        }
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_buscar, menu)
        val menuBusqueda = menu.findItem(R.id.menu_buscar).actionView as SearchView
        menuBusqueda.isSubmitButtonEnabled = false
        menuBusqueda.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            buscarPictograma(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
        //    buscarPictograma(newText)
        }
        return true
    }

    //OJO, a diferencia de Categoria, no requiero buscar pictogramas
    private fun buscarPictograma(query: String?) {
        val searchQuery = "%$query%"
        //Acá, además del término buscado, debe ir la Categoría a la que pertenece el Pictograma
        pictogramaViewModel.searchPictograma(searchQuery).observe(
            this, { list ->
                pictogramaAdapter.differ.submitList(list)
            }
        )
    }
*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}