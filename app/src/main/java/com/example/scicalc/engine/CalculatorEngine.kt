package com.example.scicalc.engine

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class CalculatorEngine {
    fun evaluate(expression: String, context: EvaluationContext): EvaluationResult {
        if (expression.isBlank()) return EvaluationResult.Success(0.0)
        val tokens = Tokenizer.tokenize(expression)
        val rpn = ShuntingYard.toRpn(tokens)
        return Evaluator.evaluate(rpn, context)
    }

    fun format(value: Double): String = ResultFormatter.format(value)
}

data class EvaluationContext(
    val angleMode: AngleMode = AngleMode.DEGREES,
    val memoryValue: Double = 0.0
)

sealed class EvaluationResult {
    data class Success(val value: Double) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}

private enum class TokenType { NUMBER, OPERATOR, FUNCTION, CONSTANT, PAREN_OPEN, PAREN_CLOSE, COMMA, POSTFIX }

private data class Token(val type: TokenType, val text: String, val value: Double? = null)

private object Tokenizer {
    private val functions = setOf(
        "sin", "cos", "tan", "asin", "acos", "atan",
        "log", "ln", "sqrt", "abs", "exp", "pow10", "inv", "ncr", "npr"
    )
    private val constants = mapOf("pi" to Math.PI, "e" to Math.E)
    private val operators = setOf("+", "-", "*", "/", "^", "mod", "root")
    private val postfix = setOf("!", "%")

    fun tokenize(expression: String): List<Token> {
        val cleaned = expression.replace("×", "*").replace("÷", "/")
        val tokens = mutableListOf<Token>()
        var index = 0
        while (index < cleaned.length) {
            val c = cleaned[index]
            when {
                c.isWhitespace() -> index++
                c.isDigit() || c == '.' -> {
                    val start = index
                    var hasExp = false
                    index++
                    while (index < cleaned.length) {
                        val ch = cleaned[index]
                        if (ch.isDigit() || ch == '.') {
                            index++
                        } else if ((ch == 'E' || ch == 'e') && !hasExp) {
                            hasExp = true
                            index++
                            if (index < cleaned.length && (cleaned[index] == '+' || cleaned[index] == '-')) {
                                index++
                            }
                        } else {
                            break
                        }
                    }
                    val text = cleaned.substring(start, index)
                    tokens.add(Token(TokenType.NUMBER, text, text.toDouble()))
                }
                c.isLetter() -> {
                    val start = index
                    index++
                    while (index < cleaned.length && cleaned[index].isLetterOrDigit()) {
                        index++
                    }
                    val text = cleaned.substring(start, index)
                    when {
                        functions.contains(text) -> tokens.add(Token(TokenType.FUNCTION, text))
                        constants.containsKey(text) -> tokens.add(Token(TokenType.CONSTANT, text, constants.getValue(text)))
                        text == "mod" -> tokens.add(Token(TokenType.OPERATOR, text))
                        text == "root" -> tokens.add(Token(TokenType.OPERATOR, text))
                        else -> throw IllegalArgumentException("Unknown token: $text")
                    }
                }
                c == '(' -> {
                    tokens.add(Token(TokenType.PAREN_OPEN, "("))
                    index++
                }
                c == ')' -> {
                    tokens.add(Token(TokenType.PAREN_CLOSE, ")"))
                    index++
                }
                c == ',' -> {
                    tokens.add(Token(TokenType.COMMA, ","))
                    index++
                }
                postfix.contains(c.toString()) -> {
                    tokens.add(Token(TokenType.POSTFIX, c.toString()))
                    index++
                }
                operators.contains(c.toString()) -> {
                    tokens.add(Token(TokenType.OPERATOR, c.toString()))
                    index++
                }
                c == '-' -> {
                    tokens.add(Token(TokenType.OPERATOR, "-"))
                    index++
                }
                else -> throw IllegalArgumentException("Unexpected character: $c")
            }
        }
        return insertImplicitMultiplication(tokens)
    }

