package com.example.dixit_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.dixit_app.databinding.ActivityCategoriasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.repository.CategoriaRepository
import com.example.dixit_app.viewmodel.CategoriaViewModel
import com.example.dixit_app.viewmodel.CategoriaViewModelFactory
import com.swein.easypermissionmanager.EasyPermissionManager

class CategoriasActivity : AppCompatActivity() {

    //New flecha atrás
    //private lateinit var navController : NavController

    private lateinit var binding: ActivityCategoriasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCategorias)

        //New para barra flecha atrás
        //setupActionBarWithNavController(navController)
    }

    /*
    //New flecha atrás
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }*/
}