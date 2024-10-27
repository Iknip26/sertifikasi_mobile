package com.example.sertifikasi_mobile.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataPemilihDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME =  "pemilihan.db"
        private const val DATABASE_VERSION =  1
        private const val TABLE_NAME =  "pemilih"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_NIK = "nik"
        private const val COLUMN_CONTACT = "contact"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_IMAGEURI = "uri"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_ADDRESSS = "address"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_NIK TEXT NOT NULL,
            $COLUMN_CONTACT TEXT,
            $COLUMN_GENDER TEXT,
            $COLUMN_IMAGEURI TEXT,
            $COLUMN_DATE TEXT,
            $COLUMN_ADDRESSS TEXT
        )
    """.trimIndent()
        db?.execSQL(createTableQuery)    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertData(dataPemilih: DataPemilih){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, dataPemilih.name)
            put(COLUMN_NIK, dataPemilih.nik)
            put(COLUMN_CONTACT, dataPemilih.contact)
            put(COLUMN_GENDER, dataPemilih.gender)
            put(COLUMN_DATE, dataPemilih.date)
            put(COLUMN_ADDRESSS, dataPemilih.address)
            put(COLUMN_IMAGEURI, dataPemilih.imageuri)
        }

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllData() : MutableList<DataPemilih>{
        val notesList = mutableListOf<DataPemilih>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val nik = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIK))
            val contact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT))
            val gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESSS))
            val imageUrls = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEURI))

            val data = DataPemilih(id, name, nik, contact, gender, date, address, imageUrls)
            notesList.add(data)
        }

        cursor.close()
        db.close()

        return notesList
    }

    fun getDetailData(id: Int): DataPemilih? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        var data: DataPemilih? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val nik = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIK))
            val contact = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT))
            val gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESSS))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEURI))
            data = DataPemilih(id, name, nik, contact, gender, date, address, imageUri)
        }

        cursor.close()
        db.close()
        return data
    }

    fun deleteData(id: Int){
        val db = writableDatabase
        val deletedRows = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}