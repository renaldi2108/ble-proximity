import SwiftUI
import SharedApp

@main
struct iOSApp: App {

    init() {
        #if targetEnvironment(simulator)
        let allowSimulatedScanner = true
        #else
        let allowSimulatedScanner = false
        #endif
        IosEntryPointKt.startProximityKoin(allowSimulatedScanner: allowSimulatedScanner)
    }

    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.keyboard)
        }
    }
}
