package cuba.util.android.sec2.ru.meterusbconnectorlib.Connector;

/**
 * 2018
 * @author Медведев М.М
 */



import java.lang.reflect.Array;

public class CreateRequest {
    private byte[] addr = null;
    private byte commandCode;
    private byte[] params;
    private byte[] request;

    /**
     * Метод который из входных параметров создайт байт массив для отправки его на счетчик.
     *
     * @param addr        Сетевой адресс
     * @param commandCode код запроса(команды)
     * @param params      доп параметры
     * @return массив для отправки с CRC кодом.
     */
    public byte[] create(String addr, byte commandCode,  String... params) {
        String dirtyAddr = addr;
        dirtyAddr = dirtyAddr.replaceAll("\\D", "");
        try {
            this.addr = toByteArray(Long.parseLong(dirtyAddr));
        }catch (NumberFormatException e){
            return null;
        }
        this.commandCode = commandCode;
        this.params = convertParams(params);
        return buildRequest();
    }

    public byte[] create(byte[] addr, byte commandCode, byte... params) {
        this.addr = addr;
        this.commandCode = commandCode;
        this.params = params;
        return buildRequest();
    }


    /**
     * Типа оглавления основного класса.
     * @return массив для отправки с CRC кодом.
     */
    private byte[] buildRequest() {
        byte[] commandNoCrc = joinArray(addr, new byte[]{commandCode}, params);
        byte[] crc = revertCrc(new CRC().getByteArray(commandNoCrc, 0, commandNoCrc.length));
        byte[] result = joinArray(commandNoCrc, crc, null);
        bytesToHex(result);
        request = result;
        return result;
    }

    /**
     * Конвертирует байты в HEX требуется другим классом, лежит здесь
     * @param bytes
     * @return
     */
    public void bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        System.out.println(new String(hexChars));
    }


    private byte[] convertParams(String[] params) {
        if(commandCode == 00 ) {
            return toByteArray(Long.parseLong(params[0]));
        }
        //TODO реализовать конвертацию параметров
//        if(params == null) return new byte[]{};
        return null;
    }


    /**
     * Соединяет массивы
     */
    private byte[] joinArray(byte[] array, byte[] array2, byte[] array3) {
        int arr1Len = array.length;
        int arr2Len = array2.length;
        int arr3Len = 0;
        if (array3 != null) arr3Len = array3.length;

        @SuppressWarnings("unchecked")
        byte[] result = (byte[]) Array.newInstance(array.getClass().getComponentType(), arr1Len + arr2Len + arr3Len);
        System.arraycopy(array, 0, result, 0, arr1Len);
        System.arraycopy(array2, 0, result, arr1Len, arr2Len);
        if (array3 != null) System.arraycopy(array3, 0, result, arr1Len + arr2Len, arr3Len);
        return result;
    }

    /**
     * Конвертирует long в байты
     * @param src long срс
     * @return байты
     */
    private static byte[] toByteArray(long src) {
        return new byte[]{
                (byte) (src >>> 24),
                (byte) (src >>> 16),
                (byte) (src >>> 8),
                (byte) src
        };
    }

    /**
     * Поскольку срс формируется зеркалом, переворачиваем.
     */
    private byte[] revertCrc(byte[] byteArray) {
        return new byte[]{byteArray[1], byteArray[0]};
    }
}
