package com.example.clockwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.Calendar

/**
 * Launcher screen: shows a live preview of the clock face and, on
 * Android 8+, lets the user pin the widget straight to the home screen
 * via AppWidgetManager.requestPinAppWidget instead of hunting through
 * the widget picker.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preview = findViewById<ImageView>(R.id.imgPreview)
        preview.setImageBitmap(
            ClockRenderer.renderClockBitmap(
                context = this,
                size = 480,
                appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID,
                calendar = Calendar.getInstance()
            )
        )

        findViewById<MaterialButton>(R.id.btnAddWidget).setOnClickListener {
            requestPinWidget()
        }
    }

    private fun requestPinWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, DarkOrangeClockWidget::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            appWidgetManager.isRequestPinAppWidgetSupported
        ) {
            appWidgetManager.requestPinAppWidget(provider, null, null)
        } else {
            Toast.makeText(this, getString(R.string.pin_widget_unsupported), Toast.LENGTH_LONG).show()
        }
    }
}
