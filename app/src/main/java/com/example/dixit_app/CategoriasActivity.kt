package com.example.dixit_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.databinding.ActivityCategoriasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.repository.CategoriaRepository
import com.example.dixit_app.viewmodel.CategoriaViewModel
import com.example.dixit_app.viewmodel.CategoriaViewModelFactory
import com.swein.easypermissionmanager.EasyPermissionManager

class CategoriasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCategorias)

        //setUpViewModel()
    }

    //ESTO
    /*@Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }*/


    /*private fun setUpViewModel() {
        val categoriaRepository = CategoriaRepository(
            DixitDatabase(this)
        )

        val viewModelProviderFactory =
            CategoriaViewModelFactory(
                application, categoriaRepository
            )

        categoriaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory)
            .get(CategoriaViewModel::class.java)

    }*/

}