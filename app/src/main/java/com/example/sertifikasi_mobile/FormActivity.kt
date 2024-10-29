package com.example.sertifikasi_mobile

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FormActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, CekLokasiDialogFragment.LocationListener,
    DialogOpenFile.OnOptionSelectedListener, DialogConfirm.DialogListener {
    private lateinit var binding: ActivityFormBinding
    private var photoUri: Uri? = null
    private var filePath: String? = ""
    private lateinit var genderInp : String
    private lateinit var db : DataPemilihDBHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        genderInp = ""
        val headerTitle = findViewById<TextView>(R.id.headerTitle_txt)
        headerTitle.text = "TAMBAH DATA"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@FormActivity)

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

            addressBtn.setOnClickListener(){
                val dialog = CekLokasiDialogFragment()
                dialog.show(supportFragmentManager, "Dialog Maps")
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

    private fun getAddressInfo(latitude:Double, longitude:Double) {
        val geocoder = Geocoder(this@FormActivity, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        val address: String = addresses!![0].getAddressLine(0)
        binding.addressEdt.setText(address)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@FormActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@FormActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@FormActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getAddressInfo(location.latitude,location.longitude)
                } else {
                    Log.e("CurrentLocation", "Location not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CurrentLocation", "Failed to get location: ${e.message}")
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

    private fun createImageFile(customDirPath: String): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val storageDir = File(customDirPath)

        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("FormActivity", "Failed to create custom directory: $customDirPath")
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
                val address = addressEdt.text.toString().trim()
                val imageURLS = filePath?.trim()
                val dataPemilihInp =
                    imageURLS?.let { DataPemilih(0, name, NIK, contact, gender, date, address, it) }
                if (dataPemilihInp != null) {
                    db.insertData(dataPemilihInp)
                }
            }
            val intentToResult = Intent(this@FormActivity, MainActivity::class.java)
            startActivity(intentToResult)
            finish()
//            Toast.makeText(this@FormActivity, filePath?.trim(), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this@FormActivity, "GAGALLLL", Toast.LENGTH_SHORT).show()
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
                filePath = "/storage/emulated/0/DCIM/GambarKamera/$fileName"
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
                val fileName = getFileName(uri)
                binding.filenameTxt.visibility = View.VISIBLE
                binding.filenameTxt.text = fileName
                filePath = getRealPathFromURI(this, uri)
            }
        }
    }

    override fun onCameraSelected() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val photoFile = createImageFile("storage/emulated/0/DCIM/GambarKamera")
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

    override fun onLocationSelected(latLng: LatLng) {
        getAddressInfo(latLng.latitude, latLng.longitude)
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

class CekLokasiDialogFragment : DialogFragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private var currentLatLng: LatLng? = null
    private var locationListener: LocationListener? = null


    // Define the launcher for requesting location permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[ACCESS_FINE_LOCATION] == true ||
                permissions[ACCESS_COARSE_LOCATION] == true
            ) {
                // Permission granted
                getCurrentLocation()
            } else {
                // Permission denied
                // Handle what to do if the user denies the permission
            }
        }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        getCurrentLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)

            // Launch permission request using the new API
            requestPermissionLauncher.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
            )

            return
        }

        googleMap.isMyLocationEnabled = true

        // Get the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLatLng = LatLng(it.latitude, it.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLatLng!!).title("Posisimu"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentLatLng?.let {
            locationListener?.onLocationSelected(it)
        }
    }

    interface LocationListener {
        fun onLocationSelected(latLng: LatLng)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        locationListener = context as? LocationListener
    }
}