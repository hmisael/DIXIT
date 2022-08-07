package com.example.dixit_app.ui.frases

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.FrasesActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentFraseModificarBinding

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.FrasePictogramaRC
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.FraseRepository
import com.example.dixit_app.ui.*
import com.example.dixit_app.viewmodel.FraseViewModel
import com.example.dixit_app.viewmodel.FraseViewModelFactory

class FraseModificarFragment : Fragment(R.layout.fragment_frase_modificar),
    TopAdapterClickFraseInterface, BottomAdapterClickFraseInterface {

    private var _binding: FragmentFraseModificarBinding? = null
    private val binding get() = _binding!!

    //Instancia contexto de navegación de este Fragment
    private val args: FraseModificarFragmentArgs by navArgs()

    //Inicializo var Frase para instanciar la frases seleccionada previamente
    private lateinit var currentFrase: Frase

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    private lateinit var pictogramasFraseRVTopAdapter: PictogramasFraseRVTopAdapter
    private lateinit var pictogramasFraseRVBottomAdapter: PictogramasFraseRVBottomAdapter

    //un View Model para Frase
    private lateinit var fraseViewModel: FraseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Aún no, necesito agregar referencia al onqueryTextLitener en declaración Fragmetn
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
        _binding = FragmentFraseModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as FrasesActivity).supportActionBar?.title = "Modificar Frase"

        val application = requireNotNull(this.activity).application
        currentFrase = args.frase!!

        //Iniciar el Repositorio de Frase para ViewModel
        val fraseRepository = FraseRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory = FraseViewModelFactory(application, fraseRepository)

        //Instancia de FraseViewModel
        fraseViewModel = ViewModelProvider(this, viewModelProviderFactory)
                        .get(FraseViewModel::class.java)

        //Inicializar el RV superior: los pictogramas de la Frase, si los hay
        setTopRecyclerView()
        //Inicializar el RV inferior: todos los pictogramas disponibles
        setBottomRecyclerView()

        //Botón para guardar los pictogramas en la Frase a crear ahora
        binding.fabDone.setOnClickListener{
            //Obtener los pictogramas de la lista del Top Adapter (los seleccionados)
            val pictogramas:List<Pictograma> = pictogramasFraseRVTopAdapter.differTop.currentList
            //Crear un objeto FrasePictogramaRC (referencia cruzada)
            var pictosFrase : FrasePictogramaRC
            //Recorrer la lista para para guardar el id de cada pictograma
            //Log.i("MODIFIC_IDFRASE", "IDFRASE: ${currentFrase.idFrase}")
            for (picto in pictogramas){
                //MOD id FrasePictogramaRC
                pictosFrase = FrasePictogramaRC(0, currentFrase.idFrase, picto.idPictograma)
                fraseViewModel.insertPictogramasFrase(pictosFrase)
            }
            val direction = FraseModificarFragmentDirections
                .actionFraseModificarFragmentToFrasesFragment()
            view.findNavController().navigate(direction)

            //Notificar datos actualizados al Adapter
            pictogramasFraseRVTopAdapter.notifyDataSetChanged()
            //Notificar datos actualizados al Adapter
            pictogramasFraseRVBottomAdapter.notifyDataSetChanged()
        }
    }

    //VER que estan ambos RV, no solo Bottom, en esta funcion
    private fun setBottomRecyclerView() {
        //Asociar adapter para RV Bottom (los pictogramas guardados)
        pictogramasFraseRVBottomAdapter = PictogramasFraseRVBottomAdapter(this)
        binding.rvBottomFrase.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictogramasFraseRVBottomAdapter
        }

        //Carga de datos en RV Bottom
        activity?.let {
            //¿USAMOS UN REPOSITORIO PARA TOOOODO?
            //O BIEN, DESDE FRASEVIEWMODEL, LE AGREGO FUNCION GETALLPICTOGRAMAS
            fraseViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner, { pictogramas ->
                    pictogramasFraseRVBottomAdapter.differBottom.submitList(pictogramas)
                    //updateUI(pictogramas)
                })
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
                .observe(viewLifecycleOwner, { frasePictogramas ->
                    //obtener listado de Pictogramas a FrasesconPictogramas (tipo de dato del differTop)
                    pictogramasFraseRVTopAdapter.differTop.submitList(frasePictogramas.get(0).pictogramas)
                    //updateUI(pictogramas)
                })
        }
    }

    /*private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.cardView3.visibility = View.GONE
            binding.rvBottomFrase.visibility = View.VISIBLE
        } else {
            binding.cardView3.visibility = View.VISIBLE
            binding.rvBottomFrase.visibility = View.GONE
        }
    }*/

    override fun onItemRVBottomClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV inferior, se agrega el ítem al RV superior
       //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasFraseRVTopAdapter.differTop.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasFraseRVTopAdapter.differTop.submitList(copiedList)
    }

    override fun onItemRVTopClick(pictograma: Pictograma) {
        //Cuando se realiza un clic en el RV superior, se agrega el ítem al RV inferior
        //Realizo copia de los resultados, casteando a una MutableList
        val copiedList = pictogramasFraseRVBottomAdapter.differBottom.currentList.toMutableList()
        //Elimino de la lista copiada el elemento de la posición clickeada
        copiedList.add(pictograma)
        //Le envío la lista resultante al differ
        pictogramasFraseRVBottomAdapter.differBottom.submitList(copiedList)
    }

    override fun onStop() {
        Glide.with(this).clear(binding.rvBottomFrase)
        //Glide.with(this).clear(binding.rvTopFrase)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}