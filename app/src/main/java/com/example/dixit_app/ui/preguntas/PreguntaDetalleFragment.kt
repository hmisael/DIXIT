package com.example.dixit_app.ui.preguntas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPreguntaDetalleBinding
import com.example.dixit_app.helper.toast
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pregunta
import com.example.dixit_app.model.entidades.Respuesta
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.ui.PictogramasPreguntaAdapter
import com.example.dixit_app.ui.PictogramasRespuestaAdapter
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PreguntaDetalleFragment  : Fragment(R.layout.fragment_pregunta_detalle) {

    //Patrón binding layout de diseño para este Fragment
    private var _binding: FragmentPreguntaDetalleBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: PreguntaDetalleFragmentArgs by navArgs()

    //Inicializo var para instanciar la pregunta seleccionada previamente
    private lateinit var currentPregunta: Pregunta

    //La Respuesta puede ser null desde PreguntaAdapter (clic en un elemento de PreguntasFragment)
    //Pero la Respuesta es un objeto desde clic botón Agregar Pregunta en PreguntasFragment
    //private lateinit var currentRespuesta: Respuesta

    //Adapter para Preguntas (RV con la lista de pictogramas)
    private lateinit var pictogramasPreguntaAdapter: PictogramasPreguntaAdapter

    //Adapter para Respuestas (RV con la lista de pictogramas)
    private lateinit var pictogramasRespuestaAdapter: PictogramasRespuestaAdapter

    //PREGUNTA: Es la descripción de la pregunta, con pictogramas

    private lateinit var pictogramaViewModel: PictogramaViewModel
    //RESPUESTA: es cada pictograma/respuesta disponible para la pregunta
    //private lateinit var preguntaViewModel: PreguntaViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreguntaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as PreguntasActivity).supportActionBar?.title =
            getString(R.string.detalle_pregunta)

        val application = requireNotNull(this.activity).application
        currentPregunta = args.pregunta!!




        //En Pregunta, al igual que Categoría, necesito mostrar Pictogramas
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        val viewModelProviderFactory = PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(PictogramaViewModel::class.java)

        //Carga de pictogramas en el RecyclerView de Pregunta
        setUpPictogramasPreguntaRV()
        //Carga de pictogramas en el RecyclerView de Respuesta
        setUpPictogramasRespuestaRV()

        //Botón FAB para editar la Pregunta y agregar o modificar Pictogramas
        binding.fabModificarPregunta.setOnClickListener {
            val direction = PreguntaDetalleFragmentDirections
                    .actionPreguntaDetalleFragmentToPreguntaModificarFragment(currentPregunta)
            view.findNavController().navigate(direction)
        }
    }


/*
    private fun searchRespuesta(nombrePregunta :String ) {
        //busco la Respuesta en bd
        activity?.let {
            currentRespuesta =
                pictogramaViewModel.getRespuesta(currentPregunta.nombrePregunta)
            //Verificar si es posible realizarlo desde lifecylce

        }
    }
*/

    private fun setUpPictogramasPreguntaRV() {
        pictogramasPreguntaAdapter = PictogramasPreguntaAdapter()
        binding.pictogramasPreguntaRV.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasPreguntaAdapter
        }

        //Obtener los pictogramas guardados en la Pregunta detallada
        activity?.let {
            pictogramaViewModel.getPictogramasByPregunta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner, { pictogramas ->
                    pictogramasPreguntaAdapter.differ.submitList(pictogramas[0].pictogramas)
                    //Debo crear un avisode VACIO para Preguntas y Respuestas
                    //updateUI(pictogramas[0].pictogramas)
                })
            binding.txtTituloPregunta.setText(currentPregunta.nombrePregunta)
        }
    }

    private fun setUpPictogramasRespuestaRV() {
        pictogramasRespuestaAdapter = PictogramasRespuestaAdapter()
        binding.pictogramasRespuestaRV.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasRespuestaAdapter
        }

        //Obtener los pictogramas guardados en la Pregunta detallada
        activity?.let {
            //Obtener los respuestas = pictogramas con nombre de Pregunta actual
            pictogramaViewModel.getPictogramasByRespuesta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner, { pictogramas ->
                    pictogramasRespuestaAdapter.differ.submitList(pictogramas[0].pictogramas)
                    //Debo crear un avisode VACIO para Preguntas y Respuestas
                    //updateUI(pictogramas[0].pictogramas)
                })
            //binding.txtTituloPregunta.setText(currentPregunta.nombrePregunta)
        }
    }


        /*
    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            //Si existen pictogramas en la Pregunta
            binding.textViewNull.visibility = View.GONE
            binding.imageViewNull.visibility = View.GONE
            binding.txtTituloPregunta.visibility = View.VISIBLE
            binding.pictogramasPreguntaRV.visibility = View.VISIBLE
            binding.txtTituloRespuesta.visibility = View.VISIBLE
            binding.pictogramasRespuestaRV.visibility = View.VISIBLE
        } else {
            //Si la Pregunta está vacía
            binding.textViewNull.visibility = View.VISIBLE
            binding.imageViewNull.visibility = View.VISIBLE
            binding.txtTituloPregunta.visibility = View.GONE
            binding.pictogramasPreguntaRV.visibility = View.GONE
            binding.txtTituloRespuesta.visibility = View.GONE
            binding.pictogramasRespuestaRV.visibility = View.GONE
        }
    }
*/


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}