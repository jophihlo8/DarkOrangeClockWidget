package com.example.clockwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object PhotoManager {

    private const val PHOTO_NAME = "widget_photo.png"

    fun savePhoto(
        context: Context,
        uri: Uri
    ): Boolean {

        return try {

            val input =
                context.contentResolver.openInputStream(uri)

            val bitmap =
                BitmapFactory.decodeStream(input)

            input?.close()

            val file =
                File(
                    context.filesDir,
                    PHOTO_NAME
                )

            val output =
                FileOutputStream(file)

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                output
            )

            output.flush()

            output.close()

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false

        }

    }

    fun loadPhoto(
        context: Context
    ): Bitmap? {

        return try {

            val file =
                File(
                    context.filesDir,
                    PHOTO_NAME
                )

            if (!file.exists())
                return null

            BitmapFactory.decodeFile(
                file.absolutePath
            )

        } catch (e: Exception) {

            null

        }

    }

    fun deletePhoto(
        context: Context
    ) {

        val file =
            File(
                context.filesDir,
                PHOTO_NAME
            )

        if (file.exists())
            file.delete()

    }

    fun hasPhoto(
        context: Context
    ): Boolean {

        return File(
            context.filesDir,
            PHOTO_NAME
        ).exists()

    }

}
