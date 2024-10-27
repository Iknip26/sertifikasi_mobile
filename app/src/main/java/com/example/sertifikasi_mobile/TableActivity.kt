package com.example.sertifikasi_mobile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sertifikasi_mobile.SQLite.DataPemilihDBHelper
import com.example.sertifikasi_mobile.databinding.ActivityTableBinding


class TableActivity : AppCompatActivity(){
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
            itemAdapter = RecycleAdapter(itemList)
            if(itemAdapter.getItemCount() == 0){
                emptyDataView.visibility = View.VISIBLE
            }else{
                emptyDataView.visibility = View.GONE
            }
            recyclerView.adapter = itemAdapter
        }
    }
}