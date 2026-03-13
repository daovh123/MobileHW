package com.example.exam_1

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.exam_1.ui.theme.Exam_1Theme
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Exam_1Theme(dynamicColor = false) {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    val isDark = isSystemInDarkTheme()

    val bgColor       = if (isDark) Color(0xFF000000) else Color(0xFFF2F2F2)
    val numBtnColor   = if (isDark) Color(0xFF2A2A2A) else Color(0xFFDDE4F0)
    val clearBtnColor = if (isDark) Color(0xFF7B3A48) else Color(0xFFECA0AD)
    val operatorColor = Color(0xFFD4A843)
    val equalColor    = Color(0xFF4CAF68)
    val textColor     = if (isDark) Color.White else Color(0xFF1A1A1A)
    val exprColor     = Color(0xFF888888)

    // so hien thi
    var display       by remember { mutableStateOf("0") }
    //toan tu
    var expression    by remember { mutableStateOf("") }
    // so dau tien
    var firstOperand  by remember { mutableStateOf<Double?>(null) }
    // dau dang cho tinh
    var pendingOp     by remember { mutableStateOf<String?>(null) }
    // flag sau khi an = , op
    var isNewInput    by remember { mutableStateOf(false) }
    // flag sau khi an =
    var justCalc      by remember { mutableStateOf(false) }

//exception
    fun fmt(n: Double): String {
        if (n.isInfinite()) return "Cannot divide by 0"
        if (n.isNaN()) return "Error"
        return if (n == floor(n)) n.toLong().toString()
        else n.toBigDecimal().stripTrailingZeros().toPlainString()
    }

// calculate
    fun compute(a: Double, op: String, b: Double): Double = when (op) {
        "+" -> a + b
        "-" -> a - b
        "×" -> a * b
        "/" -> if (b != 0.0) a / b else Double.POSITIVE_INFINITY
        else -> b
    }
//0123456789
    fun onDigit(d: String) {
        if (isNewInput || justCalc) {
            display = d
            isNewInput = false
            if (justCalc) { expression = ""; justCalc = false }
        } else {
            if (display.replace(".", "").replace("-", "").length >= 15) return
            display = if (display == "0") d else display + d
        }
    }
//.
    fun onDecimal() {
        if (isNewInput || justCalc) {
            display = "0."
            isNewInput = false
            if (justCalc) { expression = ""; justCalc = false }
        } else if (!display.contains(".")) {
            display += "."
        }
    }
//00
    fun onDoubleZero() {
        if (!isNewInput && !justCalc && display != "0" &&
            display.replace(".", "").replace("-", "").length < 14
        ) {
            display += "00"
        }
    }
// +/*-
    fun onOperator(op: String) {
        val cur = display.toDoubleOrNull() ?: 0.0

        if (!isNewInput && pendingOp != null && !justCalc) {
            val res = compute(firstOperand!!, pendingOp!!, cur)
            val resStr = fmt(res)
            expression   = "$resStr $op"
            firstOperand = res
        } else {
            expression   = "${fmt(cur)} $op"
            firstOperand = cur
        }

        pendingOp  = op
        isNewInput = true
        justCalc   = false
        display    = "0"
    }
// =
    fun onEquals() {
        if (pendingOp == null || firstOperand == null) return
        val second = display.toDoubleOrNull() ?: 0.0
        val result = compute(firstOperand!!, pendingOp!!, second)
        expression   = "${expression.trimEnd()} ${fmt(second)} ="
        display      = fmt(result)  
        pendingOp    = null
        firstOperand = null
        justCalc     = true
    }
// C
    fun onClear() {
        display      = "0"
        expression   = ""
        firstOperand = null
        pendingOp    = null
        isNewInput   = false
        justCalc     = false
    }

// CE
    fun onClearEntry() {
        display    = "0"
        if (justCalc) expression = ""
        justCalc   = false
        isNewInput = false
    }

    fun onPercent() {
        val cur    = display.toDoubleOrNull() ?: 0.0
        val result = if (firstOperand != null) firstOperand!! * cur / 100.0
                     else cur / 100.0
        display = fmt(result)
    }

    val buttonRows = listOf(
        listOf("C",  "CE", "%", "/"),
        listOf("7",  "8",  "9", "×"),
        listOf("4",  "5",  "6", "-"),
        listOf("1",  "2",  "3", "+"),
        listOf("00", "0",  ".", "=")
    )

    val displayFontSize = when {
        display.length > 14 -> 22.sp
        display.length > 10 -> 32.sp
        display.length > 7  -> 42.sp
        else                -> 52.sp
    }

// screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .systemBarsPadding()
    ) {
        // screen 1
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement   = Arrangement.Bottom,
            horizontalAlignment   = Alignment.End
        ) {
            // small display
            Text(
                text      = expression,
                color     = exprColor,
                fontSize  = 18.sp,
                textAlign = TextAlign.End,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            // main display
            Text(
                text       = display,
                color      = textColor,
                fontSize   = displayFontSize,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.End,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
        // button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            buttonRows.forEach { row ->
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { label ->
                        val btnBg = when (label) {
                            "C"                -> clearBtnColor
                            "+", "-", "×", "/" -> operatorColor
                            "="                -> equalColor
                            else               -> numBtnColor
                        }
                        val btnFg = when (label) {
                            "C", "+", "-", "×", "/", "=" -> Color.White
                            else                          -> textColor
                        }

                        Button(
                            onClick = {
                                when (label) {
                                    "C"  -> onClear()
                                    "CE" -> onClearEntry()
                                    "%"  -> onPercent()
                                    "/"  -> onOperator("/")
                                    "×"  -> onOperator("×")
                                    "-"  -> onOperator("-")
                                    "+"  -> onOperator("+")
                                    "="  -> onEquals()
                                    "."  -> onDecimal()
                                    "00" -> onDoubleZero()
                                    else -> onDigit(label)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            shape  = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = btnBg)
                        ) {
                            Text(
                                text       = label,
                                fontSize   = if (label == "00") 18.sp else 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = btnFg
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(name = "Light Mode", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun LightModePreview() {
    Exam_1Theme(darkTheme = false, dynamicColor = false) {
        CalculatorApp()
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    widthDp = 360,
    heightDp = 720,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun DarkModePreview() {
    Exam_1Theme(darkTheme = true, dynamicColor = false) {
        CalculatorApp()
    }
}