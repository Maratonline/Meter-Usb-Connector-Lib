package cuba.util.android.sec2.ru.meterusbconnectorlib.M200;

/**
 * 2018
 *
 * @author Медведев М.М
 */

import android.app.Activity;
import cuba.util.android.sec2.ru.meterusbconnectorlib.Connector.*;

public class CommandLogic implements IMercCon200 {
    private Activity activity;

    public CommandLogic(Activity activity) {
        this.activity = activity;
    }

    /**
     * Обнуление сетевого адреса счетчика по серийному номеру.
     *
     * @param serial Серийный номер счетчика
     * @return Серийный номер счетчика. Null если запрос завершился неудачей.
     */
    @Override
    public Decryptor resetMercInetAddress(String serial) {
        if (serial != null && serial.length() > 6)
            return setNewInetAddress(serial.substring(serial.length() - 6));
        else return new Decryptor();
    }

    /**
     * Основная реализация поиска счетчика по серийнику или номеру квартиры. Проходится по 4 этапам возможного поиска.
     *
     * @param serial     Серийный номер счетчика
     * @param appartment Квартира
     * @return Серийный номер счетчика. Null если запрос завершился неудачей.
     */
    @Override
    public Decryptor resetMercInetAddress(String serial, String appartment) {
        try {
            // STAGE 1  пробуем найти по серийнику если нашли устанавливаем новый адрес
            Decryptor endAndwer = resetMercInetAddress(serial);
            if (!endAndwer.getAnswerStatus().equals(MeterAnswerStatus.NOT_CONNECTION))
                return endAndwer;
            // STAGE 2 пробуем найти по формуле вывода из квартиры
            if (appartment != null && !appartment.equals("")) {
                endAndwer = setNewInetAddress(appartmentChecker(appartment));
                if (!endAndwer.getAnswerStatus().equals(MeterAnswerStatus.NOT_CONNECTION))
                    return endAndwer;
                // STAGE 3 Запрашиваем счетчик по квартире если мы его нашли, тогда устанавливаем новый адрес
                endAndwer = setNewInetAddress(appartment);
                if (!endAndwer.getAnswerStatus().equals(MeterAnswerStatus.NOT_CONNECTION))
                    return endAndwer;
            }
            // STAGE 4 kv0
            endAndwer = setNewInetAddress("0");
            if (!endAndwer.getAnswerStatus().equals(MeterAnswerStatus.NOT_CONNECTION))
                return endAndwer;
            endAndwer = setNewInetAddress(appartmentChecker("0"));
            if (!endAndwer.getAnswerStatus().equals(MeterAnswerStatus.NOT_CONNECTION))
                return endAndwer;
        } catch (Exception e) {
            e.printStackTrace();
            return new Decryptor();
        }
        return new Decryptor();
    }


    /**
     * Метод для ручного запроса, передача сетевого адреса напрямую.
     *
     * @param inetAddress Сетевой адрес
     * @return Серийный номер счетчика. Null если запрос завершился неудачей.
     */
    @Override
    public Decryptor resetMercInetAddressManual(String inetAddress) {
        Decryptor answer = setNewInetAddress(inetAddress);
        if (!answer.getAnswerStatus().equals(MeterAnswerStatus.NOT_CONNECTION)) return answer;
        else answer = setNewInetAddress(appartmentChecker(inetAddress));
        return answer;
    }

    /**
     * Метод меняет сетевой адрес на 0, проверяя закрепился ли результат.
     *
     * @param inetAddress сетевой адрес устройства
     * @return Серийник, -1 если ответа нет.
     */
    private Decryptor setNewInetAddress(String inetAddress) {
        if (inetAddress != null && !inetAddress.equals("")) {
            byte[] request = new CreateRequest().create(inetAddress, (byte) 0x00, "0");
            byte[] answerByte = null;
            if (request != null && request.length > 0)
                answerByte = new ConnectorUsb(activity).sendBytes(request);
            if (answerByte == null) return new Decryptor();
            else
                return new Decryptor(new ConnectorUsb(activity).sendBytes(new CreateRequest().create("0", (byte) 0x2F), 3, 1000));
        } else return new Decryptor();
    }

    /**
     * Конвертация квартиры в его "серийник" если передана квартира
     *
     * @param appartment квартира
     * @return Сконвертированный вариант.
     */

    private String appartmentChecker(String appartment) {
        if (appartment != null && !appartment.equals("")) {
            long appartmentInt;
            try {
                String dirtyAddr = appartment;
                dirtyAddr = dirtyAddr.replaceAll("\\D", "");
                appartmentInt = Long.parseLong(dirtyAddr);
            } catch (NumberFormatException e) {
                return null;
            }
            if (appartmentInt < 10000) {
                return String.valueOf(appartmentInt * 8 + 4194304003L);
            }
            return null;
        } else return null;
    }
}
