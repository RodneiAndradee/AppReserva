package com.example.appreserva.ui.success

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appreserva.R

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sucess_booking)

        val okButton = findViewById<Button>(R.id.okButton)
        val resumoTextView = findViewById<TextView>(R.id.tvResumoReserva)

        // Recupera dados passados pela BookingActivity (via Intent)
        val sala = intent.getStringExtra("reservaSala") ?: "Sala não informada"
        val data = intent.getStringExtra("reservaData") ?: "Data não informada"
        val hora = intent.getStringExtra("reservaHora") ?: "Horário não informado"
        val professor = intent.getStringExtra("reservaProfessor") ?: "Professor não informado"
        val anotacoes = intent.getStringExtra("reservaAnotacoes") ?: ""

        val textoResumo = """
            ✔️ Reserva Confirmada!

            🏫 Sala: $sala
            📅 Data: $data
            🕒 Horário: $hora
            👤 Professor: $professor
            📝 Anotações: ${if (anotacoes.isBlank()) "Nenhuma" else anotacoes}
        """.trimIndent()

        resumoTextView.text = textoResumo

        okButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}

