import { WebPlugin } from '@capacitor/core';
import { ZebraDiscoveryResult, ZebraPrinterStatus, CapacitorZebraPrinterBTPlugin } from './definitions';


export class CapacitorZebraPrinterBTWeb extends WebPlugin implements CapacitorZebraPrinterBTPlugin {
  private printers = [
    {
      name: 'Test Printer 1',
      address: 'A1B2C3D4E5F607',
      manufacturer: 'TEST',
      modelNumber: 'TEST-1234',
      connected: false
    },
    {
      name: 'Test Printer 2',
      address: 'A1B2C3D4E5F608',
      manufacturer: 'TEST',
      modelNumber: 'TEST-1234',
      connected: false
    }
  ];
  private connectedPrinter: any = null;

  constructor() {
    super({
      name: 'ZebraCapacitorPlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async discover(): Promise<ZebraDiscoveryResult> {
    console.log("ZebraPluginWeb::discover");
    return {
      printers: this.printers
    };
  }

  async printerStatus(): Promise<ZebraPrinterStatus> {
    console.log("ZebraPluginWeb::printerStatus");
    return new Promise(resolve => setTimeout(() => {
      let status = {
        connected: false,
        isReadyToPrint: false,
        isPaused: false,
        isReceiveBufferFull: false,
        isRibbonOut: false,
        isPaperOut: false,
        isHeadTooHot: false,
        isHeadOpen: false,
        isHeadCold: false,
        isPartialFormatInProgress: false
      };
      if (this.connectedPrinter != null) {
        status.connected = true;
        status.isReadyToPrint = true;
      }
      resolve(status);
      return status;
    }, 500));
  }

  async print(options: { cpcl: string }): Promise<any> {
    console.log("ZebraPluginWeb::print", options.cpcl);
    return new Promise(resolve => setTimeout(() => {
      resolve(true);
    }, 1000));
  }

  async isConnected(): Promise<{ connected: boolean }> {
    return new Promise((resolve, _reject) => {
      console.log("ZebraPluginWeb::isConnected");

      resolve({ connected: this.connectedPrinter != null });
    });
  }

  async connect(options: { MACAddress: string }): Promise<{ success: boolean }> {
    console.log("ZebraPluginWeb::connect", options.MACAddress);

    return new Promise(resolve => setTimeout(() => {
      const printer = this.printers.find(p => p.address == options.MACAddress);
      if (printer != null) {
        printer.connected = true;

        // if we were previously connected to a printer, mark it as not connected
        if(this.connectedPrinter){
          this.connectedPrinter.connected = false;
        }

        // set our connectPrinter to this one
        this.connectedPrinter = printer;
        resolve({ success: true });
      };
      resolve({ success: false });
    }, 1000));
  }

  async disconnect(): Promise<void> {
    return new Promise((resolve, _reject) => {
      console.log("ZebraPluginWeb::disconnect");
      resolve();
    });
  }
}
