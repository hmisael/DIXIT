package com.example.dixit_app.view.preguntas

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dixit_app.view.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPreguntaDetalleBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pregunta
import com.example.dixit_app.model.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory

class PreguntaDetalleFragment  : Fragment() {

    //Patrón binding layout de diseño para este Fragment
    private lateinit var binding: FragmentPreguntaDetalleBinding

    //Instancia contexto de navegación de este Fragment
    private val args: PreguntaDetalleFragmentArgs by navArgs()

    //Inicializar variable para instanciar la pregunta seleccionada previamente
    private lateinit var currentPregunta: Pregunta

    //Pregunta: entidad descripción de la pregunta, con pictogramas
    private lateinit var pictogramaViewModel: PictogramaViewModel
    //Respuesta: es cada pictograma/respuesta disponible para la pregunta
    //private lateinit var preguntaViewModel: PreguntaViewModel

    //La Respuesta puede ser null desde PreguntaAdapter (clic en un elemento de PreguntasFragment)
    //Pero la Respuesta es un objeto desde clic botón Agregar Pregunta en PreguntasFragment
    //private lateinit var currentRespuesta: Respuesta

    //Adapter para Preguntas (RV con la lista de pictogramas)
    private var pictogramasPreguntaAdapter: PictogramasPreguntaAdapter = PictogramasPreguntaAdapter()

    //Adapter para Respuestas (RV con la lista de pictogramas)
    private var pictogramasRespuestaAdapter: PictogramasRespuestaAdapter = PictogramasRespuestaAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPreguntaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as PreguntasActivity).supportActionBar?.title = getString(R.string.detalle_pregunta)

        val application = requireNotNull(this.activity).application

        //En Pregunta, al igual que Categoría, necesito mostrar Pictogramas
        val pictogramaRepository = PictogramaRepository(DixitDatabase(application))

        val viewModelProviderFactory = PictogramaViewModelFactory(application, pictogramaRepository)

        pictogramaViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(PictogramaViewModel::class.java)

        currentPregunta = args.pregunta!!

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

    private fun setUpPictogramasPreguntaRV() {
        //pictogramasPreguntaAdapter = PictogramasPreguntaAdapter()
        binding.pictogramasPreguntaRV.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            } else {
                // Orientación horizontal
                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            }
            setHasFixedSize(true)
            adapter = pictogramasPreguntaAdapter
        }

        //Obtener los pictogramas guardados en la Pregunta detallada
        activity?.let {
            pictogramaViewModel.getPictogramasByPregunta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasPreguntaAdapter.differ.submitList(pictogramas[0].pictogramas)
                    //TODO aviso "sin elementos" para Preguntas y Respuestas
                    //updateUI(pictogramas[0].pictogramas)
                }
            binding.txtTituloPregunta.setText(currentPregunta.nombrePregunta)
        }
    }

    private fun setUpPictogramasRespuestaRV() {
        pictogramasRespuestaAdapter = PictogramasRespuestaAdapter()
        binding.pictogramasRespuestaRV.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            } else {
                // Orientación horizontal
                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            }
            setHasFixedSize(true)
            adapter = pictogramasRespuestaAdapter
        }

        //Obtener los pictogramas guardados en la Pregunta detallada
        activity?.let {
            //Obtener los respuestas = pictogramas con nombre de Pregunta actual
            pictogramaViewModel.getPictogramasByRespuesta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasRespuestaAdapter.differ.submitList(pictogramas[0].pictogramas)
                    //Debo crear un avisode VACIO para Preguntas y Respuestas
                    //updateUI(pictogramas[0].pictogramas)
                }
            //binding.txtTituloPregunta.setText(currentPregunta.nombrePregunta)
        }
    }

}