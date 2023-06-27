package com.example.dixit_app.view.frases

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.view.FrasesActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentFraseDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Frase
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory


//Si está vacío, ir a FraseModificarFragment. Si tiene elementos, mostrar Frase
class FraseDetalleFragment : Fragment(){

    //Patrón binding layout de diseño para este Fragment
    private lateinit var binding: FragmentFraseDetalleBinding

    //Instancia contexto de navegación de este Fragment
    private val args: FraseDetalleFragmentArgs by navArgs()

    //Inicializo var para instanciar la frases seleccionada previamente
    private lateinit var currentFrase: Frase

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

    //Adapter para ítems de RecyclerView
    private var pictogramasFraseAdapter: PictogramasFraseAdapter = PictogramasFraseAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFraseDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity

        val application = requireNotNull(this.activity).application

        //En Frase, al igual que Categoría, necesito mostrar Pictogramas
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        val viewModelProviderFactory = PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(PictogramaViewModel::class.java)

        currentFrase = args.frase!!
        (activity as FrasesActivity).supportActionBar?.title = currentFrase.nombreFrase


        //Carga de datos en el RecyclerView
        setUpRecyclerViewPictogramas()

        //Botón FAB para editar la Frase y agregar o modificar Pictogramas
        binding.fabEdit.setOnClickListener {
            val direction = FraseDetalleFragmentDirections
                .actionFraseDetalleFragmentToFraseModificarFragment(currentFrase)
            view.findNavController().navigate(direction)
        }
    }

    private fun setUpRecyclerViewPictogramas() {
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
            adapter = pictogramasFraseAdapter
        }

        //Obtener los pictogramas guardados en la Frase detallada
        activity?.let {
            pictogramaViewModel.getPictogramasByFrase(currentFrase.nombreFrase)
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasFraseAdapter.differ.submitList(pictogramas[0].pictogramas)
                    updateUI(pictogramas[0].pictogramas)
                }
        }
    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            //Si existen pictogramas en la Frase

            binding.clFraseDetalle.visibility = View.GONE
            /*
            binding.textView.visibility = View.GONE
            binding.imageErrorView.visibility = View.GONE
            */

            binding.listaPictogramasRecyclerView.visibility = View.VISIBLE
        } else {
            //Si la Frase está vacía
            binding.clFraseDetalle.visibility = View.VISIBLE

            /*
            binding.textView.visibility = View.VISIBLE
            binding.imageErrorView.visibility = View.VISIBLE
             */

            binding.listaPictogramasRecyclerView.visibility = View.GONE
        }
    }


    /*
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/
}