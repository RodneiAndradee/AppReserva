package com.example.appreserva.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appreserva.R
import com.example.appreserva.data.repository.ReservationRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class ApprovedRejectedBookingsActivity : AppCompatActivity() {

    private val viewModel: ApprovedRejectedBookingsViewModel by viewModels {
        ApprovedRejectedBookingsViewModelFactory(ReservationRepository(FirebaseFirestore.getInstance()))
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservationAdapter: ReservationAdapter // Reutiliza o mesmo adapter
    private lateinit var tvNoOtherReservations: TextView // TextView para "Nenhuma reserva para exibir"
    private lateinit var progressBarLoadingAll: ProgressBar // ProgressBar para esta tela
    private lateinit var btnBack: ImageButton // Botão de voltar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_approved_rejected_bookings)

        // Inicializa as Views
        recyclerView = findViewById(R.id.recycler_view_all_reservations)
        tvNoOtherReservations = findViewById(R.id.tv_no_other_reservations)
        progressBarLoadingAll = findViewById(R.id.progress_bar_loading_all)
        btnBack = findViewById(R.id.btn_back_from_reservas)

        setupRecyclerView()
        observeViewModel()
        setupBackButton() // Configura o botão de voltar
    }

    private fun setupRecyclerView() {
        // Passamos null para os listeners de clique, pois não haverá aprovação/rejeição aqui
        reservationAdapter = ReservationAdapter(emptyList(), null, null)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reservationAdapter
    }

    private fun observeViewModel() {
        viewModel.allReservations.observe(this) { reservations ->
            reservationAdapter.updateReservations(reservations)
            tvNoOtherReservations.visibility = if (reservations.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (reservations.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.errorMessage.observe(this) { message ->
            Snackbar.make(findViewById(android.R.id.content), "Erro: $message", Snackbar.LENGTH_LONG).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBarLoadingAll.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Volta para a Activity anterior
        }
    }
}