package cuba.util.android.sec2.ru.meterusbconnectorlib.M200;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;

public class UsbConect extends Activity {
    private final String TAG = "UsbConect";


    public void doSomething (Context context){
        // Get UsbManager from Android.

        UsbManager manager = (UsbManager) getSystemService(context.USB_SERVICE);
        Log.e(TAG, "THE METHOD BEGAN 1 ");

        // Find the first available driver.
        UsbSerialDriver driver = UsbSerialProber.acquire(manager);

        if (driver != null) {

            try {
                driver.open();
                driver.setBaudRate(115200);

                Log.e(TAG, "THE METHOD BEGAN 2");
                byte buffer[] = new byte[16];
                int numBytesRead = driver.read(buffer, 1000);
                Log.d(TAG, "Read " + numBytesRead + " bytes.");
            } catch (IOException e) {
                // Deal with error.
            } finally {
                try {
                    driver.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
