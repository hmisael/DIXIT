package com.example.dixit_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.navigation.findNavController
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentMainBinding


class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgCategorias.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.categoriasActivity)
        }


        binding.imgFrases.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.frasesActivity)
        }


        binding.imgPreguntas.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.preguntasActivity)
        }

        binding.imgRutinas.setOnClickListener{ view ->
            view.findNavController().navigate(R.id.rutinasActivity)
        }



    }


    }