package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var etActivityName: EditText
    private lateinit var etActivityDescription: EditText
    private lateinit var etActivitySchedule: EditText
    private lateinit var etActivityLocation: EditText
    private lateinit var btnAddActivity: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Inicialização dos campos
        etActivityName = findViewById(R.id.etActivityName)
        etActivityDescription = findViewById(R.id.etActivityDescription)
        etActivitySchedule = findViewById(R.id.etActivitySchedule)
        etActivityLocation = findViewById(R.id.etActivityLocation)
        btnAddActivity = findViewById(R.id.btnAddActivity)
        dbHelper = DatabaseHelper(this)

        // Lógica para adicionar uma nova atividade
        btnAddActivity.setOnClickListener {
            val name = etActivityName.text.toString()
            val description = etActivityDescription.text.toString()
            val schedule = etActivitySchedule.text.toString()
            val location = etActivityLocation.text.toString()

            if (name.isEmpty() || description.isEmpty() || schedule.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Todos os campos são obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.addActivity(name, description, schedule, location)
            if (result != -1L) {
                Toast.makeText(this, "Atividade cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                etActivityName.text.clear()
                etActivityDescription.text.clear()
                etActivitySchedule.text.clear()
                etActivityLocation.text.clear()
            } else {
                Toast.makeText(this, "Erro ao cadastrar atividade.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
