package com.example.dixit_app

import android.content.Intent
import android.os.Bundle
import android.window.SplashScreen
import androidx.appcompat.app.AppCompatActivity

//Activity splash (pantalla de presentación)
class SplashActivity : AppCompatActivity() {
    //Iniciar MainActivity posteriormente
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        //impedir regresar con el botón Atrás
        finish()

    }
}