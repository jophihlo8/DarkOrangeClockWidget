package com.example.clockwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * Launched by the system as this widget's APPWIDGET_CONFIGURE activity
 * (see widget_24h_clock_info.xml) whenever a widget is added, and again
 * whenever the user taps an existing widget to change its photo. Either
 * way it always receives a real EXTRA_APPWIDGET_ID from the caller.
 */
class PhotoConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            saveImageAndUpdateWidget(uri)
        } else {
            // User backed out of the picker without choosing anything.
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        pickImageLauncher.launch("image/*")
    }

    private fun saveImageAndUpdateWidget(sourceUri: Uri) {
        val saved = PhotoManager.savePhoto(this, appWidgetId, sourceUri)

        if (!saved) {
            Toast.makeText(this, getString(R.string.photo_load_failed), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(this)
        DarkOrangeClockWidget.updateAppWidget(this, appWidgetManager, appWidgetId)

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
