// Copyright 2026 Vindhya Data Science
// SPDX-License-Identifier: MPL-2.0
import XCTest

private final class UnwrapProbe {
    let text: String
    init(text: String) { self.text = text }
}

final class XCTUnwrapMetadataTests: XCTestCase {
    func testUnwrapPreservesConcreteReferenceAndEvaluatesExpressionOnce() throws {
        let expected = UnwrapProbe(text: "retained")
        var evaluations = 0
        func candidate() -> UnwrapProbe? {
            evaluations += 1
            return expected
        }
        let actual = try XCTUnwrap(candidate())
        XCTAssertTrue(actual === expected)
        XCTAssertEqual(actual.text, "retained")
        XCTAssertEqual(evaluations, 1)
        let nullable: UnwrapProbe? = expected
        let withMessage = try XCTUnwrap(nullable, "A present concrete probe must unwrap")
        XCTAssertTrue(withMessage === expected)
        XCTAssertEqual(withMessage.text, "retained")
    }
}
