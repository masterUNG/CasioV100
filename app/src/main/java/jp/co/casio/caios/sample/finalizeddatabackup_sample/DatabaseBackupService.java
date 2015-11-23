package jp.co.casio.caios.sample.finalizeddatabackup_sample;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import jp.co.casio.caios.framework.device.SerialCom;
import jp.co.casio.vx.framework.device.lineprintertools.Encoding;

public class DatabaseBackupService extends IntentService {

    //My Explicit
    private String myQTY;
    private String myCONSECNUMBER, itemName, myUnitPrice, strITEMTYPE, myintFunc, myarrFunc;
    private int intCount,countQty=0, intStartCount = 0, intITEMNAMEcount, intConsecnumcount,
    //*****------Point for Change Max Value ------*****
    maxFontName = 30,           //จำนวน Character ของชื่อสินค้าหลัก
            lenghtChaCondiment = 25,    //จำนวน Character ของชื่อ Condiment
            maxCondiment = 6,           //จำนวน Condiment ต่อหนึ่ง Label
            condimentPerLine = 2;       //จำนวน Condiment ต่อหนึ่งบรรทัด
    //-------------------------------------------------
    private int[] arrayITEMTYPE;
    private String[] ITEMNAMEStrings;
    private int[] arrayQTY;
    //private String[] arrayQTYnotNull;

    SerialCom com = new SerialCom();

    private static String TAG = "DatabaseBackupService";
    private static String myTAG = "Master";
    // Database provider.
    private final static String PROVIDER = "jp.co.casio.caios.framework.database";

    // This application create & insert to this sample DB file.
    // This file has two table, same as CST004 and CST005.
    private final String SAMPL_DBNAME = "SALESWORK_SAMPLE.DB";
    private final String SAMPL_DBFOLDER = "/sample";


    private String[] strItemName;

    private ByteArrayOutputStream data;

    private static final String SQLCMD_CREATE_TMP_CST005 =
            "CREATE TABLE CST005 ("
                    + "	TERMINALNUMBER	VARCHAR	(2)	NOT NULL,"
                    + "	BIZDATE		VARCHAR	(8)	NOT NULL,"
                    + "	CONSECNUMBER	VARCHAR	(6)	NOT NULL,"
                    + "	LINENUMBER	VARCHAR	(3)	NOT NULL,"
                    + "	CLKCODE		VARCHAR	(10)	NOT NULL,"
                    + "	CLKNAME		VARCHAR	(24)	NOT NULL,"
                    + "	DTLMODE		VARCHAR	(1)	NOT NULL,"
                    + "	DTLTYPE		VARCHAR	(1)	NOT NULL,"
                    + "	DTLFUNC		VARCHAR	(2)	NOT NULL,"
                    + "	ITEMCODE	VARCHAR	(16),"
                    + "	ITEMNAME	VARCHAR	(24),"
                    + "	FUNCKEYCODE	VARCHAR	(6),"
                    + "	FUNCKEYNAME	VARCHAR	(24),"
                    + "	FUNCTIONCODE	VARCHAR	(6),"
                    + "	FUNCTIONNAME	VARCHAR	(24),"
                    + "	SCANCODE1	VARCHAR	(16),"
                    + "	SCANCODE2	VARCHAR	(16),"
                    + "	ITEMRELCODE	VARCHAR	(13),"
                    + "	ITEMGRPCODE	VARCHAR	(6),"
                    + "	ITEMGRPNAME	VARCHAR	(24),"
                    + "	ITEMDEPTCODE	VARCHAR	(6),"
                    + "	ITEMDEPTNAME	VARCHAR	(24),"
                    + "	ITEMTYPE	VARCHAR	(1),"
                    + "	ITEMCLASSCODE	VARCHAR	(6),"
                    + "	SELECTIVEITEM1	VARCHAR	(1),"
                    + "	SELECTIVEITEM2	VARCHAR	(1),"
                    + "	SELECTIVEITEM3	VARCHAR	(1),"
                    + "	SELECTIVEITEM4	VARCHAR	(1),"
                    + "	SELECTIVEITEM5	VARCHAR	(1),"
                    + "	SELECTIVEITEM6	VARCHAR	(1),"
                    + "	SELECTIVEITEM7	VARCHAR	(1),"
                    + "	SELECTIVEITEM8	VARCHAR	(1),"
                    + "	SELECTIVEITEM9	VARCHAR	(1),"
                    + "	SELECTIVEITEM10	VARCHAR	(1),"
                    + "	UNITQTY		NUMERIC	(10,4)	DEFAULT 0  NOT NULL,"
                    + "	UNITPRICELINK	VARCHAR	(2),"
                    + "	WEIGHT		NUMERIC	(10,4)	DEFAULT 0  NOT NULL,"
                    + "	QTY		NUMERIC	(10,4)	DEFAULT 0  NOT NULL,"
                    + "	UNITPRICE	NUMERIC	(13)	DEFAULT 0  NOT NULL,"
                    + "	AMT		NUMERIC	(13)	DEFAULT 0  NOT NULL,"
                    + "	TAXCODE1	VARCHAR	(2),"
                    + "	TAXCODE2	VARCHAR	(2),"
                    + "	TAXCODE3	VARCHAR	(2),"
                    + "	TAXCODE4	VARCHAR	(2),"
                    + "	TAXCODE5	VARCHAR	(2),"
                    + "	PROFITAMT	NUMERIC	(14,4)	DEFAULT 0  NOT NULL,"
                    + "	MAXCASHCOUNT	NUMERIC	(3)	DEFAULT 0  NOT NULL,"
                    + "	CRECOMPCODE	VARCHAR	(10),"
                    + "	LINKLINENUMBER	VARCHAR	(3),"
                    + "	ITEMORDERDATE	VARCHAR	(8),"
                    + "	ITEMORDERTIME	VARCHAR	(6),"
                    + "	NBCHKLINE	VARCHAR	(3),"
                    + "	RELINVOICELINE	VARCHAR	(3),"
                    + "	ZCOUNTER        VARCHAR (6),"
                    + "	REMARKS		VARCHAR (30),"
                    + "	CREATEDATETIME	VARCHAR (14)	NOT NULL,"
                    + "	UPDATEDATETIME	VARCHAR (14)	NOT NULL,"
                    + ""
                    + "	CONSTRAINT CST005_PRIMARY PRIMARY KEY ("
                    + "		TERMINALNUMBER,"
                    + "		BIZDATE,"
                    + "		CONSECNUMBER,"
                    + "		LINENUMBER,"
                    + "		CREATEDATETIME"
                    + "		)"
                    + "	);";


