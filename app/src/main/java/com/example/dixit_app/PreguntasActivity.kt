package com.example.dixit_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dixit_app.databinding.ActivityPreguntasBinding

class PreguntasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreguntasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreguntasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarPreguntas)
    }
}