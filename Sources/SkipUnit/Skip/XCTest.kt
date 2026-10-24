// Copyright 2023–2026 Skip
// SPDX-License-Identifier: MPL-2.0
package skip.unit

/// Alias for the @Test annotation to be able to use it without importing JUnit
typealias Test = org.junit.Test

/// Alias for an org.junit.AssumptionViolatedException, which will skip a test in the same way as conditionally throwing XCTSkip
typealias XCTSkip = org.junit.AssumptionViolatedException

// Keep this boundary independent of SkipLib: SkipLib depends on SkipUnit for its tests.
// Boxed JVM Float/Double.equals distinguishes signed zeros and makes NaN reflexive;
// Swift Equatable uses IEEE equality even when assertions receive erased/optional values.
private fun assertionEquals(a: Any?, b: Any?): Boolean = when (a) {
    is Double -> b is Double && a.toDouble() == b.toDouble()
    is Float -> b is Float && a.toFloat() == b.toFloat()
    // JUnit's assertEquals(expected, actual), which this replaces, asks the EXPECTED value. Keep
    // that receiver: an array-literal expectation compares by contents against a Set or slice
    // result (RNABrowser-fiea.28: two GeneFox tests depended on it).
    else -> b == a
}

private fun assertEquality(a: Any?, b: Any?, equal: Boolean, message: (() -> String)? = null) {
    if (assertionEquals(a, b) != equal) {
        val diagnostic = message?.invoke() ?: if (equal) "expected:<$b> but was:<$a>" else "Values should be different. Actual: $a"
        org.junit.Assert.fail(diagnostic)
    }
}

private fun assertAccuracy(a: Double, b: Double, accuracy: Double, message: String? = null) {
    // Native XCTest accepts exact equality regardless of tolerance, and uses its magnitude.
    if (!(a == b || kotlin.math.abs(a - b) <= kotlin.math.abs(accuracy))) {
        org.junit.Assert.fail(message ?: "expected:<$b> but was:<$a> (accuracy <$accuracy>)")
    }
}

private fun assertAccuracy(a: Float, b: Float, accuracy: Float, message: String? = null) {
    if (!(a == b || kotlin.math.abs(a - b) <= kotlin.math.abs(accuracy))) {
        org.junit.Assert.fail(message ?: "expected:<$b> but was:<$a> (accuracy <$accuracy>)")
    }
}

/// Mimics the API of `XCTest` for a JUnit 4 test.
///
/// Behavior difference: JUnit `assert*` thows an exception, but `XCTAssert*` just reports the failure and continues
/// NOTE: the parameter order of JUnit 5's `org.junit.jupiter.api.Assertions` is the reverse of JUnit 4's `org.junit.Assert`
interface XCTestCase {

	@org.junit.Before
	fun beforeTest() {
		setUp()
	}

	/// The equivalent to XCTestCase setUp(), meant to be overridden in subclasses.
	fun setUp() {
	}

	@org.junit.After
	fun afterTest() {
		tearDown()
	}

	/// The equivalent to XCTestCase tearDown(), meant to be overridden in subclasses.
	fun tearDown() {
	}

    fun XCTFail(): Unit = org.junit.Assert.fail()

    fun XCTFail(msg: String): Unit = org.junit.Assert.fail(msg)

    fun <T> XCTUnwrap (ob: T?): T { org.junit.Assert.assertNotNull(ob); if (ob != null) { return ob }; throw AssertionError() }
    fun <T> XCTUnwrap(ob: T?, msg: String): T { org.junit.Assert.assertNotNull(msg, ob); if (ob != null) { return ob }; throw AssertionError() }

