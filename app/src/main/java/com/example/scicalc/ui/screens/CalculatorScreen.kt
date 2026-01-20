package com.example.scicalc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.scicalc.R
import com.example.scicalc.ads.BannerAd
import com.example.scicalc.engine.AngleMode
import com.example.scicalc.ui.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel, navController: NavHostController) {
    val state by viewModel.calculatorState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val haptics = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SciCalc") },
                actions = {
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.History,
                            contentDescription = "History"
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BannerAd(
                modifier = Modifier.fillMaxWidth(),
                adUnitId = stringResource(id = R.string.admob_banner_id),
                settings = settings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            DisplayPanel(expression = viewModel.formatExpression(state.expression), result = state.result, isError = state.isError)
            Spacer(modifier = Modifier.height(12.dp))
            ModeRow(
                angleMode = state.angleMode,
                isSecond = state.isSecond,
                onModeChange = viewModel::setAngleMode,
                onToggleSecond = viewModel::toggleSecond
            )
            Spacer(modifier = Modifier.height(12.dp))
            ButtonsPanel(
                state = state,
                onPress = { action ->
                    if (settings.haptics) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    action()
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun DisplayPanel(expression: String, result: String, isError: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .padding(16.dp)
        ) {
            Text(
                text = expression.ifBlank { "0" },
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = result,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ModeRow(
    angleMode: AngleMode,
    isSecond: Boolean,
    onModeChange: (AngleMode) -> Unit,
    onToggleSecond: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ModeChip(label = "DEG", selected = angleMode == AngleMode.DEGREES) { onModeChange(AngleMode.DEGREES) }
        ModeChip(label = "RAD", selected = angleMode == AngleMode.RADIANS) { onModeChange(AngleMode.RADIANS) }
        ModeChip(label = "GRAD", selected = angleMode == AngleMode.GRADIANS) { onModeChange(AngleMode.GRADIANS) }
        Spacer(modifier = Modifier.width(8.dp))
        ModeChip(label = if (isSecond) "2nd" else "2nd", selected = isSecond) { onToggleSecond() }
    }
}

@Composable
private fun ModeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 12.sp)
    }
}

@Composable
private fun ButtonsPanel(
    state: com.example.scicalc.ui.CalculatorState,
    onPress: (() -> Unit) -> Unit,
    viewModel: CalculatorViewModel
) {
    val rowSpacing = 8.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(rowSpacing),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("MC") { onPress { viewModel.memoryClear() } }
            CalcButton("MR") { onPress { viewModel.memoryRecall() } }
            CalcButton("M+") { onPress { viewModel.memoryAdd() } }
            CalcButton("M-") { onPress { viewModel.memorySubtract() } }
            CalcButton("AC", container = MaterialTheme.colorScheme.error) { onPress { viewModel.allClear() } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton(if (state.isSecond) "asin" else "sin") { onPress { viewModel.addFunction(if (state.isSecond) "asin" else "sin") } }
            CalcButton(if (state.isSecond) "acos" else "cos") { onPress { viewModel.addFunction(if (state.isSecond) "acos" else "cos") } }
            CalcButton(if (state.isSecond) "atan" else "tan") { onPress { viewModel.addFunction(if (state.isSecond) "atan" else "tan") } }
            CalcButton(if (state.isSecond) "10^x" else "log") { onPress { viewModel.addFunction(if (state.isSecond) "pow10" else "log") } }
            CalcButton(if (state.isSecond) "e^x" else "ln") { onPress { viewModel.addFunction(if (state.isSecond) "exp" else "ln") } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("π") { onPress { viewModel.addConstant("pi") } }
            CalcButton("e") { onPress { viewModel.addConstant("e") } }
            CalcButton("x²") { onPress { viewModel.append("^2") } }
            CalcButton("x³") { onPress { viewModel.append("^3") } }
            CalcButton("xʸ") { onPress { viewModel.addOperator("^") } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("√") { onPress { viewModel.addFunction("sqrt") } }
            CalcButton("x√y") { onPress { viewModel.addOperator("root") } }
            CalcButton("1/x") { onPress { viewModel.addFunction("inv") } }
            CalcButton("|") { onPress { viewModel.addFunction("abs") } }
            CalcButton("n!") { onPress { viewModel.addPostfix("!") } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("nPr") { onPress { viewModel.append("npr(") } }
            CalcButton("nCr") { onPress { viewModel.append("ncr(") } }
            CalcButton("mod") { onPress { viewModel.addOperator("mod") } }
            CalcButton("EXP") { onPress { viewModel.addExp() } }
            CalcButton("(") { onPress { viewModel.append("(") } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton(")") { onPress { viewModel.append(")") } }
            CalcButton("CE") { onPress { viewModel.clearEntry() } }
            CalcButton("⌫") { onPress { viewModel.backspace() } }
            CalcButton("%") { onPress { viewModel.addPostfix("%") } }
            CalcButton("÷") { onPress { viewModel.addOperator("/") } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("7") { onPress { viewModel.addDigit("7") } }
            CalcButton("8") { onPress { viewModel.addDigit("8") } }
            CalcButton("9") { onPress { viewModel.addDigit("9") } }
            CalcButton("×") { onPress { viewModel.addOperator("*") } }
            CalcButton("±") { onPress { viewModel.toggleSign() } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("4") { onPress { viewModel.addDigit("4") } }
            CalcButton("5") { onPress { viewModel.addDigit("5") } }
            CalcButton("6") { onPress { viewModel.addDigit("6") } }
            CalcButton("-") { onPress { viewModel.addOperator("-") } }
            CalcButton("+") { onPress { viewModel.addOperator("+") } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("1") { onPress { viewModel.addDigit("1") } }
            CalcButton("2") { onPress { viewModel.addDigit("2") } }
            CalcButton("3") { onPress { viewModel.addDigit("3") } }
            CalcButton("0", modifier = Modifier.weight(1.2f)) { onPress { viewModel.addDigit("0") } }
            CalcButton(".") { onPress { viewModel.addDecimal() } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CalcButton("=") { onPress { viewModel.evaluateAndCommit() } }
        }
    }
}

@Composable
private fun CalcButton(
    label: String,
    modifier: Modifier = Modifier.weight(1f),
    container: Color = MaterialTheme.colorScheme.secondaryContainer,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container)
    ) {
        Text(text = label, fontSize = 16.sp, textAlign = TextAlign.Center)
    }
}
