package com.example.appreserva.ui.professor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appreserva.R
import com.example.appreserva.ui.calendar.CalendarActivity

class ProfessorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professor)

        val btnAbrirCalendario = findViewById<ImageView>(R.id.btnCalendario)

        btnAbrirCalendario.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }
}
