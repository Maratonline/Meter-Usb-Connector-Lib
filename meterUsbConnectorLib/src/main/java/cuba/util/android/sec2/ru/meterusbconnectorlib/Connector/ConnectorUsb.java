package cuba.util.android.sec2.ru.meterusbconnectorlib.Connector;

/**
 *  2018
 * @author Медведев М.М
 */

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbManager;
import androidx.annotation.NonNull;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import static java.lang.Thread.sleep;

public class ConnectorUsb {
    private Activity activity;
    private String portNAME = "/dev/ttyS0";
    private final int DEFAULT_WAITING = 500;
    private final int DEFAULT_RETRY = 3;

    private final String TAG = "Connector";


    private UsbSerialDriver serialPort;



    public ConnectorUsb(Activity activity){
        this.activity = activity;
    }

    public ConnectorUsb(String portNAME){
        this.portNAME = portNAME;
    }

    /**
     * Основной метод для отправки запросов.
     * @param data сформированный запрос в байтах
     * @return Ответ в байтах.
     */
    public byte[] sendBytes(byte[] data){
        return sendBytes(data, DEFAULT_RETRY, DEFAULT_WAITING);
    }

    /**
     * Основной метод для отправки запросов с дополнительными данными.
     * @param data сформированный запрос в байтах
     * @param countRetry кол-во попыток отправки запроса
     * @param waitAnswer ожидание ответа в мс.
     * @return Ответ в байтах.
     */
    public byte[] sendBytes(byte[] data, int countRetry, int waitAnswer) {
        try {
                doSomething();
            if (serialPort != null ) {
                try {
                    return send(data, countRetry, waitAnswer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
            return null;
        } finally {
            closePort();
        }
        return null;
    }



    /**
     * Приватный метод открывающий соединения с предустановленными параметрами для Меркурия 200
     */
    public void doSomething (){
        // Get UsbManager from Android.

        UsbManager manager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        Log.e(TAG, "THE METHOD BEGAN 1 ");

        // Find the first available driver.
        serialPort = UsbSerialProber.acquire(manager);

        if (serialPort != null) {

            try {
                serialPort.open();
                serialPort.setBaudRate(9600);


                Log.e(TAG, "THE METHOD BEGAN 2");
                byte buffer[] = new byte[16];
                int numBytesRead = serialPort.read(buffer, 1000);
                Log.d(TAG, "Read " + numBytesRead + " bytes.");
            } catch (IOException e) {
                // Deal with error.
            }
        }
    }


    /**
     * Метод отправляющий байт запросы который так же возвращает ответ от счетчика
     * @param data байт запрос
     * @param countRetry кол-во попыток отправки запроса
     * @param waitAnswer ожидание ответа в мс
     * @return ответ от счетчика или NULL
     */
    private byte[] send(byte[] data, int countRetry, int waitAnswer) throws SerialPortException, IOException {
        int realRetry = 0;
        do {
            serialPort.write(data, waitAnswer);
            try {
                sleep(waitAnswer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte buffer[] = new byte[100];
            int numBytesRead = serialPort.read(buffer, waitAnswer);
            if(numBytesRead != 0 ) {
                byte[] endAnswer = new byte[numBytesRead];
                System.arraycopy(buffer,0,endAnswer,0, numBytesRead);
                return endAnswer;
            }
            realRetry++;
        } while (realRetry < countRetry);
        return null;
    }

    /**
     * Закрывает соединение.
     */
    private void closePort(){
        try {
            if (serialPort != null) serialPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){

            }
        }
    }
}

