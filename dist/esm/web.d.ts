import { WebPlugin } from '@capacitor/core';
import { ZebraDiscoveryResult, ZebraPrinterStatus, CapacitorZebraPrinterBTPlugin } from './definitions';
export declare class CapacitorZebraPrinterBTWeb extends WebPlugin implements CapacitorZebraPrinterBTPlugin {
    private printers;
    private connectedPrinter;
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    discover(): Promise<ZebraDiscoveryResult>;
    printerStatus(): Promise<ZebraPrinterStatus>;
    print(options: {
        cpcl: string;
    }): Promise<any>;
    isConnected(): Promise<{
        connected: boolean;
    }>;
    connect(options: {
        MACAddress: string;
    }): Promise<{
        success: boolean;
    }>;
    disconnect(): Promise<void>;
}
