import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorZebraPrinterBTPlugin)
public class CapacitorZebraPrinterBTPlugin: CAPPlugin {
    private let implementation = CapacitorZebraPrinterBT()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