    fun XCTAssert(a: Boolean): Unit = org.junit.Assert.assertTrue(a)
    fun XCTAssert(a: Boolean, msg: String): Unit = org.junit.Assert.assertTrue(msg, a)
    fun XCTAssertTrue(a: Boolean): Unit = org.junit.Assert.assertTrue(a)
    fun XCTAssertTrue(a: Boolean, msg: String): Unit = org.junit.Assert.assertTrue(msg, a)
    fun XCTAssertFalse(a: Boolean): Unit = org.junit.Assert.assertFalse(a)
    fun XCTAssertFalse(a: Boolean, msg: String): Unit = org.junit.Assert.assertFalse(msg, a)

    fun XCTAssertNil(a: Any?): Unit = org.junit.Assert.assertNull(a)
    fun XCTAssertNil(a: Any?, msg: String): Unit = org.junit.Assert.assertNull(msg, a)
	fun <T> XCTAssertNotNil(a: T?): T { org.junit.Assert.assertNotNull(a); return a!! }
	fun <T> XCTAssertNotNil(a: T?, msg: String): T { org.junit.Assert.assertNotNull(msg, a); return a!! }

    fun XCTAssertIdentical(a: Any?, b: Any?): Unit = org.junit.Assert.assertSame(b, a)
    fun XCTAssertIdentical(a: Any?, b: Any?, msg: String): Unit = org.junit.Assert.assertSame(msg, b, a)
    fun XCTAssertNotIdentical(a: Any?, b: Any?): Unit = org.junit.Assert.assertNotSame(b, a)
    fun XCTAssertNotIdentical(a: Any?, b: Any?, msg: String): Unit = org.junit.Assert.assertNotSame(msg, b, a)

    fun XCTAssertEqual(a: Any?, b: Any?): Unit = assertEquality(a, b, true)
    fun XCTAssertEqual(a: Any?, b: Any?, msg: String): Unit = assertEquality(a, b, true) { "${a} is not equal to ${b} – " + msg }

    fun XCTAssertNotEqual(a: Any?, b: Any?): Unit = assertEquality(a, b, false)
    fun XCTAssertNotEqual(a: Any?, b: Any?, msg: String): Unit = assertEquality(a, b, false) { msg }

	fun <T : Comparable<T>> XCTAssertEqual(a: T?, b: T?): Unit = assertEquality(a, b, true) { "${a} != ${b}" }
	fun <T : Comparable<T>> XCTAssertEqual(a: T?, b: T?, msg: String): Unit = assertEquality(a, b, true) { msg }
	fun <T : Comparable<T>> XCTAssertNotEqual(a: T?, b: T?): Unit = assertEquality(a, b, false) { "${a} == ${b}" }
	fun <T : Comparable<T>> XCTAssertNotEqual(a: T?, b: T?, msg: String): Unit = assertEquality(a, b, false) { msg }

	fun XCTAssertEqual(a: Double, b: Double, accuracy: Double): Unit = assertAccuracy(a, b, accuracy)
	fun XCTAssertEqual(a: Double, b: Double, accuracy: Double, msg: String): Unit = assertAccuracy(a, b, accuracy, msg)

	fun XCTAssertEqual(a: Float, b: Float, accuracy: Float): Unit = assertAccuracy(a, b, accuracy)
	fun XCTAssertEqual(a: Float, b: Float, accuracy: Float, msg: String): Unit = assertAccuracy(a, b, accuracy, msg)

