package com.example.clockwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PhotoConfigActivity : AppCompatActivity() {

    private val photoPicker =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            if (uri == null) {
                finish()
                return@registerForActivityResult
            }

            val success = PhotoManager.savePhoto(
                this,
                uri
            )

            if (success) {

                updateAllWidgets()

                Toast.makeText(
                    this,
                    "Photo Updated",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Failed to Save Photo",
                    Toast.LENGTH_SHORT
                ).show()

            }

            finish()

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoPicker.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )

    }

    private fun updateAllWidgets() {

        val manager =
            AppWidgetManager.getInstance(this)

        val ids =
            manager.getAppWidgetIds(
                ComponentName(
                    this,
                    DarkOrangeClockWidget::class.java
                )
            )

        ids.forEach {

            DarkOrangeClockWidget.updateAppWidget(
                this,
                manager,
                it
            )

        }

    }

}
