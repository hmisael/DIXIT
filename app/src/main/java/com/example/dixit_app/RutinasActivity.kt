package com.example.dixit_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.databinding.ActivityRutinasBinding
import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.repository.RutinaRepository
import com.example.dixit_app.viewmodel.RutinaViewModel
import com.example.dixit_app.viewmodel.RutinaViewModelFactory

class RutinasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRutinasBinding
    //lateinit var rutinaViewModel: RutinaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRutinasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarRutinas)

        //setUpViewModel()
    }

/*
    private fun setUpViewModel() {
        val rutinaRepository = RutinaRepository(
            DixitDatabase(this)
        )

        val viewModelProviderFactory =
            RutinaViewModelFactory(
                application, rutinaRepository
            )

        rutinaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory)
            .get(RutinaViewModel::class.java)
    }*/
}