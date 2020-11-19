package cuba.util.android.sec2.ru.meterusbconnectorlib.M200;



/**
 *  2018
 * @author Медведев М.М
 */



public class Decryptor {
    private byte[] bytes;
    private MeterAnswerStatus meterAnswerStatus;

    public Decryptor(){
        meterAnswerStatus = MeterAnswerStatus.NOT_CONNECTION;
    }

    public Decryptor(byte[] data) {
        this.bytes = data;
        if(bytes == null)meterAnswerStatus = MeterAnswerStatus.NOT_CONNECTION;
        else if(bytes.length == 11 && bytes[4] == (byte)0x2F ) meterAnswerStatus = MeterAnswerStatus.CORRECT;
        else meterAnswerStatus = MeterAnswerStatus.UNCORRECT;
    }

    /**
     * Получение серийника из ответа, с проверкой запроса.
     * @return -1 - если ответа нет, серийник - если все верно
     */
    public int getSerial() {
             return bytes[5] << 24 | (bytes[6] & 0xFF) << 16 | (bytes[7] & 0xFF) << 8 | (bytes[8] & 0xFF);
    }

    public byte[] getAddress(){
        return new byte[]{bytes[0], bytes[1], bytes[2], bytes[3]};
    }

    public byte[] getFullAnswer(){
        return bytes;
    }

    public MeterAnswerStatus getAnswerStatus(){
        return meterAnswerStatus;
    }
}
