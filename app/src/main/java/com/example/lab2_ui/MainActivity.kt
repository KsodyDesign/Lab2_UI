package com.example.lab2_ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var cbAgree: CheckBox
    private lateinit var btnSubmit: Button

    private lateinit var etMessage: EditText
    private lateinit var btnGo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация элементов формы (Задание 1)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        cbAgree = findViewById(R.id.cbAgree)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Инициализация элементов навигации (Задание 2)
        etMessage = findViewById(R.id.etMessage)
        btnGo = findViewById(R.id.btnGo)

        // Обработка нажатия кнопки формы
        btnSubmit.setOnClickListener {
            val name = etName.text.toString().trim()
            val ageText = etAge.text.toString().trim()
            val isAgreed = cbAgree.isChecked

            // Валидация полей
            if (name.isEmpty()) {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.isEmpty()) {
                Toast.makeText(this, "Введите возраст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age < 0 || age > 150) {
                Toast.makeText(this, "Некорректный возраст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isAgreed) {
                Toast.makeText(this, "Примите правила", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultMessage = "Имя: $name, Возраст: $age, Правила: ${if (isAgreed) "Да" else "Нет"}"
            Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show()
        }

        // Переход на второй экран с передачей данных
        btnGo.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Введите текст сообщения", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("EXTRA_MESSAGE", text)
            startActivity(intent)
        }
    }
}
