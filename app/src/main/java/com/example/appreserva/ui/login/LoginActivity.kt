package com.example.appreserva.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.appreserva.R
import com.example.appreserva.data.repository.AuthRepository
import com.example.appreserva.ui.admin.AdminActivity
import com.example.appreserva.ui.calendar.CalendarActivity

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository())
    }

    private lateinit var spinnerPerfil: Spinner
    private var perfilSelecionado: String = "Administrador"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        spinnerPerfil = findViewById(R.id.spinnerPerfil)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        val perfis = listOf("Administrador", "Professor")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, perfis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPerfil.adapter = adapter

        spinnerPerfil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                perfilSelecionado = perfis[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                perfilSelecionado = "Administrador"
            }
        }

        btnEntrar.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()

            loginViewModel.login(email, senha)
        }

        loginViewModel.loginResult.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                val nextScreen = if (perfilSelecionado == "Administrador") {
                    AdminActivity::class.java
                } else {
                    CalendarActivity::class.java
                }
                startActivity(Intent(this, nextScreen))
                finish()
            }
        })

        loginViewModel.errorMessage.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, "Erro no login: $it", Toast.LENGTH_LONG).show()
            }
        })
    }
}



