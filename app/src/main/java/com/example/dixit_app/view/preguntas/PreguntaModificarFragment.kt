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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.view.PreguntasActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentPreguntaModificarBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pregunta
import com.example.dixit_app.model.entities.PreguntaPictogramaRC
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.repository.PreguntaRepository

import com.example.dixit_app.viewmodel.PreguntaViewModel
import com.example.dixit_app.viewmodel.PreguntaViewModelFactory
import com.google.android.material.snackbar.Snackbar


class PreguntaModificarFragment : Fragment(),
    TopAdapterClickPreguntaInterface, BottomAdapterClickPreguntaInterface,
    MenuProvider, SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentPreguntaModificarBinding

    //Instancia contexto de navegación de este Fragment
    private val args: PreguntaModificarFragmentArgs by navArgs()

    //Inicializo var Pregunta para instanciar la pregunta seleccionada previamente
    private lateinit var currentPregunta: Pregunta

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    private lateinit var pictogramasPreguntaRVTopAdapter: PictogramasPreguntaRVTopAdapter
    private lateinit var pictogramasPreguntaRVBottomAdapter: PictogramasPreguntaRVBottomAdapter

    //un View Model para Pregunta
    private lateinit var preguntaViewModel: PreguntaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        binding = FragmentPreguntaModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, lifecycle.currentState)

        //Busqueda
        binding.searchPictogramaPregunta.setOnQueryTextListener(this)

        currentPregunta = args.pregunta!!

        //Reemplazo título de toolbar Activity
        (activity as PreguntasActivity).supportActionBar?.title = "Pregunta: "+currentPregunta.nombrePregunta

        val application = requireNotNull(this.activity).application

        //Iniciar el Repositorio de Pregunta para ViewModel
        val preguntaRepository = PreguntaRepository(DixitDatabase(application))

        val viewModelProviderFactory = PreguntaViewModelFactory(application, preguntaRepository)

        //Instancia de PreguntaViewModel
        preguntaViewModel = ViewModelProvider(this, viewModelProviderFactory)
                            .get(PreguntaViewModel::class.java)

        //NOMBRE DE PREGUNTA
        //binding.txtPictogramasEleccion.text = getString(R.string.pregunta_txt, currentPregunta.nombrePregunta)

        //Inicializar el RV superior: los pictogramas de la Pregunta, si los hay
        setTopRecyclerView()
        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()
    }

    //Utilizar ambos RV
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasPreguntaRVBottomAdapter = PictogramasPreguntaRVBottomAdapter(this)
        binding.rvBottomPregunta.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            } else {
                // Orientación horizontal
                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            }
            setHasFixedSize(true)
            adapter = pictogramasPreguntaRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            preguntaViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasPreguntaRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                }
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
                .observe(viewLifecycleOwner) { preguntaPictogramas ->
                    //obtener listado de Pictogramas a PreguntasconPictogramas (tipo de dato del differTop)
                    pictogramasPreguntaRVTopAdapter.differTop.submitList(preguntaPictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                }
        }
    }

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
        //TODO verificar comentarios
        //Cuando se realiza un clic en el RV superior, se agrega el ítem al RV inferior
        //Realizo copia de los resultados, casteando a una MutableList
        //val copiedList = pictogramasPreguntaRVBottomAdapter.differBottom.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        //copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        //pictogramasPreguntaRVBottomAdapter.differBottom.submitList(copiedList)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_guardar, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
                val pictogramas:List<Pictograma> = pictogramasPreguntaRVTopAdapter.differTop.currentList
                //Crear un objeto PreguntaPictogramaRC (referencia cruzada)
                var pictosPregunta : PreguntaPictogramaRC
                //Recorrer la lista para para guardar el id de cada pictograma
                for (picto in pictogramas){
                    pictosPregunta = PreguntaPictogramaRC(0, currentPregunta.idPregunta, picto.idPictograma)
                    preguntaViewModel.insertPictogramasPregunta(pictosPregunta)
                }
                //Se prosigue a editar las Respuestas
                val direction = PreguntaModificarFragmentDirections
                    .actionPreguntaModificarFragmentToRespuestaModificarFragment(currentPregunta)
                view?.findNavController()?.navigate(direction)
                true
            }
            //when para botón extra
            else -> false
        }
    }

    private fun buscarPictograma(query: String?) {
        val searchQuery = "%$query%"
        preguntaViewModel.searchPictograma(searchQuery).observe(this) { list ->
            pictogramasPreguntaRVBottomAdapter.differBottom.submitList(list)
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
        //Glide.with(this).clear(binding.rvTopPregunta)
        super.onStop()
    }

}