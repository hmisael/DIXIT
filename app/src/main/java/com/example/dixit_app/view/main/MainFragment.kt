package com.example.dixit_app.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController

import com.example.dixit_app.R
import com.example.dixit_app.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private var isCameraPermissionGranted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                    permissions ->
                isReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
                isWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
                isCameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: isCameraPermissionGranted

            }

        requestPermission()

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

    private fun requestPermission(){
        val context : Context = requireContext()

        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        //------------------------------------------------------------------

        val permissionRequest : MutableList<String> = ArrayList()

        if (!isReadPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!isCameraPermissionGranted){
            permissionRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }
}