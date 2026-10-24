// Copyright 2026 Vindhya Data Science
// SPDX-License-Identifier: MPL-2.0
package skip.unit

/// Exercise real assertion failures, not a duplicate test-only equality implementation.
class FloatingPointAssertionTests : XCTestCase {
    private fun mustFail(block: () -> Unit): AssertionError =
        org.junit.Assert.assertThrows(AssertionError::class.java, block)

    @org.junit.Test
    fun xctestAnyOverloadsUseIEEEEquality() {
        val equal = listOf<(Any?, Any?) -> Unit>(
            { a, b -> XCTAssertEqual(a, b) },
            { a, b -> XCTAssertEqual(a, b, "equal message") }
        )
        val unequal = listOf<(Any?, Any?) -> Unit>(
            { a, b -> XCTAssertNotEqual(a, b) },
            { a, b -> XCTAssertNotEqual(a, b, "unequal message") }
        )
        for (assertEqual in equal) {
            assertEqual(-0.0, 0.0)
            assertEqual(-0.0f, 0.0f)
            assertEqual(null, null)
            mustFail { assertEqual(Double.NaN, Double.NaN) }
            mustFail { assertEqual(Float.NaN, Float.NaN) }
            mustFail { assertEqual(null, 0.0) }
        }
        for (assertUnequal in unequal) {
            assertUnequal(Double.NaN, Double.NaN)
            assertUnequal(Float.NaN, Float.NaN)
            assertUnequal(null, 0.0)
            mustFail { assertUnequal(-0.0, 0.0) }
            mustFail { assertUnequal(-0.0f, 0.0f) }
            mustFail { assertUnequal(null, null) }
        }
    }

    @org.junit.Test
    fun xctestComparableOverloadsUseIEEEEquality() {
        val negative: Double? = -0.0
        val positive: Double? = 0.0
        val nan: Double? = Double.NaN
        XCTAssertEqual(negative, positive)
        XCTAssertEqual(negative, positive, "equal message")
        XCTAssertNotEqual(nan, nan)
        XCTAssertNotEqual(nan, nan, "unequal message")
        mustFail { XCTAssertEqual(nan, nan) }
        mustFail { XCTAssertEqual(nan, nan, "equal message") }
        mustFail { XCTAssertNotEqual(negative, positive) }
        mustFail { XCTAssertNotEqual(negative, positive, "unequal message") }
        val floatNan: Float? = Float.NaN
        XCTAssertEqual<Float>(-0.0f, 0.0f)
        XCTAssertEqual<Float>(-0.0f, 0.0f, "equal float")
        XCTAssertNotEqual(floatNan, floatNan)
        XCTAssertNotEqual(floatNan, floatNan, "unequal float")
        mustFail { XCTAssertEqual(floatNan, floatNan) }
        mustFail { XCTAssertEqual(floatNan, floatNan, "equal float") }
        mustFail { XCTAssertNotEqual<Float>(-0.0f, 0.0f) }
        mustFail { XCTAssertNotEqual<Float>(-0.0f, 0.0f, "unequal float") }
    }

    @org.junit.Test
    fun swiftTestingAnyAndRequireOverloadsUseIEEEEquality() {
        val equal = listOf<(Any?, Any?) -> Unit>(
            { a, b -> expectEqual(a, b) }, { a, b -> expectEqual(a, b, "expect equal") },
            { a, b -> requireEqual(a, b) }, { a, b -> requireEqual(a, b, "require equal") }
        )
        val unequal = listOf<(Any?, Any?) -> Unit>(
            { a, b -> expectNotEqual(a, b) }, { a, b -> expectNotEqual(a, b, "expect unequal") },
            { a, b -> requireNotEqual(a, b) }, { a, b -> requireNotEqual(a, b, "require unequal") }
        )
        for (assertEqual in equal) {
            assertEqual(-0.0, 0.0)
            assertEqual(-0.0f, 0.0f)
            assertEqual(null, null)
            mustFail { assertEqual(Double.NaN, Double.NaN) }
            mustFail { assertEqual(Float.NaN, Float.NaN) }
        }
        for (assertUnequal in unequal) {
            assertUnequal(Double.NaN, Double.NaN)
            assertUnequal(Float.NaN, Float.NaN)
            mustFail { assertUnequal(-0.0, 0.0) }
            mustFail { assertUnequal(-0.0f, 0.0f) }
            mustFail { assertUnequal(null, null) }
        }
    }

    @org.junit.Test
    fun swiftTestingComparableOverloadsUseIEEEEquality() {
        expectEqual<Double>(-0.0, 0.0)
        expectEqual<Double>(-0.0, 0.0, "expect equal")
        requireEqual<Double>(-0.0, 0.0)
        requireEqual<Double>(-0.0, 0.0, "require equal")
        expectNotEqual<Double>(Double.NaN, Double.NaN)
        expectNotEqual<Double>(Double.NaN, Double.NaN, "expect unequal")
        mustFail { expectEqual<Double>(Double.NaN, Double.NaN) }
        mustFail { expectEqual<Double>(Double.NaN, Double.NaN, "expect equal") }
        mustFail { requireEqual<Double>(Double.NaN, Double.NaN) }
        mustFail { requireEqual<Double>(Double.NaN, Double.NaN, "require equal") }
        mustFail { expectNotEqual<Double>(-0.0, 0.0) }
        mustFail { expectNotEqual<Double>(-0.0, 0.0, "expect unequal") }
        expectEqual<Float>(-0.0f, 0.0f)
        expectEqual<Float>(-0.0f, 0.0f, "expect float")
        requireEqual<Float>(-0.0f, 0.0f)
        requireEqual<Float>(-0.0f, 0.0f, "require float")
        expectNotEqual<Float>(Float.NaN, Float.NaN)
        expectNotEqual<Float>(Float.NaN, Float.NaN, "expect float")
        mustFail { expectEqual<Float>(Float.NaN, Float.NaN) }
        mustFail { expectEqual<Float>(Float.NaN, Float.NaN, "expect float") }
        mustFail { requireEqual<Float>(Float.NaN, Float.NaN) }
        mustFail { requireEqual<Float>(Float.NaN, Float.NaN, "require float") }
        mustFail { expectNotEqual<Float>(-0.0f, 0.0f) }
        mustFail { expectNotEqual<Float>(-0.0f, 0.0f, "expect float") }
    }

