import { registerPlugin } from '@capacitor/core';
const CapacitorZebraPrinterBT = registerPlugin('CapacitorZebraPrinterBT', {
    web: () => import('./web').then(m => new m.CapacitorZebraPrinterBTWeb()),
});
export * from './definitions';
export { CapacitorZebraPrinterBT };
//# sourceMappingURL=index.js.map