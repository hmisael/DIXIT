package com.example.dixit_app.view.frases

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
import com.example.dixit_app.databinding.FragmentFraseModificarBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Frase
import com.example.dixit_app.model.entities.FrasePictogramaRC
import com.example.dixit_app.model.entities.Pictograma
import com.example.dixit_app.model.repository.FraseRepository
import com.example.dixit_app.view.FrasesActivity
import com.example.dixit_app.viewmodel.FraseViewModel
import com.example.dixit_app.viewmodel.FraseViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FraseModificarFragment : Fragment(), TopAdapterClickFraseInterface, BottomAdapterClickFraseInterface,
                                MenuProvider, SearchView.OnQueryTextListener{

    private lateinit var binding: FragmentFraseModificarBinding

    //Instancia contexto de navegación de este Fragment
    private val args: FraseModificarFragmentArgs by navArgs()

    //Inicializo var Frase para instanciar la frases seleccionada previamente
    private lateinit var currentFrase: Frase

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    private lateinit var pictogramasFraseRVTopAdapter: PictogramasFraseRVTopAdapter
    private lateinit var pictogramasFraseRVBottomAdapter: PictogramasFraseRVBottomAdapter

    //un View Model para Frase
    private lateinit var fraseViewModel: FraseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        binding = FragmentFraseModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, lifecycle.currentState)

        //Busqueda
        binding.searchView.setOnQueryTextListener(this)

        currentFrase = args.frase!!

        //Reemplazo título de toolbar Activity
        (activity as FrasesActivity).supportActionBar?.title = "Frase: "+currentFrase.nombreFrase

        val application = requireNotNull(this.activity).application
        //Iniciar el Repositorio de Frase para ViewModel
        val fraseRepository = FraseRepository(DixitDatabase(application))

        val viewModelProviderFactory = FraseViewModelFactory(application, fraseRepository)

        //Instancia de FraseViewModel
        fraseViewModel = ViewModelProvider(this, viewModelProviderFactory)
                        .get(FraseViewModel::class.java)

        //Inicializar el RV superior: los pictogramas de la Frase, si los hay
        setTopRecyclerView()
        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()
    }

    //VER que estan ambos RV, no solo Bottom, en esta funcion
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasFraseRVBottomAdapter = PictogramasFraseRVBottomAdapter(this)
        binding.rvBottomFrase.apply {
            val orientation = resources.configuration.orientation
            layoutManager = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Orientación vertical
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            } else {
                // Orientación horizontal
                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            }
            setHasFixedSize(true)
            adapter = pictogramasFraseRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            fraseViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner) { pictogramas ->
                    pictogramasFraseRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                }
        }

        //Asociar adapter para RV Top (los pictogramas seleccionados)
        pictogramasFraseRVTopAdapter = PictogramasFraseRVTopAdapter(this)
        binding.rvTopFrase.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = pictogramasFraseRVTopAdapter
        }
    }

    private fun setTopRecyclerView(){
        //Cargar pictogramas de Frase en RV Top
        //Es Ref. Cruzada los pictogramas de la frases (Y DEBO ENVIAR ARGUMENTO FRASE)
        activity?.let {
            fraseViewModel.getFrasePictogramas(currentFrase.nombreFrase)
                .observe(viewLifecycleOwner) { frasePictogramas ->
                    //obtener listado de Pictogramas a FrasesconPictogramas (tipo de dato del differTop)
                    pictogramasFraseRVTopAdapter.differTop.submitList(frasePictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                }
        }
    }

    override fun onItemRVBottomClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV inferior, se agrega el ítem al RV superior
       //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasFraseRVTopAdapter.differTop.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasFraseRVTopAdapter.differTop.submitList(copiedList)
    }


    //TODO editar implementación
    override fun onItemRVTopClick(pictograma: Pictograma) {
        //OJO, quizásme reescribe lita.... siempre

        //Cuando se realiza un clic en el RV superior, se agrega el ítem al RV inferior
        //Realizo copia de los resultados, casteando a una MutableList

        //val copiedList = pictogramasFraseRVBottomAdapter.differBottom.currentList.toMutableList()

        //Elimino de la lista copiada el elemento de la posición clickeada
        //copiedList.add(pictograma)

        //Le envío la lista resultante al differ
       //pictogramasFraseRVBottomAdapter.differBottom.submitList(copiedList)

        //ESto no, porque se hace en adapter.
        //pictogramasFraseRVTopAdapter.differTop.currentList.remove(pictograma)

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_guardar, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            //accedo al layout a través de su id
            R.id.menu_guardar -> {
                //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
                val pictogramas:List<Pictograma> = pictogramasFraseRVTopAdapter.differTop.currentList
                //Crear un objeto FrasePictogramaRC (referencia cruzada)
                var pictosFrase : FrasePictogramaRC
                //Recorrer la lista para para guardar el id de cada pictograma
                //Eliminar los pares de FrasePicctogramaRC con el idFrase actual
                for (picto in pictogramas){
                    //MOD id FrasePictogramaRC
                    pictosFrase = FrasePictogramaRC(0, currentFrase.idFrase, picto.idPictograma)
                    fraseViewModel.insertPictogramasFrase(pictosFrase)
                }

                Snackbar.make(
                    requireView(), "Pictogramas agregados a la Frase exitosamente.",
                    Snackbar.LENGTH_SHORT
                ).show()

                val direction = FraseModificarFragmentDirections
                    .actionFraseModificarFragmentToFrasesFragment()
                view?.findNavController()?.navigate(direction)
                true
            }
            //when para botón extra (si hace falta)
            else -> false
        }
    }

    private fun buscarPictograma(query: String?) {
        val searchQuery = "%$query%"
        fraseViewModel.searchPictograma(searchQuery).observe(this) { list ->
            pictogramasFraseRVBottomAdapter.differBottom.submitList(list)
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
        Glide.with(this).clear(binding.rvBottomFrase)
        super.onStop()
    }

}