    fun <T : Comparable<T>> XCTAssertGreaterThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !> ${b}", a > b)
    fun <T : Comparable<T>> XCTAssertGreaterThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a > b)
    fun <T : Comparable<T>> XCTAssertGreaterThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !>= ${b}", a >= b)
    fun <T : Comparable<T>> XCTAssertGreaterThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a >= b)

    fun <T : Comparable<T>> XCTAssertLessThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !< ${b}", a < b)
    fun <T : Comparable<T>> XCTAssertLessThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a < b)
    fun <T : Comparable<T>> XCTAssertLessThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !<= ${b}", a <= b)
    fun <T : Comparable<T>> XCTAssertLessThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a <= b)

    // MARK: Additional overloads needed for XCTAssert*() which have different signatures on Linux (@autoclosures) than on Darwin platforms (direct values)

    fun XCTUnwrap(ob: () -> Any?) = { val x = ob(); org.junit.Assert.assertNotNull(x); x }
    fun XCTUnwrap(ob: () -> Any?, msg: () -> String) = { val x = ob(); org.junit.Assert.assertNotNull(msg(), x); x }

    fun XCTAssertTrue(a: () -> Boolean) = org.junit.Assert.assertTrue(a())
    fun XCTAssertTrue(a: () -> Boolean, msg: () -> String) = org.junit.Assert.assertTrue(msg(), a())
    fun XCTAssertFalse(a: () -> Boolean) = org.junit.Assert.assertFalse(a())
    fun XCTAssertFalse(a: () -> Boolean, msg: () -> String) = org.junit.Assert.assertFalse(msg(), a())

    fun XCTAssertNil(a: () -> Any?) = org.junit.Assert.assertNull(a())
    fun XCTAssertNil(a: () -> Any?, msg: () -> String) = org.junit.Assert.assertNull(msg(), a())
    fun XCTAssertNotNil(a: () -> Any?) = org.junit.Assert.assertNotNull(a())
    fun XCTAssertNotNil(a: () -> Any?, msg: () -> String) = org.junit.Assert.assertNotNull(msg(), a())

    fun XCTAssertIdentical(a: () -> Any?, b: () -> Any?) = org.junit.Assert.assertSame(b(), a())
    fun XCTAssertIdentical(a: () -> Any?, b: () -> Any?, msg: () -> String) = org.junit.Assert.assertSame(msg(), b(), a())
    fun XCTAssertNotIdentical(a: () -> Any?, b: () -> Any?) = org.junit.Assert.assertNotSame(b(), a())
    fun XCTAssertNotIdentical(a: () -> Any?, b: () -> Any?, msg: () -> String) = org.junit.Assert.assertNotSame(msg(), b(), a())

    fun XCTAssertEqual(a: () -> Any?, b: () -> Any?) {
        val left = a()
        val right = b()
        assertEquality(left, right, true)
    }
    fun XCTAssertEqual(a: () -> Any?, b: () -> Any?, msg: () -> String) {
        val left = a()
        val right = b()
        assertEquality(left, right, true, msg)
    }
    fun XCTAssertNotEqual(a: () -> Any?, b: () -> Any?) {
        val left = a()
        val right = b()
        assertEquality(left, right, false)
    }
    fun XCTAssertNotEqual(a: () -> Any?, b: () -> Any?, msg: () -> String) {
        val left = a()
        val right = b()
        assertEquality(left, right, false, msg)
    }

	fun measure(block: () -> Unit) = block()
}

// MARK: Swift Testing assertion functions
// These are the targets for transpiled #expect() and #require() macros.

fun expectTrue(a: Boolean): Unit = org.junit.Assert.assertTrue(a)
fun expectTrue(a: Boolean, msg: String): Unit = org.junit.Assert.assertTrue(msg, a)
fun expectFalse(a: Boolean): Unit = org.junit.Assert.assertFalse(a)
fun expectFalse(a: Boolean, msg: String): Unit = org.junit.Assert.assertFalse(msg, a)

fun expectEqual(a: Any?, b: Any?): Unit = assertEquality(a, b, true)
fun expectEqual(a: Any?, b: Any?, msg: String): Unit = assertEquality(a, b, true) { "${a} is not equal to ${b} – " + msg }
fun <T : Comparable<T>> expectEqual(a: T?, b: T?): Unit = assertEquality(a, b, true) { "${a} != ${b}" }
fun <T : Comparable<T>> expectEqual(a: T?, b: T?, msg: String): Unit = assertEquality(a, b, true) { msg }

