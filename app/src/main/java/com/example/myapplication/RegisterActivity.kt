package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etCpf: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var rgUserType: RadioGroup
    private lateinit var rbAdmin: RadioButton
    private lateinit var rbUser: RadioButton
    private lateinit var btnRegister: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialização dos campos
        etCpf = findViewById(R.id.etCpf)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        rgUserType = findViewById(R.id.rgUserType)
        rbAdmin = findViewById(R.id.rbAdmin)
        rbUser = findViewById(R.id.rbUser)
        btnRegister = findViewById(R.id.btnRegister)
        dbHelper = DatabaseHelper(this)

        // Lógica de cadastro
        btnRegister.setOnClickListener {
            val cpf = etCpf.text.toString()
            val name = etName.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (cpf.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos os campos são obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userType = if (rbAdmin.isChecked) "admin" else "user"
            val result = dbHelper.addUser(cpf, name, phone, email, password, userType)

            if (result != -1L) {
                val userMessage = if (userType == "admin") "Administrador cadastrado com sucesso!" else "Usuário final cadastrado com sucesso!"
                Toast.makeText(this, userMessage, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao cadastrar usuário.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
