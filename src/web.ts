import { WebPlugin } from '@capacitor/core';

import type { CapacitorZebraPrinterBTPlugin } from './definitions';

export class CapacitorZebraPrinterBTWeb extends WebPlugin implements CapacitorZebraPrinterBTPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
