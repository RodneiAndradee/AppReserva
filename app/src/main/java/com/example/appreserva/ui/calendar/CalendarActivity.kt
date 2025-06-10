package com.example.appreserva.ui.calendar

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem				
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.appreserva.R
import com.example.appreserva.ui.booking.BookingActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

import com.example.appreserva.data.repository.AuthRepository
import com.example.appreserva.ui.login.LoginActivity
import com.example.appreserva.ui.admin.ApprovedRejectedBookingsActivity			
class CalendarActivity : AppCompatActivity() {

    private var dataSelecionada: String = ""
    private lateinit var layoutReservas: LinearLayout
    private val firestore = FirebaseFirestore.getInstance()

	private lateinit var icMenu: ImageView
    private val novaReservaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            atualizarReservas()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val fabEdit = findViewById<FloatingActionButton>(R.id.fabEdit)
        layoutReservas = findViewById(R.id.layoutReservas)

		icMenu = findViewById(R.id.icMenu) // Inicializar icMenu

        setupMenuButton() // Chamar a função para configurar o botão de menu
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.JULY)
        calendar.set(Calendar.DAY_OF_MONTH, 22)
        calendarView.date = calendar.timeInMillis

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dataSelecionada = formatter.format(calendar.time)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            dataSelecionada = formatter.format(cal.time)
        }

        fabEdit.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            intent.putExtra("dataSelecionada", dataSelecionada)
            novaReservaLauncher.launch(intent)
        }

        atualizarReservas()
    }

	private fun setupMenuButton() {
        icMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            // Infla o menu_admin.xml
            popupMenu.menuInflater.inflate(R.menu.menu_admin, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_profile -> { // ID do item "Perfil" no menu_admin.xml
                        Toast.makeText(this, "Perfil clicado!", Toast.LENGTH_SHORT).show()
                        // Adicione a Intent para a Activity de Perfil aqui
                        true
                    }
                    R.id.action_settings -> { // ID do item "Configurações" no menu_admin.xml
                        Toast.makeText(this, "Configurações clicado!", Toast.LENGTH_SHORT).show()
                        // Adicione a Intent para a Activity de Configurações aqui
                        true
                    }
                    R.id.action_reservas -> { // ID do item "Reservas" no menu_admin.xml
                        val intent = Intent(this, ApprovedRejectedBookingsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_logout -> { // ID do item "Sair do APP" no menu_admin.xml
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
    private fun atualizarReservas() {
        layoutReservas.removeAllViews()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        firestore.collection("reservations")
            .whereEqualTo("userId", userEmail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.getString("date") ?: "Data não encontrada"
                    val hora = document.getString("time") ?: ""
                    val material = document.getString("material") ?: ""
                    val anotacoes = document.getString("notes") ?: ""

                    val dataFormatada = try {
                        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val parsedDate = originalFormat.parse(data)
                        targetFormat.format(parsedDate!!)
                    } catch (e: Exception) {
                        data
                    }

                    val reservaId = document.id

                    val cardView = androidx.cardview.widget.CardView(this).apply {
                        radius = 12f
                        cardElevation = 6f
                        setContentPadding(24, 24, 24, 24)
                        useCompatPadding = true
                        val layout = LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            setPadding(8, 8, 8, 8)
                            setBackgroundColor(Color.WHITE)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        val icon = ImageView(context).apply {
                            setImageResource(R.drawable.ic_calendar)
                            setColorFilter(Color.parseColor("#FF9843"))
                            val size = resources.getDimensionPixelSize(R.dimen.icon_size)
                            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                                setMargins(0, 0, 24, 0)
                            }
                        }

                        val text = TextView(context).apply {
                            text = "Reserva $dataFormatada"
                            setTextColor(Color.BLACK)
                            textSize = 16f
                            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            setOnClickListener {
                                mostrarResumo(dataFormatada, hora, material, anotacoes)
                            }
                        }

                        val deleteBtn = ImageButton(context).apply {
                            setImageResource(R.drawable.ic_delete)
                            setBackgroundColor(Color.TRANSPARENT)
                            setColorFilter(Color.RED)
                            setOnClickListener {
                                confirmarExclusao(reservaId)
                            }
                        }

                        layout.addView(icon)
                        layout.addView(text)
                        layout.addView(deleteBtn)
                        addView(layout)
                    }

                    layoutReservas.addView(cardView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar reservas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmarExclusao(reservaId: String) {
        AlertDialog.Builder(this)
            .setTitle("Excluir reserva")
            .setMessage("Deseja realmente excluir essa reserva?")
            .setPositiveButton("Sim") { _, _ ->
                firestore.collection("reservations").document(reservaId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Reserva excluída.", Toast.LENGTH_SHORT).show()
                        atualizarReservas()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao excluir reserva.", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarResumo(data: String, hora: String, material: String, anotacoes: String) {
        val resumo = "Data: $data\nHorário: $hora\nMaterial: $material\nAnotações: $anotacoes"
        AlertDialog.Builder(this)
            .setTitle("Resumo da Reserva")
            .setMessage(resumo)
            .setPositiveButton("OK", null)
            .show()
    }
}






