package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tv: TextView

    private var accumulator = 0.0               // resultado parcial
    private var current = StringBuilder()       // número em digitação
    private var lastOp: Char? = null            // '+', '-', '×', '÷'
    private var justEvaluated = false
    private var expression = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.tvDisplay)

        // Números
        val numberIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        numberIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onNumber((it as Button).text.toString()) }
        }

        // Operadores
        findViewById<Button>(R.id.btnAdd).setOnClickListener { onOperator('+') }
        findViewById<Button>(R.id.btnSub).setOnClickListener { onOperator('-') }
        findViewById<Button>(R.id.btnMul).setOnClickListener { onOperator('×') }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { onOperator('÷') }

        // Especiais
        findViewById<Button>(R.id.btnEq).setOnClickListener { onEquals() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnDot).setOnClickListener { addDot() }
        findViewById<Button>(R.id.btnSign).setOnClickListener { toggleSign() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { percent() }

        updateDisplay("0")
    }

    private fun onNumber(digit: String) {
        if (justEvaluated) { // começar novo número após '='
            current.clear()
            expression.clear() // para iniciar nova expressão
            justEvaluated = false
        }
        if (digit == "0" && current.toString() == "0") return
        if (current.toString() == "0") current.clear()

        current.append(digit)
        expression.append(digit) // mantém a expressão completa
        updateDisplay(expression.toString())
    }

    private fun addDot() {
        if (justEvaluated) {
            current.clear(); justEvaluated = false
        }
        if (!current.contains('.')) {
            if (current.isEmpty()) current.append('0')
            current.append('.')
            updateDisplay(current.toString())
        }
    }

    private fun toggleSign() {
        if (current.isEmpty()) {
            current.append('0')
        }
        if (current.startsWith("-")) {
            current.deleteCharAt(0)
        } else {
            current.insert(0, '-')
        }
        updateDisplay(current.toString())
    }

    private fun percent() {
        val value = getCurrentValue() / 100.0
        current.replace(0, current.length, trimDouble(value))
        updateDisplay(current.toString())
    }

    private fun onOperator(op: Char) {
        val value = getCurrentValue()
        if (lastOp == null) {
            accumulator = value
        } else {
            accumulator = applyOp(accumulator, value, lastOp!!)
        }
        lastOp = op

        expression.append(" $op ") // mostra o operador
        current.clear()
        updateDisplay(expression.toString())
    }

    private fun onEquals() {
        val value = getCurrentValue()
        if (lastOp != null) {
            accumulator = applyOp(accumulator, value, lastOp!!)
            lastOp = null
            current.replace(0, current.length, trimDouble(accumulator))
            updateDisplay(current.toString())
            justEvaluated = true
        }
    }

    private fun backspace() {
        if (current.isNotEmpty()) {
            current.deleteCharAt(current.length - 1)
            updateDisplay(if (current.isEmpty()) "0" else current.toString())
        }
    }

    private fun clearAll() {
        accumulator = 0.0
        current.clear()
        expression.clear()
        lastOp = null
        justEvaluated = false
        updateDisplay("0")
    }

    private fun getCurrentValue(): Double {
        return current.toString().toDoubleOrNull() ?: 0.0
    }

    private fun applyOp(a: Double, b: Double, op: Char): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> if (b == 0.0) Double.NaN else a / b
            else -> b
        }
    }

    private fun updateDisplay(text: String) {
        tv.text = text
    }

    private fun trimDouble(d: Double): String {
        val s = d.toString()
        return if (s.endsWith(".0")) s.dropLast(2) else s
    }
}