    public DatabaseBackupService() {
        super("DatabaseBackup");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //consecNumber คือ เลขกำกับของบิล
        String consecNumber = intent.getStringExtra(BroadcastReceiver.EXTRA_CONSECNUMBER);
        String selection = String.format("CONSECNUMBER='%s'", consecNumber);
        copySALESWORKcst005("CST005", selection, "LINENUMBER ASC", SQLCMD_CREATE_TMP_CST005);

    }   // Handler Initen

    private boolean copySALESWORKcst005(String tableName, String selection, String sortOrder, String createSQL) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority(PROVIDER);
        builder.appendPath("SALESWORK");
        builder.appendPath(tableName);
        Uri uri = builder.build();

        //สร้าง Cursor จาก Consecnumber ล่าสุด
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(uri, null, selection, null, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }   // try
        if (cursor == null) {
            return false;
        }   // if

        //เร่ิมนับ จำนวน Record บน Cursor
        int count = cursor.getCount();
        //cursor เริ่มจาก Record บนสุด
        cursor.moveToFirst();
        //ทดสอบหา จำนวน ItemName
        intConsecnumcount = 0;

        for (int i = 0; i < count; i++) {

            String strMyITEMNAME = cursor.getString(cursor.getColumnIndex("CONSECNUMBER"));

            if (strMyITEMNAME != null) {
                intConsecnumcount += 1;
            }
            //cursor ทำงานบน Record ถัดไป
            cursor.moveToNext();

        }   // for

        cursor.moveToFirst();

        int countQty = 0;
        // จองหน่วยความจำให้ Array
        arrayITEMTYPE = new int[intConsecnumcount];
        ITEMNAMEStrings = new String[intConsecnumcount];
        arrayQTY = new int[intConsecnumcount];
        //arrayQTYnotNull = new String[intConsecnumcount];

        strItemName = new String[intConsecnumcount];

