package com.matt.livefeed;

import android.app.Activity;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

public class ArduinoHookup extends Activity {

    int BAUD_RATE = 2400;
    Physicaloid physicaloid;

    public ArduinoHookup() {
        physicaloid = new Physicaloid(this);
        physicaloid.setBaudrate(BAUD_RATE);

        if(physicaloid.open()) { 	// tries to connect to device and if device was connected
            // read listener, When new data is received from Arduino add it to Text view
            physicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];
                    physicaloid.read(buf, size);
                }
            });

        } else {
            //Error while connecting
            Toast.makeText(this, "Cannot open", Toast.LENGTH_LONG).show();
        }
    }

}
