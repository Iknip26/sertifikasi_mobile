package com.example.sertifikasi_mobile.SQLite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DataPemilih(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val name: String,
    val nik: String,
    val contact: String,
    val gender: String,
    val date: String,
    val address: String,
    val imageuri: String,
    )