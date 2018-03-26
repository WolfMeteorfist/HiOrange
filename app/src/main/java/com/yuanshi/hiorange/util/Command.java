package com.yuanshi.hiorange.util;

/**
 * Created by Administrator on 2018/2/5.
 */

public class Command {

    public static final String TYPE_READ_BOX = "01";
    public static final String TYPE_LOCK = "02";
    public static final String TYPE_VOICE = "05";
    //    public static final String TYPE_UPGRADE = "06";
//    public static final String TYPE_RUNNING_MODE = "07";
    public static final String TYPE_FINGER = "08";
    public static final String TYPE_BOX_INFO = "09";

    //VOICE
    public static final String VOICE_READ = "01";
    public static final String VOICE_SET = "02";

    public static String getCommand(String cmdType, String cmd, String dataLength, String data) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cmdType).append(cmd).append(dataLength);

        if (data.length() != 0) {
            stringBuilder.append(data);
        }

        byte[] bytes = stringBuilder.toString().getBytes();
        int crc16String = CheckCRCModBus(bytes, bytes.length);
        String crcByte = Integer.toHexString(crc16String);
        String lowByte = crcByte.substring(2, 4);
        String hightByte = crcByte.substring(0, 2);


        stringBuilder.append(lowByte).append(hightByte);

        stringBuilder.insert(0,"5555");

        return stringBuilder.toString();
    }


    private static String CRC16(byte[] Buf, int len) {
        int CRC;
        int i, j;
        CRC = 0xffff;
        for (i = 0; i < len; i++) {
            CRC = CRC ^ (Buf[i] & 0xff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x01) == 1) {
                    CRC = (CRC >> 1) ^ 0xA001;
                } else {
                    CRC = CRC >> 1;
                }
            }
        }

        return Integer.toHexString(CRC);
    }


    private  static int CalcCRCModBus(byte cDataIn, int wCRCIn)
    {
        int wCheck = 0;
        int i;

        wCRCIn = wCRCIn ^ cDataIn;


        for( i = 0; i < 8; i++)
        {
            wCheck = wCRCIn & 1;
            wCRCIn = wCRCIn >> 1;
            wCRCIn = wCRCIn & 0x7fff;

            if(wCheck == 1)
            {
                wCRCIn = wCRCIn ^ 0xa001;
            }
            wCRCIn = wCRCIn & 0xffff;
        }

        return wCRCIn;
    }

    public static int CheckCRCModBus(byte[] pDataIn, int iLenIn)
    {
        int wHi = 0;
        int wLo = 0;
        int wCRC;
        int i;

        wCRC = 0xFFFF;

        for (i = 0; i < iLenIn; i++)
        {
            wCRC = CalcCRCModBus(pDataIn[i], wCRC);
        }

        wHi = wCRC / 256;
        wLo = wCRC % 256;
        wCRC = (wHi << 8) | wLo;

        return wCRC;
    }

}
