package com.example.dixit_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dixit_app.databinding.ActivityFrasesBinding
import com.example.dixit_app.databinding.ActivityPreguntasBinding
import com.example.dixit_app.databinding.ActivityRutinasBinding

class FrasesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrasesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFrasesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarFrases)

    }
}