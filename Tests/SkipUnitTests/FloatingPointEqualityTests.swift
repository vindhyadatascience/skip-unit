// Copyright 2026 Vindhya Data Science
// SPDX-License-Identifier: MPL-2.0
import XCTest
import Testing

private func runtimeNaN(_ zero: Double) -> Double { zero / zero }
private func runtimeFloatNaN(_ zero: Float) -> Float { zero / zero }

final class FloatingPointEqualityTests: XCTestCase {
    func testExactIEEEAssertions() {
        let nan = runtimeNaN(0.0)
        XCTAssertEqual(-0.0, 0.0)
        XCTAssertNotEqual(nan, nan)
        let optionalNegative: Double? = -0.0
        let optionalPositive: Double? = 0.0
        let optionalNan: Double? = nan
        XCTAssertEqual(optionalNegative, optionalPositive)
        XCTAssertNotEqual(optionalNan, optionalNan)
        let floatNan = runtimeFloatNaN(Float(0.0))
        XCTAssertEqual(Float(-0.0), Float(0.0))
        XCTAssertNotEqual(floatNan, floatNan)
    }

    func testAccuracyReferencePassingCases() {
        let nan = runtimeNaN(0.0)
        let infinity = 1.0 / 0.0
        XCTAssertEqual(1.0, 2.0, accuracy: -1.0)
        XCTAssertEqual(1.0, 1.0, accuracy: nan)
        XCTAssertEqual(infinity, infinity, accuracy: 0.0)
        XCTAssertEqual(infinity, -infinity, accuracy: infinity)
    }
}

@Suite struct FloatingPointTestingTests {
    @Test func exactIEEEExpectAndRequire() throws {
        let notANumber = runtimeNaN(0.0)
        let negativeZero: Double = -0.0
        let positiveZero: Double = 0.0
        #expect(negativeZero == positiveZero)
        #expect(notANumber != notANumber)
        try #require(negativeZero == positiveZero)
        try #require(notANumber != notANumber)
        let floatProbeValue = runtimeFloatNaN(Float(0.0))
        #expect(Float(-0.0) == Float(0.0))
        #expect(floatProbeValue != floatProbeValue)
    }
}
