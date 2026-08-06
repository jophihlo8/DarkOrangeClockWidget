package com.example.clockwidget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val changePhoto = findViewById<MaterialButton>(R.id.btnChangePhoto)

        changePhoto.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PhotoConfigActivity::class.java
                )
            )

        }

    }

}