        for (int i = 0; i < intConsecnumcount; i++) {

            //ค่า CONSECNUMBER หมายเลขบิล
            int intCONSECNUMBER = cursor.getColumnIndex("CONSECNUMBER");
            myCONSECNUMBER = cursor.getString(intCONSECNUMBER).substring(3);
            String masterCons = myCONSECNUMBER;

            //ได้ค่าของ itemName ชื่อสินค้า
            int intITEMNAME = cursor.getColumnIndex("ITEMNAME");
            itemName = cursor.getString(intITEMNAME);

            ITEMNAMEStrings[i] = itemName;

            //สำหรับเก็บชื่อสินค้าทั้งหมด

            strItemName[i] = itemName;
            Encoding encoding;//-----------------------------------------------------------------------------------------


            //ได้ค่าของ QTY จำนวนที่สั่ง

            int intQTY = cursor.getColumnIndex("QTY");
            myQTY = cursor.getString(intQTY);
            //arrayQTY[i] = myQTY ;

            //ได้ค่าของ UnitPrice ราคาสินค้า
            int intUNITPRICE = cursor.getColumnIndex("UNITPRICE");
            myUnitPrice = cursor.getString(intUNITPRICE);

            //ทดสอบดึ่งค่า ITEMTYPE ประเภทของตัวสินค้า
            int intITEMTYPE = cursor.getColumnIndex("ITEMTYPE");

            strITEMTYPE = cursor.getString(intITEMTYPE);

            try {
                arrayITEMTYPE[i] = Integer.parseInt(strITEMTYPE);
            } catch (Exception e) {
                arrayITEMTYPE[i] = 9;
                ITEMNAMEStrings[i] = "Have Null";
            }

            //นับจำนวน arrayITEMTYPE ที่ != null
            if (arrayITEMTYPE[i] != 9) {
                arrayQTY[countQty] = Integer.parseInt(myQTY);
                countQty += 1;
            }

            cursor.moveToNext();

        }   //for

        //จัดระเบียบ Array
        String[] finalITEMNAME = forITEMNAMEstring(ITEMNAMEStrings);

        int[] finalArrayITEMTYPE = forITEMTYPE(arrayITEMTYPE);

        forPrintLabelCondiment(myCONSECNUMBER, finalITEMNAME, finalITEMNAME.length, finalArrayITEMTYPE, arrayQTY);
        cursor.close();

