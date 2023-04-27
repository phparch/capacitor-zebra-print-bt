export interface CapacitorZebraPrinterBTPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
