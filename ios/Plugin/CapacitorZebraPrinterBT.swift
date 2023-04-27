import Foundation

@objc public class CapacitorZebraPrinterBT: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
