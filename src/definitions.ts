export interface ZebraPrinter {
  name: string;
  address: string;
  manufacturer: string;
  modelNumber: string;
  connected: boolean;
}

export interface ZebraDiscoveryResult {
  printers: Array<ZebraPrinter>;
}

export interface ZebraPrinterStatus{
  connected:boolean;
  isReadyToPrint?: boolean;
  isPaused?: boolean;
  isReceiveBufferFull?: boolean;
  isRibbonOut?: boolean;
  isPaperOut?: boolean;
  isHeadTooHot?: boolean;
  isHeadOpen?: boolean;
  isHeadCold?: boolean;
  isPartialFormatInProgress?: boolean;
}

export interface CapacitorZebraPrinterBTPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  discover(options: any): Promise<ZebraDiscoveryResult>;
  printerStatus(): Promise<ZebraPrinterStatus>;
  print(options: { cpcl: string }): Promise<void>;
  isConnected(): Promise<{ connected: boolean }>;
  connect(options: { MACAddress: string }): Promise<{ success: boolean }>;
  disconnect(): Promise<void>;
}
