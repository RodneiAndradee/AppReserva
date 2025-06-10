package com.example.appreserva.ui.admin

import android.graphics.Color // Importar Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appreserva.R
import com.example.appreserva.data.model.Reservation

class ReservationAdapter(
    private var reservations: List<Reservation>,
    private val onApproveClick: ((Reservation) -> Unit)? = null, // Tornar anulável
    private val onRejectClick: ((Reservation) -> Unit)? = null // Tornar anulável
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    fun updateReservations(newReservations: List<Reservation>) {
        reservations = newReservations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_request, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation)
    }

    override fun getItemCount(): Int = reservations.size

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRoomDate: TextView = itemView.findViewById(R.id.tv_request_room_date)
        private val tvProfessorTime: TextView = itemView.findViewById(R.id.tv_request_professor_time)
        private val tvMaterial: TextView = itemView.findViewById(R.id.tv_request_material)
        private val tvNotes: TextView = itemView.findViewById(R.id.tv_request_notes)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_request_status) // NOVO: TextView para o status
        private val btnApprove: Button = itemView.findViewById(R.id.btn_approve)
        private val btnReject: Button = itemView.findViewById(R.id.btn_reject)

        fun bind(reservation: Reservation) {
            tvRoomDate.text = "Sala ${reservation.roomId} - ${reservation.date}"
            tvProfessorTime.text = "Prof. ${reservation.professor} - ${reservation.time}"
            tvMaterial.text = "Material: ${reservation.material}"
            tvNotes.text = "Anotações: ${reservation.notes}"

            // NOVO: Exibe o status e define a cor
            tvStatus.text = "Status: ${reservation.status.capitalize()}"
            when (reservation.status) {
                "approved" -> tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Verde
                "rejected" -> tvStatus.setTextColor(Color.parseColor("#FF4444")) // Vermelho
                "pending" -> tvStatus.setTextColor(Color.parseColor("#FF9B55")) // Laranja
                else -> tvStatus.setTextColor(Color.BLACK)
            }
            tvStatus.visibility = View.VISIBLE // Garante que o status esteja visível

            // Condicionalmente exibe os botões de aprovação/rejeição
            if (onApproveClick != null && onRejectClick != null && reservation.status == "pending") {
                btnApprove.visibility = View.VISIBLE
                btnReject.visibility = View.VISIBLE
                btnApprove.setOnClickListener {
                    onApproveClick.invoke(reservation)
                }
                btnReject.setOnClickListener {
                    onRejectClick.invoke(reservation)
                }
            } else {
                btnApprove.visibility = View.GONE
                btnReject.visibility = View.GONE
            }
        }
    }
}