package com.example.appreserva.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appreserva.R
import com.example.appreserva.data.repository.AuthRepository
import com.example.appreserva.data.model.Reservation
import com.example.appreserva.data.repository.ReservationRepository
import com.example.appreserva.ui.booking.BookingActivity
import com.example.appreserva.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.appreserva.ui.admin.AdminViewModelFactory

class AdminActivity : AppCompatActivity() {

    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(ReservationRepository(FirebaseFirestore.getInstance()))
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var tvNoRequests: TextView
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var btnMenuAdmin: ImageButton
    private lateinit var btnCriarReserva: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recyclerView = findViewById(R.id.recycler_view_requests)
        tvNoRequests = findViewById(R.id.tv_no_requests)
        progressBarLoading = findViewById(R.id.progress_bar_loading)
        btnMenuAdmin = findViewById(R.id.btn_menu_admin)
        btnCriarReserva = findViewById(R.id.btn_criar_reserva)

        setupRecyclerView()
        observeViewModel()
        setupMenuButton()
        setupCriarReservaButton()
    }

    private fun setupRecyclerView() {
        reservationAdapter = ReservationAdapter(
            emptyList(),
            onApproveClick = { reservation ->
                adminViewModel.approveReservation(reservation.id)
            },
            onRejectClick = { reservation ->
                adminViewModel.rejectReservation(reservation.id)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = reservationAdapter
    }

    private fun observeViewModel() {
        adminViewModel.pendingReservations.observe(this) { reservations ->
            reservationAdapter.updateReservations(reservations)
            tvNoRequests.visibility = if (reservations.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (reservations.isEmpty()) View.GONE else View.VISIBLE
        }

        adminViewModel.actionResult.observe(this) { (success, message) ->
            // Exibe o Snackbar (popup na parte inferior da tela)
            val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
            if (!success) {
                snackbar.duration = Snackbar.LENGTH_LONG
            }
            snackbar.show()
        }

        adminViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoadingIndicator() else hideLoadingIndicator()
        }
    }

    private fun hideLoadingIndicator() {
        progressBarLoading.visibility = View.GONE
    }

    private fun showLoadingIndicator() {
        progressBarLoading.visibility = View.VISIBLE
    }

    private fun setupMenuButton() {
        btnMenuAdmin.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.menu_admin, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_profile -> {
                        Snackbar.make(view, "Perfil clicado!", Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_settings -> {
                        Snackbar.make(view, "Configurações clicado!", Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_reservas -> { // Lógica para "Reservas"
                        val intent = Intent(this, ApprovedRejectedBookingsActivity::class.java) // CRIA A INTENT
                        startActivity(intent) // INICIA A NOVA ACTIVITY
                        true // Retorna true para indicar que o item foi tratado
                    }
                    R.id.action_logout -> {
                        AuthRepository(FirebaseAuth.getInstance()).logout()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
    private fun setupCriarReservaButton() {
        btnCriarReserva.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }
    }
}