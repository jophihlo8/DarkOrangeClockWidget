package com.example.clockwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class PhotoConfigActivity : AppCompatActivity() {

    private val pickPhoto =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            if (uri != null) {

                savePhoto(uri)

            } else {

                finish()

            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickPhoto.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )

    }

    private fun savePhoto(uri: Uri) {

        try {

            val input = contentResolver.openInputStream(uri)

            val bitmap = BitmapFactory.decodeStream(input)

            input?.close()

            val file = File(filesDir, "widget_photo.png")

            val out = FileOutputStream(file)

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )

            out.flush()

            out.close()

            updateWidget()

            Toast.makeText(
                this,
                "Photo Updated",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {

            e.printStackTrace()

        }

        finish()

    }

    private fun updateWidget() {

        val manager =
            AppWidgetManager.getInstance(this)

        val ids =
            manager.getAppWidgetIds(
                ComponentName(
                    this,
                    DarkOrangeClockWidget::class.java
                )
            )

        for (id in ids) {

            DarkOrangeClockWidget.updateAppWidget(
                this,
                manager,
                id
            )

        }

    }

}
