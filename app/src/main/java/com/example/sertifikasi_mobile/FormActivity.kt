package com.example.sertifikasi_mobile

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.sertifikasi_mobile.SQLite.DataPemilih
import com.example.sertifikasi_mobile.SQLite.DataPemilihDBHelper
import com.example.sertifikasi_mobile.databinding.ActivityFormBinding
import com.example.sertifikasi_mobile.databinding.DialogConfirmBinding
import com.example.sertifikasi_mobile.databinding.OpenFileDialogBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FormActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    DialogOpenFile.OnOptionSelectedListener, DialogConfirm.DialogListener {
    private lateinit var binding: ActivityFormBinding
    private var photoUri: Uri? = null
    private var filePath: String? = ""
    private lateinit var genderInp : String
    private lateinit var db : DataPemilihDBHelper

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        genderInp = ""
        val headerTitle = findViewById<TextView>(R.id.headerTitle_txt)
        headerTitle.text = "TAMBAH DATA"

        with(binding){
            dateBtn.setOnClickListener {
                val datePicker = DatePicker()
                datePicker.show(supportFragmentManager, "datePicker")
            }

            fileBtn.setOnClickListener {
                val dialog = DialogOpenFile()
                dialog.listener = this@FormActivity
                dialog.show(supportFragmentManager, "DialogOpenFile")
            }

            submitBtn.setOnClickListener {
                if(fieldNotEmpty()){
                    val dialog = DialogConfirm()
                    dialog.show(supportFragmentManager, "DialogConfirm")
                }else{
                    Toast.makeText(this@FormActivity, "MASIH ADA KOLOM YANG KOSONG !", Toast.LENGTH_SHORT).show()
                }
            }

            genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.perempuan_radioBTN -> {
                        genderInp = perempuanRadioBTN.text.toString()
                    }
                    R.id.laki_laki_radioBTN -> {
                        genderInp = lakiLakiRadioBTN.text.toString()
                    }
                }
            }

        }
    }

    fun fieldNotEmpty(): Boolean {
        with(binding) {
//            Toast.makeText(this@FormActivity, nameEDT.text.toString() + NIKEDT.text.toString() + contactEDT.text.toString() + gender + dateBtn.text.toString() + addressBtn.text.toString() + filenameTxt.text.toString(), Toast.LENGTH_LONG).show()
            if (nameEDT.text.toString() != "" && NIKEDT.text.toString() != "" && contactEDT.text.toString() != "" && genderInp!="" && dateBtn.text.toString() != "" && addressBtn.text.toString() != "" && filenameTxt.text.toString() != "") {
                return true
            }else{
                return false
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (storageDir == null) {
            Log.e("FormActivity", "Storage directory is null")
            return null
        }

        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                photoUri = FileProvider.getUriForFile(
                    this@FormActivity,
                    "${applicationContext.packageName}.fileprovider",
                    this
                )
            }
        } catch (ex: IOException) {
            Log.e("FormActivity", "Error creating image file: ${ex.message}", ex)
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }


    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }

    override fun onDateSet(p0: android.widget.DatePicker?, p1: Int, p2: Int, p3:
    Int) {
        val selectedDate = "$p3/${p2 + 1}/$p1"
        binding.dateBtn.text = selectedDate
    }


//    KONFIMASI DIALOG OK, CREATE DATA DB
    override fun onDialogResult(result: Boolean) {
        db = DataPemilihDBHelper(this)
        if (result) {
            with(binding){
//                Toast.makeText(this@FormActivity, nameEDT.text.toString() + NIKEDT.text.toString() + contactEDT.text.toString() + genderInp + dateBtn.text.toString() + addressBtn.text.toString() + filenameTxt.text.toString(), Toast.LENGTH_LONG).show()
                val name = nameEDT.text.toString().trim()
                val NIK = NIKEDT.text.toString().trim()
                val contact = contactEDT.text.toString().trim()
                val gender = genderInp.trim()
                val date = dateBtn.text.toString().trim()
                val address = addressBtn.text.toString().trim()
                val imageURLS = filenameTxt.text.toString().trim()
                val dataPemilihInp =
                    imageURLS?.let { DataPemilih(0, name, NIK, contact, gender, date, address, it) }
                if (dataPemilihInp != null) {
                    db.insertData(dataPemilihInp)
                }
            }
            val intentToResult = Intent(this@FormActivity, MainActivity::class.java)
            startActivity(intentToResult)
            finish()
            Toast.makeText(this@FormActivity, "Data berhasil di tambahkan", Toast.LENGTH_SHORT).show()
        }else{
//            Toast.makeText(this@FormActivity, "GAGALLLL", Toast.LENGTH_SHORT).show()
        }
    }


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoUri?.let {
                binding.imageUploadImg.setImageURI(it)
                binding.defaultImg.visibility = View.GONE
                binding.defaultTextTxt.visibility = View.GONE
                binding.imageUploadImg.visibility = View.VISIBLE
                val fileName = getFileName(photoUri!!)
                binding.filenameTxt.visibility = View.VISIBLE
                binding.filenameTxt.text = fileName
            }
        }else{
            Toast.makeText(this@FormActivity, "BUKA KAMERA GAGAL", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                binding.imageUploadImg.setImageURI(uri)
                binding.defaultImg.visibility = View.GONE
                binding.defaultTextTxt.visibility = View.GONE
                binding.imageUploadImg.visibility = View.VISIBLE
                val fileName = uri.toString()
                binding.filenameTxt.visibility = View.VISIBLE
                binding.filenameTxt.text = fileName
            }
        }
    }

    override fun onCameraSelected() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                photoUri?.let { cameraLauncher.launch(it) }
            } else {
                Toast.makeText(this, "Failed to create file for photo.", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }


    override fun onGallerySelected() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraSelected()
        } else {
            Toast.makeText(this, "Camera permission is required to use the camera.", Toast.LENGTH_SHORT).show()
        }
    }
}

//CLASS UNTUK BUAT DIALOG OPENFILE
class DialogOpenFile : DialogFragment() {

    interface OnOptionSelectedListener {
        fun onCameraSelected()
        fun onGallerySelected()
    }

    var listener: OnOptionSelectedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val binding = OpenFileDialogBinding.inflate(inflater)

        with(binding) {
            cameraBtn.setOnClickListener {
                listener?.onCameraSelected()
                dismiss()
            }
            galleryBtn.setOnClickListener {
                listener?.onGallerySelected()
                dismiss()
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }
}


//CLASS UNTUK BUAT DATEPICKER
class DatePicker: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val monthOfYear = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireActivity(),
            activity as DatePickerDialog.OnDateSetListener,
            year,
            monthOfYear,
            dayOfMonth
        )
    }
}

//CLASS UNTUK BUAT DIALOG KONFIRMASI
class DialogConfirm : DialogFragment() {

    interface DialogListener {
        fun onDialogResult(result: Boolean)
    }
    private lateinit var listener: DialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val binding = DialogConfirmBinding.inflate(inflater)

        with(binding) {
            yesBtn.setOnClickListener {
                listener.onDialogResult(true)
                dismiss()
            }
            noBtn.setOnClickListener {
                listener.onDialogResult(false)
                dismiss()
            }
        }
        builder.setView(binding.root)
        return builder.create()
    }
}