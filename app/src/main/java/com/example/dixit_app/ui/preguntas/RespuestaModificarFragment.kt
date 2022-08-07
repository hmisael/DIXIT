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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentRespuestaModificarBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.model.entidades.Pregunta
import com.example.dixit_app.model.entidades.Respuesta
import com.example.dixit_app.model.entidades.RespuestaPictogramaRC
import com.example.dixit_app.repository.PreguntaRepository
import com.example.dixit_app.ui.*
import com.example.dixit_app.viewmodel.PreguntaViewModel
import com.example.dixit_app.viewmodel.PreguntaViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class RespuestaModificarFragment : Fragment(R.layout.fragment_respuesta_modificar),
    TopAdapterClickPreguntaInterface, BottomAdapterClickPreguntaInterface {

    private var _binding: FragmentRespuestaModificarBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: RespuestaModificarFragmentArgs by navArgs()

    //Inicializo var Respuesta para instanciar la respuesta seleccionada previamente
    private lateinit var currentPregunta: Pregunta
    //private var currentRespuesta: Respuesta? = null


    //Adapter para ambos RV (no carga, solo es la vista del RV)
    //USAR MISMO ADAPTER DE PREGUNTAS, porque maneja Pictogramas
    private lateinit var pictogramasRespuestaRVTopAdapter: PictogramasPreguntaRVTopAdapter
    private lateinit var pictogramasRespuestaRVBottomAdapter: PictogramasPreguntaRVBottomAdapter

    //un View Model para Respuesta
    private lateinit var preguntaViewModel: PreguntaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        _binding = FragmentRespuestaModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as PreguntasActivity).supportActionBar?.title = "Modificar Respuesta"

        //usar adapter y viewmodel de Pictogramas (porque CategoriaDetalleFragment lo usa)
        val application = requireNotNull(this.activity).application
        currentPregunta = args.pregunta!!
        //currentRespuesta = args.respuesta!!
        //Iniciar el Repositorio de Respuesta para ViewModel
        val preguntaRepository = PreguntaRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory = PreguntaViewModelFactory(application, preguntaRepository)

        //Instancia de RespuestaViewModel
        preguntaViewModel = ViewModelProvider(this, viewModelProviderFactory)
                            .get(PreguntaViewModel::class.java)

        //NOMBRE DE PREGUNTA
        binding.txtPictogramasRespuesta.text = getString(R.string.respuesta_txt)

        //Inicializar el RV superior: los pictogramas de la Respuesta, si los hay
        setTopRecyclerView()
        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()

        //Botón para guardar los pictogramas en la Respuesta a crear ahora
        binding.fabDone.setOnClickListener{
            //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
            val pictogramas:List<Pictograma> = pictogramasRespuestaRVTopAdapter.differTop.currentList
            //Crear un objeto RespuestaPictogramaRC (referencia cruzada): 1 RESPUESTA = 1 PICTOGRAMA
            var pictoRespuesta : RespuestaPictogramaRC
            //Recorrer la lista para para guardar el id de cada pictograma
            for (picto in pictogramas){
                //A CORREGIR: uso idPregunta pero debe ser idRespuesta
                pictoRespuesta = RespuestaPictogramaRC(currentPregunta.idPregunta, picto.idPictograma)
                preguntaViewModel.insertPictogramasRespuesta(pictoRespuesta)
                /*
                //Inicializar con un  valor por defecto
                val respuesta = Respuesta(0, picto.nombrePictograma)
                lifecycleScope.launch {
                    //Cada pictograma, es una respuesta. Procedo a insertarlo en BD y capturar el id generado
                    idRespuestaBD = preguntaViewModel.insertRespuesta(respuesta)
                    //Instanciar una Pregunta con el idPregunta generado para seguir navegando
                    //val respuestaBD = Respuesta(idRespuestaBD, picto.nombrePictograma, currentPregunta.nombrePregunta)

                    //Creo RC entre Respuesta (con id insertado) y Pictograma (del actual recorrido)
                    pictoRespuesta = RespuestaPictogramaRC(idRespuestaBD, picto.idPictograma)
                    preguntaViewModel.insertPictogramasRespuesta(pictoRespuesta)

                 */

            }
            //Se prosigue a editar las Respuestas
            val direction =
                RespuestaModificarFragmentDirections.actionRespuestaModificarFragmentToPreguntasFragment()
            view.findNavController().navigate(direction)

            Snackbar.make(
                view, "Respuesta modificada exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()

            //Notificar datos actualizados al Adapter
            pictogramasRespuestaRVTopAdapter.notifyDataSetChanged()
            //Notificar datos actualizados al Adapter
            pictogramasRespuestaRVBottomAdapter.notifyDataSetChanged()
        }
    }

    //VER que estan ambos RV, no solo Bottom, en esta funcion
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasRespuestaRVBottomAdapter = PictogramasPreguntaRVBottomAdapter(this)
        binding.rvBottomPregunta.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasRespuestaRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            preguntaViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner, { pictogramas ->
                    pictogramasRespuestaRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                })
        }

        //Asociar adapter para RV Top (los pictogramas seleccionados)
        pictogramasRespuestaRVTopAdapter = PictogramasPreguntaRVTopAdapter(this)
        binding.rvTopRespuesta.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = pictogramasRespuestaRVTopAdapter
        }
    }

    private fun setTopRecyclerView(){
        //Cargar pictogramas de Respuesta en RV Top
        //Es Ref. Cruzada los pictogramas de la respuesta (Y DEBO ENVIAR ARGUMENTO FRASE)
        activity?.let {
            preguntaViewModel.getPictogramasByRespuesta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner, { respuestaPictogramas ->
                    //obtener listado de Pictogramas a RespuestasconPictogramas (tipo de dato del differTop)
                    pictogramasRespuestaRVTopAdapter.differTop.submitList(respuestaPictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                })
        }
    }

    /*private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.cardView3.visibility = View.GONE
            binding.rvBottomRespuesta.visibility = View.VISIBLE
        } else {
            binding.cardView3.visibility = View.VISIBLE
            binding.rvBottomRespuesta.visibility = View.GONE
        }
    }*/

    override fun onItemRVBottomClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV inferior, se agrega el ítem al RV superior
        //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasRespuestaRVTopAdapter.differTop.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasRespuestaRVTopAdapter.differTop.submitList(copiedList)
    }

    override fun onItemRVTopClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV superior, se agrega el ítem al RV inferior
        //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasRespuestaRVBottomAdapter.differBottom.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasRespuestaRVBottomAdapter.differBottom.submitList(copiedList)
    }

    override fun onStop() {
        Glide.with(this).clear(binding.rvBottomPregunta)
        Glide.with(this).clear(binding.rvTopRespuesta)
        super.onStop()
    }




}