package com.example.projqrcode


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    private lateinit var qrCodeImageView: ImageView
    private lateinit var raEditText: EditText
    private var qrCodeBitmap: Bitmap? = null
    private var photoBitmap: Bitmap? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qrCodeImageView = findViewById(R.id.qrCodeImageView)
        raEditText = findViewById(R.id.raEditText)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val scanQrCodeButton: Button = findViewById(R.id.scanQrCodeButton)
        scanQrCodeButton.setOnClickListener {
            scanQRCode()
        }

        val takePhotoButton: Button = findViewById(R.id.takePhotoButton)
        takePhotoButton.setOnClickListener {
            val qrCodeMessage = qrCodeTextView.text.toString()
            if (qrCodeMessage.isNotEmpty()) {
                takePhoto(qrCodeMessage)
            } else {
                Toast.makeText(this, "Escaneie o QR Code primeiro", Toast.LENGTH_SHORT).show()
            }
        }

        val submitButton: Button = findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            submitData()
        }

        // Verifica a permissão de localização
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this, OnSuccessListener<Location> { location ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    private fun takePhoto(qrCodeMessage: String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun submitData() {
        val qrCodeMessage = qrCodeTextView.text.toString()
        val ra = raEditText.text.toString()

        if (qrCodeMessage.isEmpty()) {
            Toast.makeText(this, "Complete todos os procedimentos", Toast.LENGTH_SHORT).show()
            return
        }
        else
            if (ra.isEmpty()){
                Toast.makeText(this, "Digite o RA", Toast.LENGTH_SHORT).show()
                return
            }
        else
            if (!isValidRA(ra)){
                Toast.makeText(this, "Digite um RA válido", Toast.LENGTH_SHORT).show()
                return
            }
        else
            if ( photoBitmap == null){
                Toast.makeText(this, "Tire uma foto", Toast.LENGTH_SHORT).show()
                return
            }

        if (currentLocation != null) {
            val data = SubmissionData(qrCodeMessage, ra, currentLocation!!, qrCodeBitmap, photoBitmap)
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("submissionData", data)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Localização não disponível", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidRA(ra: String): Boolean {
        val regex = Regex("^[0-9]{5}$")
        return regex.matches(ra)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    "Permissão de Locaização negada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
                } else {
                    qrCodeTextView.text = result.contents
                    Toast.makeText(this, "Escaneado: ${result.contents}", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            photoBitmap = imageBitmap
            // Aqui você pode usar a imagem capturada
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_IMAGE_CAPTURE = 1002
    }
}
