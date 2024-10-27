package com.example.sertifikasi_mobile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sertifikasi_mobile.SQLite.DataPemilihDBHelper
import com.example.sertifikasi_mobile.databinding.ActivityTableBinding


class TableActivity : AppCompatActivity(), OnDataChangedListener {
    private lateinit var itemAdapter: RecycleAdapter
    private lateinit var binding: ActivityTableBinding
    private lateinit var db : DataPemilihDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityTableBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val headerTitle = findViewById<TextView>(R.id.headerTitle_txt)
        headerTitle.text = "LIHAT DATA"

        with(binding){

            db = DataPemilihDBHelper(this@TableActivity)
            val itemList = db.getAllData()
            recyclerView.layoutManager = LinearLayoutManager(this@TableActivity)

            itemAdapter = RecycleAdapter(itemList, this@TableActivity)
            recyclerView.adapter = itemAdapter
            binding.bottomText.text = "Jumlah data saat ini : " + itemAdapter.getItemCount().toString()
        }
    }

    override fun onDataChanged(newCount: Int) {
        binding.bottomText.text = "Jumlah data saat ini : $newCount"
    }
}