package com.example.dixit_app.ui.rutinas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.R
import com.example.dixit_app.RutinasActivity
import com.example.dixit_app.databinding.FragmentRutinaDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.ui.PictogramasRutinaAdapter
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory

class RutinaDetalleFragment : Fragment(R.layout.fragment_rutina_detalle){

    //Patrón binding layout de diseño para este Fragment
    private var _binding: FragmentRutinaDetalleBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: RutinaDetalleFragmentArgs by navArgs()

    //Inicializo var para instanciar la rutina seleccionada previamente
    private lateinit var currentRutina: Rutina

    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

    //IMPORTANTE: ¡Adapter Pictograma adaptado para Rutinas!
    //para mostrar recycler view con la lista de pictogramas
    private lateinit var pictogramasRutinaAdapter: PictogramasRutinaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRutinaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as RutinasActivity).supportActionBar?.title = getString(R.string.detalle_rutinas)

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

        currentRutina = args.rutina!!

        //---------CARGA DE DATOS EN RECYCLER VIEW--------
        setUpRecyclerViewPictogramas()

        //Botón FAB para editar la Frase y agregar o modificar Pictogramas
        binding.fabEdit.setOnClickListener {
            val direction =
                RutinaDetalleFragmentDirections.actionRutinaDetalleFragmentToRutinaModificarFragment(
                    currentRutina
                )
            view.findNavController().navigate(direction)
        }
    }

    private fun setUpRecyclerViewPictogramas() {
        pictogramasRutinaAdapter = PictogramasRutinaAdapter()
        binding.listaPictogramasRutinaRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasRutinaAdapter
        }

        activity?.let {
            pictogramaViewModel.getPictogramasByRutina(currentRutina.nombreRutina)
                .observe(viewLifecycleOwner, { pictogramas ->
                    pictogramasRutinaAdapter.differ.submitList(pictogramas[0].pictogramas)
                    updateUI(pictogramas[0].pictogramas)
                })

            binding.txtTituloRutina.setText(currentRutina.nombreRutina)
        }
    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.textView.visibility = View.GONE
            binding.imageErrorRutinaView.visibility = View.GONE
            binding.txtTituloRutina.visibility = View.VISIBLE
            binding.listaPictogramasRutinaRecyclerView.visibility = View.VISIBLE
        } else {
            binding.textView.visibility = View.VISIBLE
            binding.imageErrorRutinaView.visibility = View.VISIBLE
            binding.txtTituloRutina.visibility = View.GONE
            binding.listaPictogramasRutinaRecyclerView.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}