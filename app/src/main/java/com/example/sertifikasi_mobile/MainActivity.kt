package com.example.sertifikasi_mobile

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sertifikasi_mobile.databinding.ActivityFormBinding
import com.example.sertifikasi_mobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        with(binding){
            formBtn.setOnClickListener() {

                val intentToForm = Intent(this@MainActivity, FormActivity::class.java)
                startActivity(intentToForm)
            }

            prevDataBtn.setOnClickListener(){
                val intentToPreview = Intent(this@MainActivity, TableActivity::class.java)
                startActivity(intentToPreview)
            }

            }
        }
}