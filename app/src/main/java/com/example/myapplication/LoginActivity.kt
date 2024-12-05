package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etLoginCpf: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegister: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etLoginCpf = findViewById(R.id.etLoginCpf)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoToRegister = findViewById(R.id.btnGoToRegister) // Referência ao botão "Cadastrar"
        dbHelper = DatabaseHelper(this)

        // Lógica de login
        btnLogin.setOnClickListener {
            val cpf = etLoginCpf.text.toString()
            val password = etLoginPassword.text.toString()

            if (cpf.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = dbHelper.authenticateUser(cpf, password)
            if (user != null) {
                if (user.userType == "admin") {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                } else {
                    startActivity(Intent(this, UserDashboardActivity::class.java))
                }
            } else {
                Toast.makeText(this, "CPF ou senha incorretos.", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirecionar para a tela de cadastro
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
