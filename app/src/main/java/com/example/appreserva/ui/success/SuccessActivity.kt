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

        // Recupera os dados enviados pela BookingActivity
        val sala = intent.getStringExtra("sala") ?: "Sala não informada"
        val data = intent.getStringExtra("data") ?: "Data não informada"
        val hora = intent.getStringExtra("horario") ?: "Horário não informado"
        val professor = intent.getStringExtra("professor") ?: "Professor não informado"
        val anotacoes = intent.getStringExtra("anotacoes") ?: ""

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


