package com.example.dixit_app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.dixit_app.R
import com.example.dixit_app.databinding.ActivityMainBinding
import com.example.dixit_app.databinding.ActivitySplashBinding

//Activity splash (pantalla de presentación)
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Thread.sleep(2000)

        Handler().postDelayed({
            //Iniciar MainActivity posteriormente
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            //impedir regresar con el botón Atrás
        }, 3000)
    }
}