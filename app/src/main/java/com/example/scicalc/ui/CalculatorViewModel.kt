package com.example.scicalc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scicalc.data.HistoryItem
import com.example.scicalc.data.HistoryRepository
import com.example.scicalc.data.SettingsRepository
import com.example.scicalc.engine.AngleMode
import com.example.scicalc.engine.CalculatorEngine
import com.example.scicalc.engine.EvaluationContext
import com.example.scicalc.engine.EvaluationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val engine: CalculatorEngine = CalculatorEngine()
) : ViewModel() {
    private val _expression = MutableStateFlow("")
    private val _result = MutableStateFlow("")
    private val _isError = MutableStateFlow(false)
    private val _memoryValue = MutableStateFlow(0.0)
    private val _isSecond = MutableStateFlow(false)

    val settingsState: StateFlow<SettingsState> = settingsRepository.settings
        .mapToSettingsState()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), SettingsState())

    val historyState: StateFlow<HistoryState> = historyRepository.history
        .map { HistoryState(it) }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), HistoryState())

    val calculatorState: StateFlow<CalculatorState> = combine(
        _expression,
        _result,
        _isError,
        _memoryValue,
        _isSecond,
        settingsState
    ) { expression, result, isError, memory, second, settings ->
        CalculatorState(
            expression = expression,
            result = result,
            isError = isError,
            memoryValue = memory,
            isSecond = second,
            angleMode = settings.angleMode
        )
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), CalculatorState())

    fun append(text: String) {
        _expression.value += text
        preview()
    }

    fun addDigit(digit: String) = append(digit)

    fun addDecimal() {
        val lastNumber = currentNumberSegment()
        if (!lastNumber.contains('.')) {
            append(if (lastNumber.isEmpty()) "0." else ".")
        }
    }

    fun addOperator(op: String) {
        if (_expression.value.isNotBlank()) {
            append(op)
        }
    }

    fun addFunction(function: String) {
        append("$function(")
    }

    fun addConstant(constant: String) {
        append(constant)
    }

    fun addPostfix(postfix: String) {
        append(postfix)
        preview()
    }

    fun addExp() {
        val last = currentNumberSegment()
        if (!last.contains('E', true)) {
            append("E")
        }
    }

    fun toggleSign() {
        val expr = _expression.value
        val lastNumber = currentNumberSegment()
        if (lastNumber.isEmpty()) {
            append("-")
            return
        }
        val start = expr.lastIndexOf(lastNumber)
        val updated = if (lastNumber.startsWith("-")) {
            expr.removeRange(start, start + 1)
        } else {
            expr.substring(0, start) + "-" + expr.substring(start)
        }
        _expression.value = updated
        preview()
    }

    fun backspace() {
        if (_expression.value.isNotEmpty()) {
            _expression.value = _expression.value.dropLast(1)
            preview()
        }
    }

    fun clearEntry() {
        _expression.value = ""
        _result.value = ""
        _isError.value = false
    }

    fun allClear() {
        _expression.value = ""
        _result.value = ""
        _isError.value = false
        _memoryValue.value = 0.0
    }

    fun toggleSecond() {
        _isSecond.value = !_isSecond.value
    }

    fun evaluateAndCommit() {
        val context = EvaluationContext(angleMode = settingsState.value.angleMode, memoryValue = _memoryValue.value)
        when (val result = engine.evaluate(_expression.value, context)) {
            is EvaluationResult.Success -> {
                val formatted = engine.format(result.value)
                _result.value = formatted
                _isError.value = false
                viewModelScope.launch {
                    if (_expression.value.isNotBlank()) {
                        historyRepository.addItem(
                            HistoryItem(
                                expression = _expression.value,
                                result = formatted,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            is EvaluationResult.Error -> {
                _result.value = result.message
                _isError.value = true
            }
        }
    }

    fun setAngleMode(mode: AngleMode) {
        viewModelScope.launch {
            settingsRepository.updateAngleMode(mode)
        }
    }

    fun updateFullscreen(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateFullscreen(enabled) }
    }

    fun updateHaptics(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateHaptics(enabled) }
    }

    fun updateShowAds(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateShowAds(enabled) }
    }

    fun updateSound(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSound(enabled) }
    }

    fun updateKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateKeepScreenOn(enabled) }
    }

    fun memoryClear() {
        _memoryValue.value = 0.0
    }

    fun memoryRecall() {
        append(engine.format(_memoryValue.value))
    }

    fun memoryAdd() {
        val value = currentResultValue()
        _memoryValue.value += value
    }

    fun memorySubtract() {
        val value = currentResultValue()
        _memoryValue.value -= value
    }

    fun applyHistory(expression: String) {
        _expression.value = expression
        preview()
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepository.clear() }
    }

    private fun currentResultValue(): Double {
        val context = EvaluationContext(angleMode = settingsState.value.angleMode, memoryValue = _memoryValue.value)
        return when (val result = engine.evaluate(_expression.value, context)) {
            is EvaluationResult.Success -> result.value
            is EvaluationResult.Error -> 0.0
        }
    }

    private fun preview() {
        if (_expression.value.isBlank()) {
            _result.value = ""
            _isError.value = false
            return
        }
        val context = EvaluationContext(angleMode = settingsState.value.angleMode, memoryValue = _memoryValue.value)
        when (val result = engine.evaluate(_expression.value, context)) {
            is EvaluationResult.Success -> {
                _result.value = engine.format(result.value)
                _isError.value = false
            }
            is EvaluationResult.Error -> {
                _result.value = result.message
                _isError.value = true
            }
        }
    }

    private fun currentNumberSegment(): String {
        val expr = _expression.value
        if (expr.isBlank()) return ""
        val regex = Regex("-?[0-9.]+(?:[Ee][+-]?[0-9]+)?$")
        return regex.find(expr)?.value ?: ""
    }

    fun formatExpression(expression: String): String {
        return expression
            .replace("*", "×")
            .replace("/", "÷")
            .replace("root", "√")
            .replace("pow10(", "10^(")
            .replace("exp(", "e^(")
            .replace("pi", "π")
    }
}

private fun kotlinx.coroutines.flow.Flow<com.example.scicalc.data.SettingsData>.mapToSettingsState(): kotlinx.coroutines.flow.Flow<SettingsState> {
    return this.map { data ->
        SettingsState(
            angleMode = data.angleMode,
            fullscreen = data.fullscreen,
            haptics = data.haptics,
            showAds = data.showAds,
            sound = data.sound,
            keepScreenOn = data.keepScreenOn
        )
    }
}
