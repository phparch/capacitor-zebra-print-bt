package com.iparq.plugins.capacitor.zebra.bt;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CapacitorZebraPrinterBT")
public class CapacitorZebraPrinterBTPlugin extends Plugin {

    private CapacitorZebraPrinterBT implementation = new CapacitorZebraPrinterBT();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
}
