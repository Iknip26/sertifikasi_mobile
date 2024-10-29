package com.example.sertifikasi_mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sertifikasi_mobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            submitBtn.setOnClickListener(){
                if(usernameEDT.text.toString()!="" && passwordEDT.text.toString()!=""){
                    if(usernameEDT.text.toString()=="admin" && passwordEDT.text.toString()=="admin123"){
                        val intentToMain = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intentToMain)
                    }
                }else{
                    Toast.makeText(this@LoginActivity, "Kolom harus terisi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}