    private fun insertImplicitMultiplication(tokens: List<Token>): List<Token> {
        if (tokens.isEmpty()) return tokens
        val output = mutableListOf<Token>()
        tokens.forEachIndexed { index, token ->
            output.add(token)
            if (index == tokens.lastIndex) return@forEachIndexed
            val next = tokens[index + 1]
            val shouldInsert = (token.type == TokenType.NUMBER || token.type == TokenType.CONSTANT || token.type == TokenType.PAREN_CLOSE || token.type == TokenType.POSTFIX)
                && (next.type == TokenType.NUMBER || next.type == TokenType.CONSTANT || next.type == TokenType.FUNCTION || next.type == TokenType.PAREN_OPEN)
            if (shouldInsert) {
                output.add(Token(TokenType.OPERATOR, "*"))
            }
        }
        return output
    }
}

private object ShuntingYard {
    private val precedence = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2,
        "mod" to 2,
        "root" to 3,
        "^" to 4,
        "u-" to 5
    )

    private val rightAssociative = setOf("^", "u-")

    fun toRpn(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()
        var previous: Token? = null
        tokens.forEach { token ->
            when (token.type) {
                TokenType.NUMBER, TokenType.CONSTANT -> output.add(token)
                TokenType.FUNCTION -> stack.addFirst(token)
                TokenType.COMMA -> {
                    while (stack.isNotEmpty() && stack.first().type != TokenType.PAREN_OPEN) {
                        output.add(stack.removeFirst())
                    }
                }
                TokenType.OPERATOR -> {
                    val opText = if (token.text == "-" && (previous == null || previous?.type in setOf(TokenType.OPERATOR, TokenType.PAREN_OPEN, TokenType.COMMA))) {
                        "u-"
                    } else token.text
                    val opToken = token.copy(text = opText)
                    while (stack.isNotEmpty() && stack.first().type in setOf(TokenType.OPERATOR) &&
                        (precedence.getValue(stack.first().text) > precedence.getValue(opText) ||
                            (precedence.getValue(stack.first().text) == precedence.getValue(opText) && opText !in rightAssociative))
                    ) {
                        output.add(stack.removeFirst())
                    }
                    stack.addFirst(opToken)
                }
                TokenType.POSTFIX -> output.add(token)
                TokenType.PAREN_OPEN -> stack.addFirst(token)
                TokenType.PAREN_CLOSE -> {
                    while (stack.isNotEmpty() && stack.first().type != TokenType.PAREN_OPEN) {
                        output.add(stack.removeFirst())
                    }
                    if (stack.isEmpty()) throw IllegalArgumentException("Mismatched parentheses")
                    stack.removeFirst()
                    if (stack.isNotEmpty() && stack.first().type == TokenType.FUNCTION) {
                        output.add(stack.removeFirst())
                    }
                }
            }
            previous = token
        }
        while (stack.isNotEmpty()) {
            val token = stack.removeFirst()
            if (token.type == TokenType.PAREN_OPEN) throw IllegalArgumentException("Mismatched parentheses")
            output.add(token)
        }
        return output
    }
}

private object Evaluator {
    fun evaluate(rpn: List<Token>, context: EvaluationContext): EvaluationResult {
        return runCatching {
            val stack = ArrayDeque<Double>()
            rpn.forEach { token ->
                when (token.type) {
                    TokenType.NUMBER, TokenType.CONSTANT -> stack.addFirst(token.value ?: 0.0)
                    TokenType.OPERATOR -> applyOperator(stack, token.text)
                    TokenType.POSTFIX -> applyPostfix(stack, token.text)
                    TokenType.FUNCTION -> applyFunction(stack, token.text, context)
                    else -> {}
                }
            }
            if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
            val value = stack.first()
            if (value.isInfinite() || value.isNaN()) throw ArithmeticException("Overflow or invalid")
            EvaluationResult.Success(value)
        }.getOrElse { error ->
            EvaluationResult.Error(error.message ?: "Error")
        }
    }

    private fun applyOperator(stack: ArrayDeque<Double>, op: String) {
        if (op == "u-") {
            val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
            stack.addFirst(-value)
            return
        }
        val right = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
        val left = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
        val result = when (op) {
            "+" -> left + right
            "-" -> left - right
            "*" -> left * right
            "/" -> if (right == 0.0) throw ArithmeticException("Divide by zero") else left / right
            "^" -> left.pow(right)
            "mod" -> left % right
            "root" -> if (right == 0.0) throw ArithmeticException("Zero root") else left.pow(1.0 / right)
            else -> throw IllegalArgumentException("Unknown operator $op")
        }
        stack.addFirst(result)
    }

