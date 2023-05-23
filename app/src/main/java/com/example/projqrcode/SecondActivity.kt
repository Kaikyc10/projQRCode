package com.example.projqrcode

import android.graphics.Bitmap
import android.graphics.Color
import com.example.projqrcode.SubmissionData
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    private fun generateQRCodeBitmap(message: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, 500, 500)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Obter os dados enviados da MainActivity
        val submissionData = intent.getParcelableExtra<SubmissionData>("submissionData")
        if (submissionData != null) {
            val ra = submissionData.ra
            val raTextView: TextView = findViewById(R.id.raTextView)
            raTextView.text = ra

            val qrCodeMessage = submissionData.qrCodeMessage
            val qrCodeBitmap = generateQRCodeBitmap(qrCodeMessage)
            val qrCodeImageView: ImageView = findViewById(R.id.qrCodeImageView)
            qrCodeImageView.setImageBitmap(qrCodeBitmap)

        }



// Exibir os dados na segunda tela
        val qrCodeImageView = findViewById<ImageView>(R.id.qrCodeImageView)
        val photoImageView = findViewById<ImageView>(R.id.photoImageView)
        val raTextView = findViewById<TextView>(R.id.raTextView)
        val locationTextView = findViewById<TextView>(R.id.locationTextView)

// Exibir a imagem do QR Code lido
       // qrCodeImageView.setImageBitmap(submissionData?.qrCodeBitmap)

// Exibir a foto tirada pelo usuário
        photoImageView.setImageBitmap(submissionData?.photoBitmap)

// Exibir o RA digitado
        //raTextView.text = submissionData?.ra

// Exibir a localização do aluno
        val location = submissionData?.location
        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0
        locationTextView.text = "Latitude: $latitude\nLongitude: $longitude"


        val qrCode = "QR Code lido" // Substitua pelo QR Code lido
        val fotoTirada = "Caminho da foto" // Substitua pelo caminho da foto tirada
        val ra = intent.getStringExtra("ra")
        //val latitude = intent.getDoubleExtra("latitude", 0.0)
        //val longitude = intent.getDoubleExtra("longitude", 0.0)

        qrCodeImageView.setImageURI(Uri.parse(qrCode)) // Exibe o QR Code
        //fotoImageView.setImageURI(Uri.parse(fotoTirada)) // Exibe a foto tirada
        //raTextView.text = ra // Exibe o RA digitado
       // localizacaoTextView.text = "Latitude: $latitude, Longitude: $longitude" // Exibe a localização
    }
}
