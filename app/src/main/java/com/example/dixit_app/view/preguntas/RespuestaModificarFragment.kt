package com.example.dixit_app.view.preguntas

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.view.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentRespuestaModificarBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.entities.Pregunta
import com.example.dixit_app.model.entities.RespuestaPictogramaRC
import com.example.dixit_app.model.repository.PreguntaRepository
import com.example.dixit_app.viewmodel.PreguntaViewModel
import com.example.dixit_app.viewmodel.PreguntaViewModelFactory
import com.google.android.material.snackbar.Snackbar


class RespuestaModificarFragment : Fragment(), TopAdapterClickPreguntaInterface, BottomAdapterClickPreguntaInterface,
                                MenuProvider, SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentRespuestaModificarBinding

    //Instancia contexto de navegación de este Fragment
    private val args: RespuestaModificarFragmentArgs by navArgs()

    //Inicializo var Respuesta para instanciar la respuesta seleccionada previamente
    private lateinit var currentPregunta: Pregunta

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    //Adapter de Pregunta, porque maneja Pictogramas
    private lateinit var pictogramasRespuestaRVTopAdapter: PictogramasPreguntaRVTopAdapter
    private lateinit var pictogramasRespuestaRVBottomAdapter: PictogramasPreguntaRVBottomAdapter

    //un View Model para Respuesta
    private lateinit var preguntaViewModel: PreguntaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        binding = FragmentRespuestaModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       //Menu
       val menuHost: MenuHost = requireActivity()
       menuHost.addMenuProvider(this, viewLifecycleOwner, lifecycle.currentState)

       //Busqueda
       binding.searchPictogramaRespuesta.setOnQueryTextListener(this)

       currentPregunta = args.pregunta!!

       //Reemplazo título de toolbar Activity
       (activity as PreguntasActivity).supportActionBar?.title = "Modificar Respuesta"

        //usar adapter y viewmodel de Pictogramas (porque CategoriaDetalleFragment lo usa)
        val application = requireNotNull(this.activity).application
        //Iniciar el Repositorio de Respuesta para ViewModel
        val preguntaRepository = PreguntaRepository(DixitDatabase(application))

        val viewModelProviderFactory = PreguntaViewModelFactory(application, preguntaRepository)

        //Instancia de RespuestaViewModel
        preguntaViewModel = ViewModelProvider(this, viewModelProviderFactory)
                            .get(PreguntaViewModel::class.java)

        //Inicializar el RV superior: los pictogramas de la Respuesta, si los hay
        setTopRecyclerView()
        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()
    }

    //Ambos RV
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasRespuestaRVBottomAdapter = PictogramasPreguntaRVBottomAdapter(this)
        binding.rvBottomPregunta.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                GridLayoutManager(requireContext(), 3)
            } else {
                // Orientación horizontal
                GridLayoutManager(requireContext(), 3)
            }
            setHasFixedSize(true)
            adapter = pictogramasRespuestaRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            preguntaViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasRespuestaRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                }
        }

        //Asociar adapter para RV Top (los pictogramas seleccionados)
        pictogramasRespuestaRVTopAdapter = PictogramasPreguntaRVTopAdapter(this)
        binding.rvTopRespuesta.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                GridLayoutManager(requireContext(), 3)
            } else {
                // Orientación horizontal
                GridLayoutManager(requireContext(), 3)
            }
            setHasFixedSize(true)
            adapter = pictogramasRespuestaRVTopAdapter
        }
    }

    private fun setTopRecyclerView(){
        //Cargar pictogramas de Respuesta en RV Top
        //Es Ref. Cruzada los pictogramas de la respuesta (enviar como args Frase)
        activity?.let {
            preguntaViewModel.getPictogramasByRespuesta(currentPregunta.nombrePregunta)
                .observe(viewLifecycleOwner) { respuestaPictogramas ->
                    //obtener listado de Pictogramas a RespuestasconPictogramas (tipo de dato del differTop)
                    pictogramasRespuestaRVTopAdapter.differTop.submitList(respuestaPictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                }
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
        //val copiedList = pictogramasRespuestaRVBottomAdapter.differBottom.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        //copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        //pictogramasRespuestaRVBottomAdapter.differBottom.submitList(copiedList)
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_guardar, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId){
            R.id.menu_guardar -> {
            //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
                val pictogramas:List<Pictograma> = pictogramasRespuestaRVTopAdapter.differTop.currentList
                //Crear un objeto RespuestaPictogramaRC (referencia cruzada): 1 RESPUESTA = 1 PICTOGRAMA
                var pictoRespuesta : RespuestaPictogramaRC
                //Recorrer la lista para para guardar el id de cada pictograma
                for (picto in pictogramas){
                    //A CORREGIR: uso idPregunta pero debe ser idRespuesta
                    pictoRespuesta = RespuestaPictogramaRC(0, currentPregunta.idPregunta, picto.idPictograma)
                    preguntaViewModel.insertPictogramasRespuesta(pictoRespuesta)
            }

        Snackbar.make(
            requireView(), "Respuesta modificada exitosamente",
            Snackbar.LENGTH_SHORT
        ).show()

            //Se prosigue a editar las Respuestas
        val direction =
            RespuestaModificarFragmentDirections.actionRespuestaModificarFragmentToPreguntasFragment()
        view?.findNavController()?.navigate(direction)
            true
        }
            //Otro when para botón extra
            else -> false
        }

    }


    private fun buscarPictograma(query: String?) {
        val searchQuery = "%$query%"
        preguntaViewModel.searchPictograma(searchQuery).observe(this) { list ->
            pictogramasRespuestaRVBottomAdapter.differBottom.submitList(list)
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            buscarPictograma(newText)
        }
        return true
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onStop() {
        Glide.with(this).clear(binding.rvBottomPregunta)
        Glide.with(this).clear(binding.rvTopRespuesta)
        super.onStop()
    }

}