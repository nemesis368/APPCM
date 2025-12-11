package com.miraflores.agenda.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.miraflores.agenda.R
import com.miraflores.agenda.databinding.ActivityLoginBinding
import com.miraflores.agenda.utils.ColorHelper
import com.miraflores.agenda.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isAdminMode = false
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar sesión activa antes de cargar la vista
        if (SessionManager.isLoggedIn(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplicar tema
        ColorHelper.applyTheme(this, binding.rootLayout)
        auth = FirebaseAuth.getInstance()

        // Cargar GIF del logo
        try {
            Glide.with(this)
                .asGif()
                .load(R.drawable.loginn)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.imgLogo)
        } catch (e: Exception) {}

        setupListeners()
        updateUIMode()
    }

    override fun onResume() {
        super.onResume()
        ColorHelper.applyTheme(this, binding.rootLayout)
    }

    private fun setupListeners() {
        binding.tvTabClient.setOnClickListener { isAdminMode = false; updateUIMode() }
        binding.tvTabAdmin.setOnClickListener { isAdminMode = true; updateUIMode() }

        binding.btnShowPassword.setOnClickListener {
            passwordVisible = !passwordVisible
            binding.etPassword.inputType = if(passwordVisible)
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            // Mantener el cursor al final del texto
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnLogin.setOnClickListener {
            if (isAdminMode) validateAdmin() else validateClient()
        }

        binding.tvRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        binding.tvForgotPassword.setOnClickListener { showRecoveryDialog() }
    }

    private fun showRecoveryDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setText("oscar_vm1@tesch.edu.mx") // Correo por defecto sugerido
        }
        AlertDialog.Builder(this)
            .setTitle("Recuperar Cuenta")
            .setMessage("Confirmar envío de enlace a:")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val email = input.text.toString().trim()
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    sendRecoveryEmail(email)
                } else {
                    Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun sendRecoveryEmail(email: String) {
        Toast.makeText(this, "Enviando...", Toast.LENGTH_SHORT).show()
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                AlertDialog.Builder(this)
                    .setTitle("¡Enviado!")
                    .setMessage("Revisa tu correo: $email")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateAdmin() {
        val u = binding.etEmail.text.toString().trim()
        val p = binding.etPassword.text.toString().trim()

        // Validación simple para admin (puedes cambiar esto por base de datos si prefieres)
        if (u == "admin" && p == "123") {
            SessionManager.saveSession(this, "admin@sistema.com", "Administrador")
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateClient() {
        val e = binding.etEmail.text.toString().trim()
        val p = binding.etPassword.text.toString().trim()

        if (e.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(e, p).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Guardar sesión y navegar
                val userName = e.substringBefore("@") // Usar parte del correo como nombre temporal
                SessionManager.saveSession(this, e, userName)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- AQUÍ ESTÁ LA SOLUCIÓN DEL ERROR DE ESPACIOS ---
    private fun updateUIMode() {
        val wine = ContextCompat.getColor(this, R.color.wine_btn)
        val gray = ContextCompat.getColor(this, R.color.gray_light)

        // Obtenemos los parámetros del botón para modificar su margen dinámicamente
        val params = binding.btnLogin.layoutParams as LinearLayout.LayoutParams

        if (isAdminMode) {
            // --- MODO ADMIN ---
            binding.tvTabAdmin.setTextColor(wine)
            binding.tvTabAdmin.setBackgroundColor(Color.WHITE)
            binding.tvTabClient.setTextColor(gray)
            binding.tvTabClient.setBackgroundColor(Color.TRANSPARENT)

            binding.etEmail.hint = "Usuario Admin"

            // Ocultamos las opciones de cliente
            binding.tvRegister.visibility = View.GONE
            binding.tvForgotPassword.visibility = View.GONE

            // TRUCO: Como ocultamos el texto de arriba, agregamos margen al botón
            // para que no se pegue a la contraseña. (30dp convertido a pixeles)
            val marginPx = (30 * resources.displayMetrics.density).toInt()
            params.topMargin = marginPx

        } else {
            // --- MODO CLIENTE ---
            binding.tvTabClient.setTextColor(wine)
            binding.tvTabClient.setBackgroundColor(Color.WHITE)
            binding.tvTabAdmin.setTextColor(gray)
            binding.tvTabAdmin.setBackgroundColor(Color.TRANSPARENT)

            binding.etEmail.hint = "Correo Electrónico"

            // Mostramos las opciones de cliente
            binding.tvRegister.visibility = View.VISIBLE
            binding.tvForgotPassword.visibility = View.VISIBLE

            // Quitamos el margen extra del botón, ya que el texto de
            // "Olvidaste contraseña" ya tiene su propio margen inferior.
            params.topMargin = 0
        }

        // Aplicar los cambios de margen al botón
        binding.btnLogin.layoutParams = params
    }
}
