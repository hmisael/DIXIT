package com.example.dixit_app.view.rutinas

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.R
import com.example.dixit_app.view.RutinasActivity
import com.example.dixit_app.databinding.FragmentRutinaModificarBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.entities.Rutina
import com.example.dixit_app.model.entities.RutinaPictogramaRC
import com.example.dixit_app.model.repository.RutinaRepository
import com.example.dixit_app.viewmodel.RutinaViewModel
import com.example.dixit_app.viewmodel.RutinaViewModelFactory

class RutinaModificarFragment : Fragment(),
    TopAdapterClickRutinaInterface, BottomAdapterClickRutinaInterface,
                                MenuProvider, SearchView.OnQueryTextListener{

    private lateinit var binding: FragmentRutinaModificarBinding

    //Instancia contexto de navegación de este Fragment
    private val args: RutinaModificarFragmentArgs by navArgs()

    //Inicializo var Rutina para instanciar la rutina seleccionada previamente
    private lateinit var currentRutina: Rutina

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    private lateinit var pictogramasRutinaRVTopAdapter: PictogramasRutinaRVTopAdapter
    private lateinit var pictogramasRutinaRVBottomAdapter: PictogramasRutinaRVBottomAdapter

    //un View Model para Rutina
    private lateinit var rutinaViewModel: RutinaViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentRutinaModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, lifecycle.currentState)

        //Busqueda
        binding.searchViewModRutina.setOnQueryTextListener(this)

        currentRutina = args.rutina!!

        //Reemplazo título de toolbar Activity
        (activity as RutinasActivity).supportActionBar?.title = "Modificar Rutina"

        val application = requireNotNull(this.activity).application

        //Iniciar el Repositorio de Rutina para ViewModel
        val rutinaRepository = RutinaRepository(DixitDatabase(application))

        val viewModelProviderFactory = RutinaViewModelFactory(application, rutinaRepository)

        //Instancia de RutinaViewModel
        rutinaViewModel = ViewModelProvider(this, viewModelProviderFactory)
                          .get(RutinaViewModel::class.java)

        //Inicializar el RV superior: los pictogramas de la Rutina, si los hay
        setTopRecyclerView()

        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()
    }

    //VER que estan ambos RV, no solo Bottom, en esta funcion
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasRutinaRVBottomAdapter = PictogramasRutinaRVBottomAdapter(this)
        binding.rvBottomRutina.apply {

            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            } else {
                // Orientación horizontal
                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            }
            setHasFixedSize(true)
            adapter = pictogramasRutinaRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            rutinaViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasRutinaRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                }
        }

        //Asociar adapter para RV Top (los pictogramas seleccionados)
        pictogramasRutinaRVTopAdapter = PictogramasRutinaRVTopAdapter(this)
        binding.rvTopRutina.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = pictogramasRutinaRVTopAdapter
        }
    }

    private fun setTopRecyclerView(){
        //Cargar pictogramas de Rutina en RV Top
        //Es Ref. Cruzada los pictogramas de la frases (Y DEBO ENVIAR ARGUMENTO RUTINA)
        activity?.let {
            rutinaViewModel.getRutinaPictogramas(currentRutina.nombreRutina)
                .observe(viewLifecycleOwner) { rutinaPictogramas ->
                    //obtener listado de Pictogramas a RutinaconPictogramas (tipo de dato del differTop)
                    pictogramasRutinaRVTopAdapter.differTop.submitList(rutinaPictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                }
        }
    }

    override fun onItemRVBottomClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV inferior, se agrega el ítem al RV superior
        //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasRutinaRVTopAdapter.differTop.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasRutinaRVTopAdapter.differTop.submitList(copiedList)
    }

    override fun onItemRVTopClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV superior, se agrega el ítem al RV inferior
        //Realizo copia de los resultados, casteando a una MutableList

        //val copiedList = pictogramasRutinaRVBottomAdapter.differBottom.currentList.toMutableList()

        //Elimino de la lista copiada el elemento de la posición clickeada

        //copiedList.add(pictograma)

        //Le envío la lista resultante al differ
        //pictogramasRutinaRVBottomAdapter.differBottom.submitList(copiedList)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_guardar, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
                val pictogramas:List<Pictograma> = pictogramasRutinaRVTopAdapter.differTop.currentList
                //Crear un objeto RutinaPictogramaRC (referencia cruzada)
                var pictosRutina : RutinaPictogramaRC
                //Recorrer la lista para para guardar el id de cada pictograma
                //Log.i("MODIFIC_IDFRASE", "IDFRASE: ${currentFrase.idFrase}")
                for (picto in pictogramas){
                    //MOD id FrasePictogramaRC
                    pictosRutina = RutinaPictogramaRC(0, currentRutina.idRutina, picto.idPictograma)
                    rutinaViewModel.insertPictogramasRutina(pictosRutina)
                }
                val direction = RutinaModificarFragmentDirections
                    .actionRutinaModificarFragmentToRutinasFragment()
                view?.findNavController()?.navigate(direction)
                true
            }
            //Otro when para botón extra
            else -> false
        }
    }

    private fun buscarPictograma(query: String?) {
        val searchQuery = "%$query%"
        rutinaViewModel.searchPictograma(searchQuery).observe(this) { list ->
            pictogramasRutinaRVBottomAdapter.differBottom.submitList(list)
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
        Glide.with(this).clear(binding.rvBottomRutina)
        //Glide.with(this).clear(binding.rvTopFrase)
        super.onStop()
    }
/*

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

*/
}