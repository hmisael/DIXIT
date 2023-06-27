package com.example.dixit_app.view.rutinas

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.R
import com.example.dixit_app.view.RutinasActivity
import com.example.dixit_app.databinding.FragmentRutinaDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.entities.Rutina
import com.example.dixit_app.model.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory

class RutinaDetalleFragment : Fragment(){

    //Patrón binding layout de diseño para este Fragment
    private lateinit var binding: FragmentRutinaDetalleBinding

    //Instancia contexto de navegación de este Fragment
    private val args: RutinaDetalleFragmentArgs by navArgs()

    //Inicializo var para instanciar la rutina seleccionada previamente
    private lateinit var currentRutina: Rutina

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

    //IMPORTANTE: ¡Adapter Pictograma adaptado para Rutinas!
    //para mostrar recycler view con la lista de pictogramas
    private var pictogramasRutinaAdapter: PictogramasRutinaAdapter = PictogramasRutinaAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentRutinaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity

        val application = requireNotNull(this.activity).application

        //En Frase, al igual que Categoría, necesito mostrar Pictogramas
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        val viewModelProviderFactory = PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory).get(PictogramaViewModel::class.java)

        currentRutina = args.rutina!!
        (activity as RutinasActivity).supportActionBar?.title = currentRutina.nombreRutina


        //Carga de datos en el RecyclerView
        setUpRecyclerViewPictogramas()

        //Botón FAB para editar la Rutina y agregar o modificar Pictogramas
        binding.fabEdit.setOnClickListener {
            val direction =
                RutinaDetalleFragmentDirections.actionRutinaDetalleFragmentToRutinaModificarFragment(
                    currentRutina
                )
            view.findNavController().navigate(direction)
        }
    }

    private fun setUpRecyclerViewPictogramas() {
        binding.listaPictogramasRutinaRecyclerView.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                GridLayoutManager(requireContext(), 3)
            } else {
                // Orientación horizontal
                GridLayoutManager(requireContext(), 5)
            }
            setHasFixedSize(true)
            adapter = pictogramasRutinaAdapter
        }

        activity?.let {
            pictogramaViewModel.getPictogramasByRutina(currentRutina.nombreRutina)
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasRutinaAdapter.differ.submitList(pictogramas[0].pictogramas)
                    updateUI(pictogramas[0].pictogramas)
                }

        }
    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            //Si existen pictogramas en la Rutina
            binding.clRutinaDetalle.visibility = View.GONE

            //binding.imageErrorRutinaView.visibility = View.GONE
            binding.listaPictogramasRutinaRecyclerView.visibility = View.VISIBLE
        } else {
            //Si la Rutina está vacía
            binding.clRutinaDetalle.visibility = View.VISIBLE
            //binding.imageErrorRutinaView.visibility = View.VISIBLE
            binding.listaPictogramasRutinaRecyclerView.visibility = View.GONE
        }
    }

    /*
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/
}