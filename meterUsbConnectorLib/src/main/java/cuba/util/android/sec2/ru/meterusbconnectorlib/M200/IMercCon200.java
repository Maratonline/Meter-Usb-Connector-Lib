package cuba.util.android.sec2.ru.meterusbconnectorlib.M200;



/**
 *  2018
 * @author Медведев М.М
 */


public interface IMercCon200 {
    /**
     * Установить новый сетевой адрес счетчика по серийнику
     * @param serial Серийный номер счетчика
     * @return Результат выполнения
     */
    public Decryptor resetMercInetAddress( String serial);
    /**
     * Установить новый сетевой адрес счетчика. Основной метод проверки и установки
     * @param serial Серийный номер счетчика
     * @param appartment Квартира
     * @return Результат выполнения
     */
    public Decryptor resetMercInetAddress(String serial, String appartment);

    /**
     * Метод для ручного обнуления сетевого адреса по конкретному сетевому адресу
     */
    public Decryptor resetMercInetAddressManual(String inetAddress);
}