    @org.junit.Test
    fun closureOverloadsEvaluateOnceInOrderAndMessagesOnlyOnFailure() {
        val trace = mutableListOf<String>()
        val a = { trace.add("a"); -0.0 as Any? }
        val b = { trace.add("b"); 0.0 as Any? }
        val message = { trace.add("message"); "lazy diagnostic" }
        XCTAssertEqual(a, b)
        org.junit.Assert.assertEquals(listOf("a", "b"), trace)
        trace.clear()
        XCTAssertEqual(a, b, message)
        org.junit.Assert.assertEquals(listOf("a", "b"), trace)
        trace.clear()
        val failure = mustFail { XCTAssertNotEqual(a, b, message) }
        org.junit.Assert.assertTrue(failure.message.orEmpty().contains("lazy diagnostic"))
        org.junit.Assert.assertEquals(listOf("a", "b", "message"), trace)
        trace.clear()
        mustFail { XCTAssertNotEqual(a, b) }
        org.junit.Assert.assertEquals(listOf("a", "b"), trace)
        val nan = { Double.NaN as Any? }
        XCTAssertNotEqual(nan, nan)
        XCTAssertNotEqual(nan, nan, { "nan unequal" })
        mustFail { XCTAssertEqual(nan, nan) }
        mustFail { XCTAssertEqual(nan, nan, { "nan equal" }) }
        trace.clear()
        val one = { trace.add("a"); 1 as Any? }
        val two = { trace.add("b"); 2 as Any? }
        XCTAssertNotEqual(one, two, message)
        org.junit.Assert.assertEquals(listOf("a", "b"), trace)
        trace.clear()
        mustFail { XCTAssertEqual(one, two, message) }
        org.junit.Assert.assertEquals(listOf("a", "b", "message"), trace)
        trace.clear()
        val throwing = { trace.add("throw"); throw IllegalStateException("operand") }
        org.junit.Assert.assertThrows(IllegalStateException::class.java) { XCTAssertEqual(throwing, b, message) }
        org.junit.Assert.assertEquals(listOf("throw"), trace)
        trace.clear()
        org.junit.Assert.assertThrows(IllegalStateException::class.java) { XCTAssertEqual(a, throwing, message) }
        org.junit.Assert.assertEquals(listOf("a", "throw"), trace)
    }

    @org.junit.Test
    fun accuracyMatchesNativeNaNInfinityAndNegativeTolerance() {
        XCTAssertEqual(-0.0, 0.0, accuracy = 0.0)
        XCTAssertEqual(1.0, 2.0, accuracy = -1.0)
        XCTAssertEqual(1.0, 1.0, accuracy = Double.NaN)
        XCTAssertEqual(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, accuracy = 0.0)
        XCTAssertEqual(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, accuracy = Double.POSITIVE_INFINITY)
        mustFail { XCTAssertEqual(Double.NaN, Double.NaN, accuracy = 0.0) }
        mustFail { XCTAssertEqual(Double.NaN, 1.0, accuracy = 1.0) }
        mustFail { XCTAssertEqual(1.0, 2.0, accuracy = Double.NaN) }
        mustFail { XCTAssertEqual(Double.NaN, Double.NaN, accuracy = 0.0, msg = "double accuracy") }
        XCTAssertEqual(-0.0f, 0.0f, accuracy = 0.0f)
        XCTAssertEqual(1.0f, 2.0f, accuracy = -1.0f)
        XCTAssertEqual(1.0f, 1.0f, accuracy = Float.NaN)
        XCTAssertEqual(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, accuracy = 0.0f)
        XCTAssertEqual(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, accuracy = Float.POSITIVE_INFINITY)
        mustFail { XCTAssertEqual(Float.NaN, Float.NaN, accuracy = 0.0f) }
        mustFail { XCTAssertEqual(Float.NaN, 1.0f, accuracy = 1.0f) }
        mustFail { XCTAssertEqual(1.0f, 2.0f, accuracy = Float.NaN) }
        mustFail { XCTAssertEqual(Float.NaN, Float.NaN, accuracy = 0.0f, msg = "float accuracy") }
    }

    @org.junit.Test
    fun otherEqualityDelegatesAndNumericTypesStayDistinct() {
        class Value(val key: Int) {
            var comparisons = 0
            override fun equals(other: Any?): Boolean { comparisons += 1; return other is Value && key == other.key }
            override fun hashCode(): Int = key
        }
        val value = Value(1)
        expectEqual(value as Any?, Value(1) as Any?)
        org.junit.Assert.assertEquals(1, value.comparisons)
        mustFail { expectEqual(1 as Any?, 1.0 as Any?) }
        mustFail { expectEqual(1.0f as Any?, 1.0 as Any?) }
        expectEqual("same", "same")
        expectNotEqual("first", "second")
    }
}