fun expectNotEqual(a: Any?, b: Any?): Unit = assertEquality(a, b, false)
fun expectNotEqual(a: Any?, b: Any?, msg: String): Unit = assertEquality(a, b, false) { msg }
fun <T : Comparable<T>> expectNotEqual(a: T?, b: T?): Unit = assertEquality(a, b, false) { "${a} == ${b}" }
fun <T : Comparable<T>> expectNotEqual(a: T?, b: T?, msg: String): Unit = assertEquality(a, b, false) { msg }

fun <T : Comparable<T>> expectGreaterThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !> ${b}", a > b)
fun <T : Comparable<T>> expectGreaterThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a > b)
fun <T : Comparable<T>> expectGreaterThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !>= ${b}", a >= b)
fun <T : Comparable<T>> expectGreaterThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a >= b)

fun <T : Comparable<T>> expectLessThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !< ${b}", a < b)
fun <T : Comparable<T>> expectLessThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a < b)
fun <T : Comparable<T>> expectLessThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !<= ${b}", a <= b)
fun <T : Comparable<T>> expectLessThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a <= b)

fun expectNil(a: Any?): Unit = org.junit.Assert.assertNull(a)
fun expectNil(a: Any?, msg: String): Unit = org.junit.Assert.assertNull(msg, a)
fun <T> expectNotNil(a: T?): T { org.junit.Assert.assertNotNull(a); return a!! }
fun <T> expectNotNil(a: T?, msg: String): T { org.junit.Assert.assertNotNull(msg, a); return a!! }

fun <T : Throwable> expectThrows(`throws`: kotlin.reflect.KClass<T>, block: () -> Unit) {
    try {
        block()
        org.junit.Assert.fail("Expected ${`throws`.simpleName} to be thrown")
    } catch (e: Throwable) {
        if (!`throws`.isInstance(e)) {
            org.junit.Assert.fail("Expected ${`throws`.simpleName} but got ${e::class.simpleName}: ${e.message}")
        }
    }
}

fun requireTrue(a: Boolean): Unit = org.junit.Assert.assertTrue(a)
fun requireTrue(a: Boolean, msg: String): Unit = org.junit.Assert.assertTrue(msg, a)

fun requireEqual(a: Any?, b: Any?): Unit = assertEquality(a, b, true)
fun requireEqual(a: Any?, b: Any?, msg: String): Unit = assertEquality(a, b, true) { "${a} is not equal to ${b} – " + msg }
fun <T : Comparable<T>> requireEqual(a: T?, b: T?): Unit = assertEquality(a, b, true) { "${a} != ${b}" }
fun <T : Comparable<T>> requireEqual(a: T?, b: T?, msg: String): Unit = assertEquality(a, b, true) { msg }

fun requireNotEqual(a: Any?, b: Any?): Unit = assertEquality(a, b, false)
fun requireNotEqual(a: Any?, b: Any?, msg: String): Unit = assertEquality(a, b, false) { msg }

fun <T : Comparable<T>> requireGreaterThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !> ${b}", a > b)
fun <T : Comparable<T>> requireGreaterThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a > b)
fun <T : Comparable<T>> requireGreaterThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !>= ${b}", a >= b)
fun <T : Comparable<T>> requireGreaterThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a >= b)
fun <T : Comparable<T>> requireLessThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !< ${b}", a < b)
fun <T : Comparable<T>> requireLessThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a < b)
fun <T : Comparable<T>> requireLessThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !<= ${b}", a <= b)
fun <T : Comparable<T>> requireLessThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a <= b)

fun <T> requireNotNil(a: T?): T { org.junit.Assert.assertNotNull(a); return a!! }
fun <T> requireNotNil(a: T?, msg: String): T { org.junit.Assert.assertNotNull(msg, a); return a!! }

fun <T : Throwable> requireThrows(`throws`: kotlin.reflect.KClass<T>, block: () -> Unit) {
    expectThrows(`throws` = `throws`, block = block)
}
