package com.example.dixit_app.view

import android.location.GnssAntennaInfo
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.dixit_app.FrasesActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentFraseModificarBinding
import com.example.dixit_app.helper.Listener
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository
import com.example.dixit_app.viewmodel.FraseViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModel
import com.example.dixit_app.viewmodel.PictogramaViewModelFactory




class FraseModificarFragment : Fragment(R.layout.fragment_frase_modificar){

    private var _binding: FragmentFraseModificarBinding? = null
    private val binding get() = _binding!!


    //un View Model para Pictogramas.
    private lateinit var pictogramaViewModel: PictogramaViewModel

    //Instancia contexto de navegación de este Fragment
    private val args: FraseModificarFragmentArgs by navArgs()
    //Inicializo var Frase para instanciar la frase seleccionada previamente
    private lateinit var currentFrase: Frase

    //Adapter para ambos RV (no carga, solo es la vista del RV)
    private lateinit var pictosFraseRVTopAdapter : PictosFraseRVAdapter
    private lateinit var pictosFraseRVBottomAdapter : PictosFraseRVAdapter

    //un View Model para Frase
    private lateinit var fraseViewModel: FraseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Aún no, necesito agregar referencia al onqueryTextLitener en declaración Fragmetn
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFraseModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reemplazo título de toolbar Activity
        (activity as FrasesActivity).supportActionBar?.title = "Modificar Frase"

        //usar adapter y viewmodel de Pictogramas (porque CategoriaDetalleFragment lo usa)

        val application = requireNotNull(this.activity).application

        currentFrase = args.frase!!

        //En Frase, al igual que Categoría, necesito mostrar Pictogramas PERO tbn drag&drop
        val pictogramaRepository = PictogramaRepository(
            DixitDatabase(application)
        )

        val viewModelProviderFactory =
            PictogramaViewModelFactory(
                application, pictogramaRepository
            )

        pictogramaViewModel = ViewModelProvider(
            this, viewModelProviderFactory
        ).get(PictogramaViewModel::class.java)

        //CARGAR DATOS EN AMBOS RECYCLER

        //EL RV VACIO O CON PICTOGRAMAS YA GUARDADOS
        //setTopRecyclerView()
        //RV CON TODOS LOS PICTOGRAMAS PARA ELEGIR
        setBottomRecyclerView()


        /*binding.tvSinPictogramas.pi {
            //FAB botón para guardar
        }*/
    }


    //MODIFICACION
    private fun setTopRecyclerView() {


    }

    private fun setBottomRecyclerView() {
        pictosFraseRVBottomAdapter = PictosFraseRVAdapter()
        binding.rvBottomFrase.apply {
            layoutManager = StaggeredGridLayoutManager(
                3,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = pictosFraseRVBottomAdapter
       }

        //Carga de datos en RV inferior

        activity?.let {
            pictogramaViewModel.getAllPictogramas()
                .observe(viewLifecycleOwner, { pictogramas ->
                  pictosFraseRVBottomAdapter.differ.submitList(pictogramas)
                    updateUI(pictogramas)
                })
        }




    }

    private fun updateUI(pictogramas: List<Pictograma>) {
        if (pictogramas.isNotEmpty()) {
            binding.cardView3.visibility = View.GONE
            binding.rvBottomFrase.visibility = View.VISIBLE
        } else {
            binding.cardView3.visibility = View.VISIBLE
            binding.rvBottomFrase.visibility = View.GONE
        }
    }




    override fun onStop() {
        Glide.with(this).clear(binding.rvBottomFrase)
        //Glide.with(this).clear(binding.rvTopFrase)
        super.onStop()
    }

}