        return true;

    }    // Method copySalseWork

    private int[] forITEMTYPE(int[] arrayITEMTYPE) {    //นับจำนวน ItemTpye ที่ != null

        int intTime = 0;
        for (int i = 0; i < arrayITEMTYPE.length; i++) {

            if (arrayITEMTYPE[i] != 9) {
                intTime += 1;
            }

        }   //for
        int intIndex = 0;
        int[] intResult = new int[intTime];
        for (int i = 0; i < arrayITEMTYPE.length; i++) {

            if (arrayITEMTYPE[i] != 9) {
                intResult[intIndex] = arrayITEMTYPE[i];
                intIndex += 1;
            }   // if

        }   // for

        return intResult;
    }   //Method

    private String[] forITEMNAMEstring(String[] itemnameStrings) {  //จัดเก็บ PLU โดยตัดค่า null ทิ้ง

        String TAG5 = "test1";

        int intTime = 0; // จำนวน Array ที่ไม่ม่ี Have Null
        for (int i = 0; i < itemnameStrings.length; i++) {

            if (!itemnameStrings[i].equals("Have Null")) {
                intTime += 1;
            }   // if
        }   // for
        int intIndex = 0;
        String[] strResult = new String[intTime];

        for (int i = 0; i < itemnameStrings.length; i++) {

            if (!itemnameStrings[i].equals("Have Null")) {
                strResult[intIndex] = itemnameStrings[i];
                intIndex += 1;
            }

        } //for

        return strResult;

    }   //Method

    private void spaceLine() {  //ขึ้นบรรทัดใหม่
        data.write(0x0d);
        data.write(0x0a);
    }   //Method

    @SuppressWarnings("serial")
    static final private HashMap<Character, Character>

            TIS620TBL = new HashMap<Character, Character>() {
        {
            //put(¥u2212, (char) 0x2D);
            put((char) ' ', (char) 0x20);
            put((char) 0xA0, (char) 0x20);
            put((char) 'ก', (char) 0xA1);
            put((char) 'ข', (char) 0xA2);
            put((char) 'ฃ', (char) 0xA3);
            put((char) 'ค', (char) 0xA4);
            put((char) 'ฅ', (char) 0xA5);
            put((char) 'ฆ', (char) 0xA6);
            put((char) 'ง', (char) 0xA7);
            put((char) 'จ', (char) 0xA8);
            put((char) 'ฉ', (char) 0xA9);
            put((char) 'ช', (char) 0xAA);
            put((char) 'ซ', (char) 0xAB);
            put((char) 'ฌ', (char) 0xAC);
            put((char) 'ญ', (char) 0xAD);
            put((char) 'ฎ', (char) 0xAE);
            put((char) 'ฏ', (char) 0xAF);
            //--------------------------
            put((char) 'ฐ', (char) 0xB0);
            put((char) 'ฑ', (char) 0xB1);
            put((char) 'ฒ', (char) 0xB2);
            put((char) 'ณ', (char) 0xB3);
            put((char) 'ด', (char) 0xB4);
            put((char) 'ต', (char) 0xB5);
            put((char) 'ถ', (char) 0xB6);
            put((char) 'ท', (char) 0xB7);
            put((char) 'ธ', (char) 0xB8);
            put((char) 'น', (char) 0xB9);
            put((char) 'บ', (char) 0xBA);
            put((char) 'ป', (char) 0xBB);
            put((char) 'ผ', (char) 0xBC);
            put((char) 'ฝ', (char) 0xBD);
            put((char) 'พ', (char) 0xBE);
            put((char) 'ฟ', (char) 0xBF);
            //--------------------------
            put((char) 'ภ', (char) 0xC0);
            put((char) 'ม', (char) 0xC1);
            put((char) 'ย', (char) 0xC2);
            put((char) 'ร', (char) 0xC3);
            put((char) 'ฤ', (char) 0xC4);
            put((char) 'ล', (char) 0xC5);
            put((char) 'ฦ', (char) 0xC6);
            put((char) 'ว', (char) 0xC7);
            put((char) 'ศ', (char) 0xC8);
            put((char) 'ษ', (char) 0xC9);
            put((char) 'ส', (char) 0xCA);
            put((char) 'ห', (char) 0xCB);
            put((char) 'ฬ', (char) 0xCC);
            put((char) 'อ', (char) 0xCD);
            put((char) 'ฮ', (char) 0xCE);
            put((char) 'ฯ', (char) 0xCF);
            //--------------------------
            put((char) 'ะ', (char) 0xD0);
            put((char) 'ั', (char) 0xD1);
            put((char) 'า', (char) 0xD2);
            put((char) 'ำ', (char) 0xD3);
            put((char) 'ิ', (char) 0xD4);
            put((char) 'ี', (char) 0xD5);
            put((char) 'ึ', (char) 0xD6);
            put((char) 'ื', (char) 0xD7);
            put((char) 'ุ', (char) 0xD8);
            put((char) 'ู', (char) 0xD9);
            put((char) '฿', (char) 0xDF);
            //--------------------------
            put((char) 'เ', (char) 0xE0);
            put((char) 'แ', (char) 0xE1);
            put((char) 'โ', (char) 0xE2);
            put((char) 'ใ', (char) 0xE3);
            put((char) 'ไ', (char) 0xE4);
            put((char) 'ๅ', (char) 0xE5);
            put((char) 'ๆ', (char) 0xE6);
            put((char) '็', (char) 0xE7);
            put((char) '่', (char) 0xE8);
            put((char) '้', (char) 0xE9);
            put((char) '๊', (char) 0xEA);
            put((char) '๋', (char) 0xEB);
            put((char) '์', (char) 0xEC);
            put((char) 'ํ', (char) 0xED);
            put((char) '*', (char) 0xEE);
            //--------------------------
            //--------------------------
            put((char) '๐', (char) 0xF0);
            put((char) '๑', (char) 0xF1);
            put((char) '๒', (char) 0xF2);
            put((char) '๓', (char) 0xF3);
            put((char) '๔', (char) 0xF4);
            put((char) '๕', (char) 0xF5);
            put((char) '๖', (char) 0xF6);
            put((char) '๗', (char) 0xF7);
            put((char) '๘', (char) 0xF8);
            put((char) '๙', (char) 0xF9);
            //--------------------------
        }
    };

    private void forPrintLabelCondiment(String myCONSECNUMBER, String[] itemNameStrings, int intLoop, int[] arrayITEMTYPE, int[] itemQTY) {
        int ret = com.open(SerialCom.SERIAL_TYPE_COM2, 1, "localhost");
        if (ret == 0) {     // เชื่อมต่อสำเร็จ
            //------------------------------------------------------

            //----------Config type of connection-------------------
            com.connectCom(SerialCom.SERIAL_BOUDRATE_19200,
                    SerialCom.SERIAL_BITLEN_8,
                    SerialCom.SERIAL_PARITY_NON,
                    SerialCom.SERIAL_STOP_1,
                    SerialCom.SERIAL_FLOW_NON);

            //------------------------------------------------------

            //Check All QTY of PLU
            int countLoop = 0;
            int totalQTY = 0;
            int d = 0;
            while (d < intLoop) {
                if (arrayITEMTYPE[d] == 0) {
                    totalQTY += itemQTY[d];
                }
                d++;
            }

            //-----------------------Start Loop-----------------------

            for (int i = 0; i < intLoop; i++) {
                for (int y = 0; y < itemQTY[i] && arrayITEMTYPE[i] == 0; y++) {

                    initPrinter();

                    countLoop += 1;
                    char[] charConsecNumber = ("#" + myCONSECNUMBER).toCharArray();
                    consecBuffer(charConsecNumber);

                    char[] charQTYNumber = ("          (" + countLoop + "/" + totalQTY + ")").toCharArray();
                    qtyBuffer(charQTYNumber);

                    //---------------------------------------------------------------------------------

                    String PLUtoTIS620 = UTF8toTIS620(itemNameStrings[i]);
                    char[] charItemNameStrings;
                    try {   //try เพราะ หากตัวอักษรน้อยกว่า .substring(0, maxFontName) จะเกิด Error
                        charItemNameStrings = PLUtoTIS620.substring(0, maxFontName).toCharArray();
                    } catch (Exception e) {
                        charItemNameStrings = PLUtoTIS620.toCharArray();
                    }
                    pluBuffer(charItemNameStrings);
                    spaceLine();

                    int checkConQTY = 0;
                    char[] charItemCondiment;

                    for (int k = 1; (i + k) < intLoop && (arrayITEMTYPE[i + k]) != 0 && checkConQTY < maxCondiment; k++) {
                        String ContoTIS620;
                        //try เพราะ หากตัวอักษรน้อยกว่า .substring(0, maxFontName) จะเกิด Error
                        try {   //ตัดจำนวนตัวอักษร condiment
                            ContoTIS620 = UTF8toTIS620(itemNameStrings[i + k]).substring(0, lenghtChaCondiment);
                        } catch (Exception e) {
                            ContoTIS620 = UTF8toTIS620(itemNameStrings[i + k]);
                        }
                        if (k == 1) {
                            charItemCondiment = ("  " + ContoTIS620).toCharArray();
                        } else {
                            charItemCondiment = (",  " + ContoTIS620).toCharArray();
                        }

                        conBuffer(charItemCondiment);

                        checkConQTY += 1;  //นับจำนวน Condiment
                    }   //for

//                    if (checkLineconBuffer % condimentPerLine != 0) {// ตัวสุดท้ายในกรณีหารไม่ลงตัว
//                        spaceLine();
//                    }
                    nextLabel();

                    byte[] out = data.toByteArray();
                    PrinterOut(out);

                }//For in
                initPrinter();

            }//For out

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            }
            com.close(); //Close connection
        } else
            Log.w("print connect", "fail");

    }   //Method

    static public String UTF8toTIS620(String in) {  //แปลง Character เป็น Hex ไทย
        StringBuilder out = new StringBuilder(in);
        for (int i = 0; i < in.length(); i++) {
            char c = out.charAt(i);
            Character a = (Character)TIS620TBL.get(Character.valueOf(c));
            if (a != null) {
                //out.setCharAt(i, a);
                out.setCharAt(i, a.charValue());
            }
        }
        return out.toString();
    }   //Method

    private void initPrinter() {    //เคลียร์ Buffer printer
        //----------------Clear buffer Printer--------------------
        byte ESC = 0x1B;                    //INIT Printer
        data = new ByteArrayOutputStream(); //INIT Printer
        data.write(0x1B);                   //INIT Printer
        data.write(0x40);                   //INIT Printer
        //--------------------------------------------------------
    }   //Method

    private void conBuffer(char[] text) {   //เก็บค่า Condiment เข้า ByteArrayOutputStream รอส่งพิมพ์
        boolean checkUpper = false;
        boolean checkLower = false;
        for (int i = 0; i < text.length; i++) {
            if (checkUperChar(text[i])) {// สำหรับตรวจสอบว่าต้องพิมพ์สระบรรทัดบนหรือไม่
                checkUpper = true;
            }else if (checkLowerChar(text[i])) {// สำหรับตรวจสอบว่าต้องพิมพ์สระบรรทัดล่างหรือไม่
                checkLower = true;
            }   //if
        }   //for
//        Log.w(TAG, "checkUpper = " + checkUpper + " & checkLower = " + checkLower + " <==== conBuffer");
        printing(text, checkUpper, checkLower);

        spaceLine();

    }   //Method

    private void printing(char[] text, boolean checkUpper, boolean checkLower) {    //ตรวจสอบสระบนล่างและพยัญชนะ
        //--------------------------------------------------------
        //บรรทัด 1 นี่คือ สระบน
        //--------------------------------------------------------
        if (checkUpper){
            for (int z = 0; z < text.length; z++) {
                int check = 0;
                try {
                    if (checkUperChar(text[z + 1])) {          //  สระบนแรก
                        if (checkUperChar(text[z + 2])) {      //  สระบนสอง
                            endCoding(text[z + 1], text[z + 2]);
                            z += 2;
                            check = 1;
                        }
                        if (!checkUperChar(text[z + 2]) & check == 0) {
                            data.write(text[z + 1]);  // พิ่มพ์ได้เลย
                            z += 1;
                        }
                    } else if (checkLowerChar(text[z + 1])) {    //  สระล่างแรก
                        if (checkUperChar(text[z + 2])) {
                            data.write(text[z + 2]);  // พิ่มพ์ได้เลย
                            z += 2;
                        }
                    } else data.write(0x20);   // เขียนช่องว่าง
                } catch (Exception e) {
//                    Log.w(TAG, "End of char");
                }
            }   //for
            spaceLine();
        }   //if

        //บรรทัด 2
        for (int y = 0; y < text.length; y++) {
            try {
                if (!(checkUperChar(text[y]) | checkLowerChar(text[y]))) {
                    data.write(text[y]);
                }
            } catch (Exception e) {
//                Log.w(TAG, "End of char");
            }   //try catch
        }   //for

        if(checkLower){
            spaceLine();

            //บรรทัด 3
            for (int y = 0; y < text.length; y++) {

                try {
                    if (checkLowerChar(text[y + 1])) {      //สระล่าง
                        data.write(text[y + 1]);
                        if (checkUperChar(text[y + 2])) {
                            y += 2;
                        } else y += 1;
                    } else if (!(checkLowerChar(text[y + 1]) | checkUperChar(text[y + 1]))) {
                        data.write(0x20);
                    }
                } catch (Exception e) {
//                    Log.w(TAG, "End of char");
                }   //try catch
            }   //for
        }   //if
    }   //Method

    private void endCoding(char x1,char x2) {   //แปลง สระบนกรณีสระสองชั้นเป็นสระคู่
        switch (x1) {
            case 0xD1:  //case"ั"
                switch (x2) {
                    case 0xE8: data.write(0x80);    //case"่"
                        break;
                    case 0xE9:data.write(0x81);     //case"้"
                        break;
                    case 0xEA:data.write(0x82);     //case'๊'
                        break;
                    case 0xEB:data.write(0x83);     //case"๋"
                        break;
                }
                break;
            case 0xD4:  //case"ิ"
                switch (x2) {
                    case 0xE8:data.write(0x84);     //case"่"
                        break;
                    case 0xE9:data.write(0x85);     //case"้"
                        break;
                    case 0xEA:data.write(0x86);     //case'๊'
                        break;
                    case 0xEB:data.write(0x87);     //case"๋""
                        break;
                    case 0xEC:data.write(0x88);     //case"์"
                        break;
                }
                break;
            case 0xD5:  //case"ี"
                switch (x2) {
                    case 0xE8:data.write(0x89);    //case"่"
                        break;
                    case 0xE9:data.write(0x8A);     //case"้"
                        break;
                    case 0xEA:data.write(0x8B);     //case'๊'
                        break;
                    case 0xEB:data.write(0x8C);     //case"๋""
                        break;
                }
                break;
            case 0xD6:  //case"ึ"
                switch (x2) {
                    case 0xE8:data.write(0x8D);    //case"่"
                        break;
                    case 0xE9:data.write(0x8E);     //case"้"
                        break;
                    case 0xEA:data.write(0x8F);     //case'๊'
                        break;
                    case 0xEB:data.write(0x90);     //case"๋""
                        break;
                }
                break;
            case 0xD7:  //case"ื"
                switch (x2) {
                    case 0xE8:data.write(0x81);    //case"่"
                        break;
                    case 0xE9:data.write(0x82);     //case"้"
                        break;
                    case 0xEA:data.write(0x83);     //case'๊'
                        break;
                    case 0xEB:data.write(0x84);     //case"๋""
                        break;
                }
                break;
        }   //Switch

    }   //Method

    private boolean checkLowerChar(char text) { //คือการหา สระล่าง ถ้ามี True
        if (text == 0xD8 | text == 0xD9) {
            return true;
        }else return false;
    }   //Method

    private boolean checkUperChar(char text) {// คือการหา สระบน ถ้ามี True
        if (text == 0xD1 | text == 0xD4 | text == 0xD5 | text == 0xD6 | text == 0xD7 | text == 0xE7
                | text == 0xE8 | text == 0xE9 | text == 0xEA | text == 0xEB | text == 0xEC
                | text == 0x80 | text == 0x81 | text == 0x82 | text == 0x83 | text == 0x84 | text == 0x85 | text == 0x86
                | text == 0x87 | text == 0x88 | text == 0x89 | text == 0x8A | text == 0x8B | text == 0x8C | text == 0x8E
                | text == 0x91 | text == 0x92 | text == 0x93 | text == 0x94 | text == 0x95 | text == 0x96 | text == 0x97
                | text == 0x98) {
            return true;
        }else return false;

    }   //Method


    private void nextLabel() {  //ขึ้น label ใหม่
        data.write(0x1C);
        data.write(0x28);
        data.write(0x4C);
        data.write(0x02);
        data.write(0x00);
        data.write(0x41);
        data.write(0x30);
    }   //Method

    private void consecBuffer(char[] text) {    //เก็บค่า Consecnumber เข้า ByteArrayOutputStream รอส่งพิมพื

        for (int y = 0; y < text.length; y++) {
            data.write(text[y]);
        }   //for

    }   //Method

    private void qtyBuffer(char[] text) {   //เก็บค่าเลขกำกับ Labal เข้า ByteArrayOutputStream รอส่งพิมพ์

        for (int y = 0; y < text.length; y++) {
            data.write(text[y]);
        }   //for
        spaceLine();

    }   //Method

    private void pluBuffer(char[] text) {   //เก็บค่า PLU เข้า ByteArrayOutputStream รอส่งพิมพ์

        boolean checkUpper = false;
        boolean checkLower = false;
        for (int i = 0; i < text.length; i++) {
            if (checkUperChar(text[i])) {
                checkUpper = true;
            }else if (checkLowerChar(text[i])) {
                checkLower = true;
            }

        }
//        Log.w(TAG, "checkUpper = " + checkUpper + " & checkLower = " + checkLower + " <==== pluBuffer");
        printing(text,checkUpper,checkLower);

    }   //Method

    private void PrinterOut(byte[] out) {   //สั่งพิมพ์
        com.writeData(out, out.length);
    }   //method

    private int checkTypePLU(int[] arrayITEMTYPE) { //ตรวจนับจำนวนสินค้าหลัก
        int resultQTY = 0;
        for (int i = 0; i < arrayITEMTYPE.length; i++) {
            if (arrayITEMTYPE[i] == 0) {
                resultQTY+=1;
            }
        }   //for

        return resultQTY;
    }//method

}    // Class Main
