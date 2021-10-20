package com.example.dixit_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentCategoriaDetalleBinding
import com.example.dixit_app.databinding.FragmentRutinaDetalleBinding


class RutinaDetalleFragment : Fragment(R.layout.fragment_rutina_detalle){

    //Patrón binding layout de diseño para este Fragment
    private var _binding: FragmentRutinaDetalleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRutinaDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

}