package com.example.dixit_app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.dixit_app.databinding.ActivityCategoriasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.repository.CategoriaRepository
import com.example.dixit_app.viewmodel.CategoriaViewModel
import com.example.dixit_app.viewmodel.CategoriaViewModelFactory


class CategoriasActivity : AppCompatActivity() {

    //TODO botón superior esq. izquierda "atrás" (flecha)
    private lateinit var binding: ActivityCategoriasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCategorias)

    }


}