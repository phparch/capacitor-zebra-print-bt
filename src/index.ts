import { registerPlugin } from '@capacitor/core';

import type { CapacitorZebraPrinterBTPlugin } from './definitions';

const CapacitorZebraPrinterBT = registerPlugin<CapacitorZebraPrinterBTPlugin>('CapacitorZebraPrinterBT', {
  web: () => import('./web').then(m => new m.CapacitorZebraPrinterBTWeb()),
});

export * from './definitions';
export { CapacitorZebraPrinterBT };
