import XCTest

final class ScanSmokeTest: XCTestCase {

    private var app: XCUIApplication!

    override func setUp() {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    func testScanningListsDevicesOpensRadarAndRecordsHistory() {
        let start = element("Mulai Pindai")
        XCTAssertTrue(start.waitForExistence(timeout: 20), "tombol Mulai Pindai tidak muncul")
        start.tap()

        let device = element("Galaxy Buds Pro")
        XCTAssertTrue(device.waitForExistence(timeout: 20), "tidak ada perangkat yang terdeteksi")
        capture(named: "scanner")
        device.tap()

        let back = element("Kembali")
        XCTAssertTrue(back.waitForExistence(timeout: 20), "layar radar tidak terbuka")
        capture(named: "tracker")
        back.tap()

        let history = element("Riwayat Perangkat")
        XCTAssertTrue(history.waitForExistence(timeout: 20), "tombol riwayat tidak muncul")
        history.tap()

        XCTAssertTrue(
            element("Galaxy Buds Pro").waitForExistence(timeout: 20),
            "riwayat tidak menyimpan perangkat yang terdeteksi",
        )
        capture(named: "history")
    }

    private func element(_ label: String) -> XCUIElement {
        app.descendants(matching: .any)[label].firstMatch
    }

    private func capture(named name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