    private fun applyPostfix(stack: ArrayDeque<Double>, op: String) {
        val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
        val result = when (op) {
            "!" -> factorial(value)
            "%" -> value / 100.0
            else -> throw IllegalArgumentException("Unknown postfix $op")
        }
        stack.addFirst(result)
    }

    private fun applyFunction(stack: ArrayDeque<Double>, name: String, context: EvaluationContext) {
        when (name) {
            "sin", "cos", "tan", "asin", "acos", "atan" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                val radians = when (context.angleMode) {
                    AngleMode.DEGREES -> Math.toRadians(value)
                    AngleMode.GRADIANS -> value * Math.PI / 200.0
                    AngleMode.RADIANS -> value
                }
                val result = when (name) {
                    "sin" -> sin(radians)
                    "cos" -> cos(radians)
                    "tan" -> tan(radians)
                    "asin" -> asin(value)
                    "acos" -> acos(value)
                    else -> atan(value)
                }
                val converted = when (name) {
                    "asin", "acos", "atan" -> when (context.angleMode) {
                        AngleMode.DEGREES -> Math.toDegrees(result)
                        AngleMode.GRADIANS -> result * 200.0 / Math.PI
                        AngleMode.RADIANS -> result
                    }
                    else -> result
                }
                stack.addFirst(converted)
            }
            "log" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                if (value <= 0.0) throw ArithmeticException("Invalid log")
                stack.addFirst(log10(value))
            }
            "ln" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                if (value <= 0.0) throw ArithmeticException("Invalid ln")
                stack.addFirst(ln(value))
            }
            "sqrt" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                if (value < 0.0) throw ArithmeticException("Invalid sqrt")
                stack.addFirst(sqrt(value))
            }
            "abs" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                stack.addFirst(abs(value))
            }
            "exp" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                stack.addFirst(exp(value))
            }
            "pow10" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                stack.addFirst(10.0.pow(value))
            }
            "inv" -> {
                val value = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                if (value == 0.0) throw ArithmeticException("Divide by zero")
                stack.addFirst(1.0 / value)
            }
            "ncr" -> {
                val r = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                val n = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                stack.addFirst(combination(n, r))
            }
            "npr" -> {
                val r = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                val n = stack.removeFirstOrNull() ?: throw IllegalArgumentException("Missing operand")
                stack.addFirst(permutation(n, r))
            }
        }
    }

    private fun factorial(value: Double): Double {
        if (value < 0 || value % 1.0 != 0.0) throw ArithmeticException("Invalid factorial")
        val n = value.toInt()
        var result = 1.0
        for (i in 2..n) result *= i
        return result
    }

    private fun permutation(n: Double, r: Double): Double {
        if (n < 0 || r < 0 || n % 1.0 != 0.0 || r % 1.0 != 0.0) throw ArithmeticException("Invalid nPr")
        val nInt = n.toInt()
        val rInt = r.toInt()
        if (rInt > nInt) throw ArithmeticException("Invalid nPr")
        var result = 1.0
        for (i in (nInt - rInt + 1)..nInt) result *= i
        return result
    }

    private fun combination(n: Double, r: Double): Double {
        if (n < 0 || r < 0 || n % 1.0 != 0.0 || r % 1.0 != 0.0) throw ArithmeticException("Invalid nCr")
        val nInt = n.toInt()
        val rInt = r.toInt()
        if (rInt > nInt) throw ArithmeticException("Invalid nCr")
        val k = minOf(rInt, nInt - rInt)
        var result = 1.0
        for (i in 1..k) {
            result = result * (nInt - k + i) / i
        }
        return round(result)
    }
}

object ResultFormatter {
    fun format(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        val absValue = abs(value)
        val formatted = when {
            absValue == 0.0 -> "0"
            absValue >= 1e12 || absValue < 1e-9 -> "%.12E".format(value)
            else -> {
                val raw = "%.12f".format(value)
                raw.trimEnd('0').trimEnd('.')
            }
        }
        return formatted.replace("E", "e")
    }
}
