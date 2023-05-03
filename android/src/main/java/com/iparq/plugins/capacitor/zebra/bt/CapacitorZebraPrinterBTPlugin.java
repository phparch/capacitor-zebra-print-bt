package com.iparq.plugins.capacitor.zebra.bt;

import com.getcapacitor.JSObject;
import com.getcapacitor.JSArray;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PermissionState;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import org.json.JSONArray;

import java.util.Iterator;
import java.util.Set;

@CapacitorPlugin(
    name = "CapacitorZebraPrinterBT",
    permissions = {
        @Permission(alias = "bluetooth", strings = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.BLUETOOTH_CONNECT",
            "android.permission.BLUETOOTH_SCAN",
        }),
    }
)
public class CapacitorZebraPrinterBTPlugin extends Plugin {
    private Connection printerConnection;
    private com.zebra.sdk.printer.ZebraPrinter printer;
    private String macAddress;

    private CapacitorZebraPrinterBT implementation = new CapacitorZebraPrinterBT();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod()
    public void print(PluginCall call) {
        String message = call.getString("cpcl");
        if (!isConnected()) {
            call.error("Printer Not Connected");
        } else {
            if (this.printCPCL(message)) {
                call.resolve();
            } else {
                call.reject("Print error");
            }
        }
    }

    @PluginMethod()
    public void isConnected(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("connected", this.isConnected());
        call.resolve(ret);
    }

    @PluginMethod()
    public void connect(PluginCall call) {
        if (getPermissionState("bluetooth") != PermissionState.GRANTED) {
            requestAllPermissions(call, "bluetoothConnectPermissionCallback");
        } else {
            String address = call.getString("MACAddress");
            com.zebra.sdk.printer.ZebraPrinter printer = this.connect(address);
            JSObject ret = new JSObject();
            ret.put("success", printer != null);
            call.resolve(ret);
        }
    }

    @PermissionCallback
    private void bluetoothConnectPermissionCallback(PluginCall call) {
        if (getPermissionState("bluetooth") == PermissionState.GRANTED) {
            connect(call);
        } else {
            call.reject("Permission is required to print");
        }
    }

    @PluginMethod()
    public void printerStatus(PluginCall call){
        String address = call.getString("MACAddress");
        JSObject ret = new JSObject();
        if(this.macAddress == macAddress && this.isConnected()){
            try{
                PrinterStatus status = printer.getCurrentStatus();
                ret.put("isReadyToPrint", status.isReadyToPrint);
                ret.put("isPaused", status.isPaused);
                ret.put("isReceiveBufferFull", status.isReceiveBufferFull);
                ret.put("isRibbonOut", status.isRibbonOut);
                ret.put("isPaperOut", status.isPaperOut);
                ret.put("isHeadTooHot", status.isHeadTooHot);
                ret.put("isHeadOpen", status.isHeadOpen);
                ret.put("isHeadCold", status.isHeadCold);
                ret.put("isPartialFormatInProgress", status.isPartialFormatInProgress);
            }catch(Exception ex){
                call.errorCallback(ex.getMessage());
            }
            ret.put("connected", true);
        }else{
            ret.put("connected", false);
        }
        call.resolve(ret);
    }

    @PluginMethod()
    public void disconnect(PluginCall call) {
        disconnect();

        call.resolve();
    }

    @PluginMethod()
    public void discover(PluginCall call){
        if (getPermissionState("bluetooth") != PermissionState.GRANTED) {
            requestAllPermissions(call, "bluetoothDiscoverPermissionCallback");
        } else {
            JSArray printers = this.NonZebraDiscovery();
            JSObject ret = new JSObject();
            ret.put("printers", printers);
            call.resolve(ret);
        }
    }

    @PermissionCallback
    private void bluetoothDiscoverPermissionCallback(PluginCall call) {
        if (getPermissionState("bluetooth") == PermissionState.GRANTED) {
            discover(call);
        } else {
            call.reject("Permission is required to print");
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if( isConnected()) disconnect();
    }

    private boolean printCPCL(String cpcl)
    {
        try {
            if (!isConnected()) {
                Log.v("EMO", "Printer Not Connected");

                return false;
            }

            byte[] configLabel = cpcl.getBytes();
            printerConnection.write(configLabel);

            if (printerConnection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
                System.out.println(friendlyName);
            }
        } catch (ConnectionException e) {
            Log.v("EMO", "Error Printing", e);

            return false;
        }

        return true;
    }

    private boolean isConnected(){
        return printerConnection != null && printerConnection.isConnected();
    }

    private synchronized com.zebra.sdk.printer.ZebraPrinter connect(String macAddress) {
        if( isConnected()) disconnect();

        printerConnection = null;
        this.macAddress = macAddress;
        printerConnection = new BluetoothConnection(macAddress);
        try {
            printerConnection.open();
        }

        catch (ConnectionException e) {
            Log.v("EMO", "Printer - Failed to open connection", e);
            disconnect();
        }
        printer = null;
        if (printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
            } catch (ConnectionException e) {
                Log.v("EMO", "Printer - Error...", e);
                printer = null;
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                Log.v("EMO", "Printer - Unknown Printer Language", e);
                printer = null;
                disconnect();
            }
        }
        return printer;
    }

    private synchronized void disconnect() {
        try {
            if (printerConnection != null) {
                printerConnection.close();
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    private JSArray NonZebraDiscovery(){
        JSArray printers = new JSArray();

        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> devices = adapter.getBondedDevices();

            JSONArray array = new JSONArray();

            for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext(); ) {
                BluetoothDevice device = it.next();
                String name = device.getName();
                String mac = device.getAddress();

                JSObject p = new JSObject();
                p.put("name",name);
                p.put("address", mac);
                printers.put(p);

            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return  printers;
    }
}
