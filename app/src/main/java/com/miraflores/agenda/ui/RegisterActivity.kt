package com.miraflores.agenda.ui

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.miraflores.agenda.databinding.ActivityRegistroBinding
import com.miraflores.agenda.utils.ColorHelper
import com.miraflores.agenda.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ColorHelper.applyTheme(this, binding.root)
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        ColorHelper.applyTheme(this, binding.root)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.tvIrALogin.setOnClickListener { finish() }

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombreReg.text.toString().trim()
            val email = binding.etCorreoReg.text.toString().trim()
            val password = binding.etPassReg.text.toString().trim()

            if (nombre.isEmpty()) {
                binding.etNombreReg.error = "Falta nombre"
                return@setOnClickListener
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etCorreoReg.error = "Email incorrecto"
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.etPassReg.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            binding.btnRegistrar.isEnabled = false
            Toast.makeText(this, "Registrando...", Toast.LENGTH_SHORT).show()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    binding.btnRegistrar.isEnabled = true
                    if (task.isSuccessful) {
                        SessionManager.saveSession(this, email, nombre)
                        Toast.makeText(this, "¡Bienvenido $nombre!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
