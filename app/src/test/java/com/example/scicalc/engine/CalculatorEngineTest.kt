package com.example.scicalc.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {
    private val engine = CalculatorEngine()

    private fun eval(expression: String, mode: AngleMode = AngleMode.DEGREES): Double {
        val result = engine.evaluate(expression, EvaluationContext(angleMode = mode))
        return (result as EvaluationResult.Success).value
    }

    @Test
    fun `addition and multiplication precedence`() {
        assertEquals(14.0, eval("2+3*4"), 1e-9)
    }

    @Test
    fun `parentheses override precedence`() {
        assertEquals(20.0, eval("(2+3)*4"), 1e-9)
    }

    @Test
    fun `implicit multiplication with parentheses`() {
        assertEquals(14.0, eval("2(3+4)"), 1e-9)
    }

    @Test
    fun `implicit multiplication with constants`() {
        assertEquals(Math.PI * 2, eval("2pi"), 1e-9)
    }

    @Test
    fun `power right associativity`() {
        assertEquals(512.0, eval("2^3^2"), 1e-9)
    }

    @Test
    fun `square and cube`() {
        assertEquals(9.0, eval("3^2"), 1e-9)
        assertEquals(27.0, eval("3^3"), 1e-9)
    }

    @Test
    fun `nth root operator`() {
        assertEquals(3.0, eval("27root3"), 1e-9)
    }

    @Test
    fun `sqrt function`() {
        assertEquals(4.0, eval("sqrt(16)"), 1e-9)
    }

    @Test
    fun `reciprocal`() {
        assertEquals(0.25, eval("inv(4)"), 1e-9)
    }

    @Test
    fun `factorial`() {
        assertEquals(120.0, eval("5!"), 1e-9)
    }

    @Test
    fun `percent postfix`() {
        assertEquals(0.5, eval("50%"), 1e-9)
    }

    @Test
    fun `mod operator`() {
        assertEquals(1.0, eval("10mod3"), 1e-9)
    }

    @Test
    fun `absolute value`() {
        assertEquals(5.0, eval("abs(-5)"), 1e-9)
    }

    @Test
    fun `log base 10`() {
        assertEquals(2.0, eval("log(100)"), 1e-9)
    }

    @Test
    fun `natural log`() {
        assertEquals(1.0, eval("ln(e)"), 1e-9)
    }

    @Test
    fun `exp and pow10`() {
        assertEquals(Math.E, eval("exp(1)"), 1e-9)
        assertEquals(1000.0, eval("pow10(3)"), 1e-9)
    }

    @Test
    fun `scientific notation parsing`() {
        assertEquals(1200.0, eval("1.2E3"), 1e-9)
    }

    @Test
    fun `unary minus`() {
        assertEquals(-6.0, eval("-3*2"), 1e-9)
    }

    @Test
    fun `nested parentheses`() {
        assertEquals(7.0, eval("(1+(2+4))"), 1e-9)
    }

    @Test
    fun `trig degrees`() {
        assertEquals(0.5, eval("sin(30)", AngleMode.DEGREES), 1e-9)
    }

    @Test
    fun `trig radians`() {
        assertEquals(0.0, eval("cos(1.57079632679)", AngleMode.RADIANS), 1e-6)
    }

    @Test
    fun `trig gradians`() {
        assertEquals(0.0, eval("cos(100)", AngleMode.GRADIANS), 1e-6)
    }

    @Test
    fun `inverse trig degrees`() {
        assertEquals(30.0, eval("asin(0.5)", AngleMode.DEGREES), 1e-6)
    }

    @Test
    fun `combination`() {
        assertEquals(10.0, eval("ncr(5,2)"), 1e-9)
    }

    @Test
    fun `permutation`() {
        assertEquals(20.0, eval("npr(5,2)"), 1e-9)
    }

    @Test
    fun `implicit multiplication with function`() {
        assertEquals(1.0, eval("2sin(30)", AngleMode.DEGREES), 1e-9)
    }

    @Test
    fun `error divide by zero`() {
        val result = engine.evaluate("1/0", EvaluationContext())
        assertTrue(result is EvaluationResult.Error)
    }

    @Test
    fun `error invalid log`() {
        val result = engine.evaluate("log(-1)", EvaluationContext())
        assertTrue(result is EvaluationResult.Error)
    }

    @Test
    fun `error invalid factorial`() {
        val result = engine.evaluate("2.5!", EvaluationContext())
        assertTrue(result is EvaluationResult.Error)
    }

    @Test
    fun `complex expression`() {
        assertEquals(9.0, eval("(2+1)^2"), 1e-9)
    }

    @Test
    fun `formatter trims zeros`() {
        val formatted = engine.format(2.5000)
        assertEquals("2.5", formatted)
    }

    @Test
    fun `formatter scientific`() {
        val formatted = engine.format(1.2e15)
        assertTrue(formatted.contains("e"))
    }

    @Test
    fun `root and power chaining`() {
        assertEquals(4.0, eval("(16root2)^2"), 1e-9)
    }

    @Test
    fun `implicit multiplication after postfix`() {
        assertEquals(12.0, eval("3!2"), 1e-9)
    }

    @Test
    fun `percent with addition`() {
        assertEquals(1.5, eval("1+50%"), 1e-9)
    }

    @Test
    fun `mod with negative`() {
        assertEquals(-1.0, eval("-10mod3"), 1e-9)
    }
}
