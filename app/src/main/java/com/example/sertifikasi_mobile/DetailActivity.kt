package com.example.sertifikasi_mobile

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.sertifikasi_mobile.SQLite.DataPemilih
import com.example.sertifikasi_mobile.SQLite.DataPemilihDBHelper
import com.example.sertifikasi_mobile.databinding.ActivityDetailBinding

import java.io.File

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private var dataId: Int = -1
    private lateinit var db : DataPemilihDBHelper
    private lateinit var dataPemilih : DataPemilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val headerTitle = findViewById<TextView>(R.id.headerTitle_txt)
        headerTitle.text = "DATA PEMILIH"

        db = DataPemilihDBHelper(this@DetailActivity)
        dataId = intent.getIntExtra("data_id", -1)
        dataPemilih = db.getDetailData(dataId)!!

        with(binding){
            nameTxt.text = dataPemilih.name
            NIKTxt.text = dataPemilih.nik
            contactTxt.text = dataPemilih.contact
            genderTxt.text = dataPemilih.gender
            dateTxt.text = dataPemilih.date
            addressTxt.text = dataPemilih.address
            if (dataPemilih.imageuri.isNotEmpty()) {
                try {
                    val uri = Uri.parse(dataPemilih.imageuri)
                    Toast.makeText(this@DetailActivity, dataPemilih.imageuri, Toast.LENGTH_SHORT).show()
                    setImageUri(dataPemilih.imageuri)
                    Toast.makeText(this@DetailActivity, "Gambar berhasil dimuat", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@DetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    e.printStackTrace() // Cetak log untuk melihat detail error di Logcat
                }
            } else {
                Toast.makeText(this@DetailActivity, "URI KOSONG", Toast.LENGTH_SHORT).show()
            }

        }
//        Toast.makeText(this@DetailActivity, dataId.toString(), Toast.LENGTH_SHORT).show()
    }

    fun setImageUri(imageUriString: String) {
        val uri: Uri? = if (imageUriString.contains("/Android/data/")) {
            val file = File(imageUriString)
            FileProvider.getUriForFile(
                this@DetailActivity,
                "${this@DetailActivity.packageName}.fileprovider",
                file
            )
        } else {
            Uri.parse(imageUriString)
        }
        if (uri != null) {
            binding.imageImg.setImageURI(uri)
        } else {
            Log.e("ImageError", "URI is null or invalid.")
        }
    }
}