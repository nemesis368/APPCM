package com.miraflores.agenda.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miraflores.agenda.R
import com.miraflores.agenda.model.Appointment

class AppointmentsAdapter(
    private val appointments: List<Appointment>,
    private val onEditClick: (Appointment) -> Unit,
    private val onDeleteClick: (Appointment) -> Unit,
    private val onConfirmClick: (Appointment) -> Unit, // Nuevo
    private val onCancelClick: (Appointment) -> Unit   // Nuevo
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvService: TextView = view.findViewById(R.id.tvService)
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val ivStatusIcon: ImageView = view.findViewById(R.id.ivStatusIcon)

        // Los 4 botones
        val btnEdit: ImageView = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
        val btnConfirm: ImageView = view.findViewById(R.id.btnConfirm)
        val btnCancel: ImageView = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        // 1. Datos básicos
        holder.tvService.text = appointment.service
        holder.tvClientName.text = appointment.clientName
        holder.tvDate.text = appointment.date
        holder.tvTime.text = appointment.time

        // 2. Manejo de Notas/Detalles
        // Asumiendo que tu modelo tiene "notes". Si se llama "details", cambia .notes por .details
        if (appointment.notes.isNotEmpty()) {
            holder.tvDetails.text = "Nota: ${appointment.notes}"
            holder.tvDetails.visibility = View.VISIBLE
        } else {
            holder.tvDetails.visibility = View.GONE
        }

        // 3. Colores del Estado
        when (appointment.status) {
            "CONFIRMADO" -> {
                holder.ivStatusIcon.setImageResource(android.R.drawable.checkbox_on_background)
                holder.ivStatusIcon.setColorFilter(Color.parseColor("#2E7D32")) // Verde
            }
            "CANCELADO", "RECHAZADO" -> {
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_delete)
                holder.ivStatusIcon.setColorFilter(Color.parseColor("#B71C1C")) // Rojo
            }
            else -> { // PENDIENTE
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_help)
                holder.ivStatusIcon.setColorFilter(Color.parseColor("#FFA000")) // Naranja
            }
        }

        // 4. Asignar Clics a los 4 botones
        holder.btnEdit.setOnClickListener { onEditClick(appointment) }
        holder.btnDelete.setOnClickListener { onDeleteClick(appointment) }
        holder.btnConfirm.setOnClickListener { onConfirmClick(appointment) }
        holder.btnCancel.setOnClickListener { onCancelClick(appointment) }
    }

    override fun getItemCount() = appointments.size
}
