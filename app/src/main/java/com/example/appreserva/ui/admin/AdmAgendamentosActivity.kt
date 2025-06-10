package com.example.appreserva.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appreserva.R
import com.example.appreserva.data.repository.ReservationRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class AdmAgendamentosActivity : AppCompatActivity() {

    // Instancia o AdminViewModel usando by viewModels e o AdminViewModelFactory
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(ReservationRepository(FirebaseFirestore.getInstance()))
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var tvNoRequests: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin) // Define o layout para activity_admin.xml

        // Inicializa as Views
        recyclerView = findViewById(R.id.recycler_view_requests) // ID do RecyclerView em activity_admin.xml
        tvNoRequests = findViewById(R.id.tv_no_requests) // ID da TextView em activity_admin.xml

        setupRecyclerView()
        observeViewModel()

    }

    private fun setupRecyclerView() {
        reservationAdapter = ReservationAdapter(
            emptyList(),
            onApproveClick = { reservation ->
                // Chama o método do ViewModel para aprovar a reserva
                adminViewModel.approveReservation(reservation.id)
            },
            onRejectClick = { reservation ->
                // Chama o método do ViewModel para rejeitar a reserva
                adminViewModel.rejectReservation(reservation.id)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reservationAdapter
    }

    private fun observeViewModel() {
        // Observa as reservas pendentes do ViewModel
        adminViewModel.pendingReservations.observe(this) { reservations ->
            reservationAdapter.updateReservations(reservations)
            // Mostra ou esconde a mensagem "Nenhuma solicitação pendente"
            tvNoRequests.visibility = if (reservations.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (reservations.isEmpty()) View.GONE else View.VISIBLE
        }

        // Observa o resultado das ações (aprovar/rejeitar) do ViewModel
        adminViewModel.actionResult.observe(this) { (success, message) ->
            // Exibe o Snackbar (popup na parte inferior da tela)
            val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
            if (!success) {
                snackbar.duration = Snackbar.LENGTH_LONG
            }
            snackbar.show()
        }

        // Observa o estado de carregamento
        adminViewModel.isLoading.observe(this) { isLoading ->
        }
    }
}