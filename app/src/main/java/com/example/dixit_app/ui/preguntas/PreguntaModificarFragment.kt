package com.example.dixit_app.ui.preguntas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPreguntaModificarBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Pregunta
import com.example.dixit_app.model.entidades.PreguntaPictogramaRC
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.model.entidades.Respuesta
import com.example.dixit_app.repository.PreguntaRepository
import com.example.dixit_app.ui.*

import com.example.dixit_app.viewmodel.PreguntaViewModel
import com.example.dixit_app.viewmodel.PreguntaViewModelFactory
import com.google.android.material.snackbar.Snackbar


class PreguntaModificarFragment : Fragment(R.layout.fragment_pregunta_modificar),
    TopAdapterClickPreguntaInterface,
    BottomAdapterClickPreguntaInterface{
    //,SearchView.OnQueryTextListener{
    //New Searchview

    private var _binding: FragmentPreguntaModificarBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: PreguntaModificarFragmentArgs by navArgs()
    //Tanto Pregunta como Respuesta no es null desde PreguntaDetalle
    //Inicializo var Pregunta para instanciar la pregunta seleccionada previamente
    private lateinit var currentPregunta: Pregunta
    //private lateinit var currentRespuesta: Respuesta

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    private lateinit var pictogramasPreguntaRVTopAdapter: PictogramasPreguntaRVTopAdapter
    private lateinit var pictogramasPreguntaRVBottomAdapter: PictogramasPreguntaRVBottomAdapter

    //un View Model para Pregunta
    private lateinit var preguntaViewModel: PreguntaViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        _binding = FragmentPreguntaModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as PreguntasActivity).supportActionBar?.title = "Modificar Pregunta"

        val application = requireNotNull(this.activity).application
        currentPregunta = args.pregunta!!
        //currentRespuesta = args.respuesta!!


        //Iniciar el Repositorio de Pregunta para ViewModel
        val preguntaRepository = PreguntaRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory = PreguntaViewModelFactory(application, preguntaRepository)

        //Instancia de PreguntaViewModel
        preguntaViewModel = ViewModelProvider(this, viewModelProviderFactory)
                            .get(PreguntaViewModel::class.java)

        //NOMBRE DE PREGUNTA
        binding.txtPictogramasEleccion.text =
            getString(R.string.pregunta_txt, currentPregunta.nombrePregunta)

        //Inicializar el RV superior: los pictogramas de la Pregunta, si los hay
        setTopRecyclerView()
        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()

        //Botón para guardar los pictogramas en la Pregunta a crear ahora
        binding.fabDone.setOnClickListener{
            //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
            val pictogramas:List<Pictograma> = pictogramasPreguntaRVTopAdapter.differTop.currentList
            //Crear un objeto PreguntaPictogramaRC (referencia cruzada)
            var pictosPregunta : PreguntaPictogramaRC
            //Recorrer la lista para para guardar el id de cada pictograma
            for (picto in pictogramas){
                pictosPregunta = PreguntaPictogramaRC(currentPregunta.idPregunta, picto.idPictograma)
                preguntaViewModel.insertPictogramasPregunta(pictosPregunta)
            }
            //Se prosigue a editar las Respuestas
            val direction = PreguntaModificarFragmentDirections
                .actionPreguntaModificarFragmentToRespuestaModificarFragment(currentPregunta)
            view.findNavController().navigate(direction)


            Snackbar.make(
                view, "Pregunta modificada exitosamente",
                Snackbar.LENGTH_SHORT
            ).show()

            //Notificar datos actualizados al Adapter
            pictogramasPreguntaRVTopAdapter.notifyDataSetChanged()
            //Notificar datos actualizados al Adapter
            pictogramasPreguntaRVBottomAdapter.notifyDataSetChanged()
        }
    }

    //VER que estan ambos RV, no solo Bottom, en esta funcion
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasPreguntaRVBottomAdapter = PictogramasPreguntaRVBottomAdapter(this)
        binding.rvBottomPregunta.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasPreguntaRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            preguntaViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner, { pictogramas ->
                    pictogramasPreguntaRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                })
        }

        //Asociar adapter para RV Top (los pictogramas seleccionados)
        pictogramasPreguntaRVTopAdapter = PictogramasPreguntaRVTopAdapter(this)
        binding.rvTopPregunta.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = pictogramasPreguntaRVTopAdapter
        }
    }

    private fun setTopRecyclerView(){
        //Cargar pictogramas de Pregunta en RV Top
        //Es Ref. Cruzada los pictogramas de la pregunta (Y DEBO ENVIAR ARGUMENTO FRASE)
        activity?.let {
            preguntaViewModel.getPictogramasByPregunta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner, { preguntaPictogramas ->
                    //obtener listado de Pictogramas a PreguntasconPictogramas (tipo de dato del differTop)
                    pictogramasPreguntaRVTopAdapter.differTop.submitList(preguntaPictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                })
        }
    }

    /*private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.cardView3.visibility = View.GONE
            binding.rvBottomPregunta.visibility = View.VISIBLE
        } else {
            binding.cardView3.visibility = View.VISIBLE
            binding.rvBottomPregunta.visibility = View.GONE
        }
    }*/

    override fun onItemRVBottomClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV inferior, se agrega el ítem al RV superior
        //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasPreguntaRVTopAdapter.differTop.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasPreguntaRVTopAdapter.differTop.submitList(copiedList)
    }

    override fun onItemRVTopClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV superior, se agrega el ítem al RV inferior
        //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasPreguntaRVBottomAdapter.differBottom.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasPreguntaRVBottomAdapter.differBottom.submitList(copiedList)
    }







    //NEW Búsqueda de pictogramas
/*
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
        preguntaViewModel.searchPictograma(searchQuery).observe(
            this, { list ->
                pictogramasPreguntaRVBottomAdapter.differBottom.submitList(list)
            }
        )
    }


*/
    override fun onStop() {
        Glide.with(this).clear(binding.rvBottomPregunta)
        //Glide.with(this).clear(binding.rvTopPregunta)
        super.onStop()
    }




}