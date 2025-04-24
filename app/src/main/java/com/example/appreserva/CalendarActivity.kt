package com.example.reservaplus

import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import com.example.appreserva.R
import java.util.*

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        // Configurar o calendário para julho com o dia 22 selecionado
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.JULY)
        calendar.set(Calendar.DAY_OF_MONTH, 22)
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR)) // Ano atual

        // Definir a data selecionada
        calendarView.date = calendar.timeInMillis

        // Listener para mudanças de data
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Aqui você pode salvar a data selecionada ou realizar ações com base nela
            // Por exemplo, atualizar a seção de solicitação com base na data
        }
    }
}