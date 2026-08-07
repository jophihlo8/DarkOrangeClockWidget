package com.example.clockwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Saves, loads and removes the background photo for each widget instance.
 * Every appWidgetId gets its own file (widget_bg_<id>.png) so multiple
 * widgets on the home screen can each show a different photo. This is the
 * single source of truth for photo storage - both PhotoConfigActivity and
 * the widget provider go through here.
 */
object PhotoManager {

    private fun photoFile(context: Context, appWidgetId: Int): File =
        File(context.filesDir, "widget_bg_$appWidgetId.png")

    fun savePhoto(context: Context, appWidgetId: Int, uri: Uri): Boolean {
        return try {
            val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)
            } ?: return false

            FileOutputStream(photoFile(context, appWidgetId)).use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadPhoto(context: Context, appWidgetId: Int): Bitmap? {
        val file = photoFile(context, appWidgetId)
        if (!file.exists()) return null

        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }
    }

    fun deletePhoto(context: Context, appWidgetId: Int) {
        val file = photoFile(context, appWidgetId)
        if (file.exists()) file.delete()
    }

    fun hasPhoto(context: Context, appWidgetId: Int): Boolean =
        photoFile(context, appWidgetId).exists()
}
