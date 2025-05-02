package com.example.appreserva.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.appreserva.R
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository
import com.example.appreserva.ui.calendar.CalendarActivity
import com.example.appreserva.ui.success.SuccessActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingActivity : AppCompatActivity() {

    private val bookingViewModel: BookingViewModel by viewModels {
        BookingViewModelFactory(ReservationRepository(FirebaseFirestore.getInstance()))
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val spinnerSala = findViewById<Spinner>(R.id.spinnerSala)
        val etHoraInicio = findViewById<EditText>(R.id.etHoraInicio)
        val etMinutoInicio = findViewById<EditText>(R.id.etMinutoInicio)
        val etHoraFim = findViewById<EditText>(R.id.etHoraFim)
        val etMinutoFim = findViewById<EditText>(R.id.etMinutoFim)
        val etMaterial = findViewById<EditText>(R.id.etMaterial)
        val etProfessor = findViewById<EditText>(R.id.etProfessor)
        val etAnotacoes = findViewById<EditText>(R.id.etAnotacoes)
        val buttonReserve = findViewById<Button>(R.id.buttonReserve)

        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        etProfessor.setText(userEmail)
        etProfessor.isEnabled = false
        etProfessor.isFocusable = false

        val dataSelecionada = intent.getStringExtra("dataSelecionada") ?: "Data não recebida"
        val tvTituloData = findViewById<TextView>(R.id.tvTituloData)

        val dataFormatada = try {
            val formatoOriginal = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val formatoDesejado = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val data = formatoOriginal.parse(dataSelecionada)
            formatoDesejado.format(data)
        } catch (e: Exception) {
            dataSelecionada
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvTituloData.text = "Dia $dataFormatada"

        val salas = listOf("Sala 101", "Sala 102", "Sala 103", "Sala 104", "Sala 105", "Sala 201", "Sala 202")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, salas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSala.adapter = adapter

        buttonReserve.setOnClickListener {
            val salaSelecionada = spinnerSala.selectedItem.toString()
            val horaInicio = etHoraInicio.text.toString()
            val minutoInicio = etMinutoInicio.text.toString()
            val horaFim = etHoraFim.text.toString()
            val minutoFim = etMinutoFim.text.toString()
            val material = etMaterial.text.toString()
            val professor = etProfessor.text.toString()
            val anotacoes = etAnotacoes.text.toString()

            val inicioEmMinutos = horaInicio.toIntOrNull()?.times(60)?.plus(minutoInicio.toIntOrNull() ?: -1) ?: -1
            val fimEmMinutos = horaFim.toIntOrNull()?.times(60)?.plus(minutoFim.toIntOrNull() ?: -1) ?: -1

            if (inicioEmMinutos < 0 || fimEmMinutos < 0) {
                Toast.makeText(this, "Horários inválidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fimEmMinutos <= inicioEmMinutos) {
                Toast.makeText(this, "Horário final deve ser maior que o inicial", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val horaCompleta = "$horaInicio:$minutoInicio - $horaFim:$minutoFim"

            // 🔒 Verifica duplicidade antes de criar
            FirebaseFirestore.getInstance().collection("reservations")
                .whereEqualTo("roomId", salaSelecionada)
                .whereEqualTo("date", dataSelecionada)
                .whereEqualTo("time", horaCompleta)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        Toast.makeText(this, "Essa sala já está reservada para esse horário.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    val reserva = Reservation(
                        userId = userEmail,
                        roomId = salaSelecionada,
                        date = dataSelecionada,
                        time = horaCompleta,
                        material = material,
                        notes = anotacoes,
                        professor = professor
                    )

                    bookingViewModel.bookReservation(reserva)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao verificar disponibilidade", Toast.LENGTH_SHORT).show()
                }
        }

        bookingViewModel.bookingResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Reserva salva, indo para tela final...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SuccessActivity::class.java)
                intent.putExtra("reservaData", dataSelecionada)
                startActivity(intent)
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Falha ao salvar a reserva.", Toast.LENGTH_SHORT).show()
            }
        }

        bookingViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}



