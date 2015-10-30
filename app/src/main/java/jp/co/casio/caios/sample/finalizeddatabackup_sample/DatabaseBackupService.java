package jp.co.casio.caios.sample.finalizeddatabackup_sample;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import jp.co.casio.caios.framework.device.SerialCom;

public class DatabaseBackupService extends IntentService {

    //My Explicit
    private String myCONSECNUMBER, itemName, myQTY, myUnitPrice,
            strITEMTYPE, myintFunc, myarrFunc;
    private String myTAG3 = "30Oct15";
    private int intCount, intStartCount = 0, intITEMNAMEcount;
    private int[] arrayITEMTYPE;
    private String[] ITEMNAMEStrings;

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


    private static final String SQLCMD_CREATE_TMP_CST004 =
            "CREATE TABLE CST004 ("
                    + "	TERMINALNUMBER	VARCHAR	(2)	NOT NULL,"
                    + "	BIZDATE		VARCHAR	(8)	NOT NULL,"
                    + "	CONSECNUMBER	VARCHAR	(6)	NOT NULL,"
                    + "	INVOICENUMBER	VARCHAR	(6)	NOT NULL,"
                    + "	INVOICEDATE	VARCHAR	(8)	NOT NULL,"
                    + "	INVOICETIME	VARCHAR	(6)	NOT NULL,"
                    + "	OPENCLKCODE		VARCHAR	(10)	NOT NULL,"
                    + "	CLKCODE		VARCHAR	(10)	NOT NULL,"
                    + "	CLKNAME		VARCHAR	(24)	NOT NULL,"
                    + "	REGMODE		VARCHAR	(1)	NOT NULL,"
                    + "	REGTYPE		VARCHAR	(1)	NOT NULL,"
                    + "	REGFUNC		VARCHAR	(1)	NOT NULL,"
                    + "	SALESTTLQTY	NUMERIC	(10,4)	DEFAULT 0  NOT NULL,"
                    + "	SALESTTLAMT	NUMERIC	(13)	DEFAULT 0  NOT NULL,"
                    + "	TAX1		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX2		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX3		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX4		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX5		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX6		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX7		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX8		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX9		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TAX10		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA1		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA2		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA3		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA4		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA5		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA6		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA7		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA8		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA9		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	TA10		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX1		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX2		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX3		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX4		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX5		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX6		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX7		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX8		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX9		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	EX10		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	NONTAX		NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	PROFITAMT	NUMERIC	(14,4)	DEFAULT 0  NOT NULL,"
                    + "	COVER		NUMERIC	(5)	DEFAULT 0  NOT NULL,"
                    + "	CUSTGPCODE	VARCHAR	(2),"
                    + "	CUSTGPNAME	VARCHAR	(24),"
                    + "	CUSTCODE	VARCHAR	(20),"
                    + "	CUSTNAME	VARCHAR	(40),"
                    + "	POINTTARGET	NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	POINTPREVIOUS	NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	POINTGOT	NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	POINTUSED	NUMERIC	(10)	DEFAULT 0  NOT NULL,"
                    + "	OPENCHKDATE	VARCHAR	(8),"
                    + "	OPENCHKTIME	VARCHAR	(6),"
                    + "	NBCHKDATE	VARCHAR	(8),"
                    + "	NBCHKTIME	VARCHAR	(6),"
                    + "	NBCHKNUMBER	VARCHAR	(6),"
                    + "	TABLENUMBER	VARCHAR	(12),"
                    + "	RELINVOICEDATE	VARCHAR	(8),"
                    + "	RELINVOICETIME	VARCHAR	(6),"
                    + "	RELINVOICENUMBER VARCHAR (6),"
                    + "	ZCOUNTER        VARCHAR (6),"
                    + "	REMARKS		VARCHAR (30),"
                    + "	CREATEDATETIME	VARCHAR (14)	NOT NULL,"
                    + "	UPDATEDATETIME	VARCHAR (14)	NOT NULL,"
                    + ""
                    + "	CONSTRAINT CST004_PRIMARY PRIMARY KEY ("
                    + "		TERMINALNUMBER,"
                    + "		BIZDATE,"
                    + "		CONSECNUMBER,"
                    + "		CREATEDATETIME"
                    + "		)"
                    + "	);";

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

        // ให้ไปทำงานที่ copySALESWORKcst004
        copySALESWORKcst004("CST004", selection, null, SQLCMD_CREATE_TMP_CST004);

        copySALESWORKcst005("CST005", selection, "LINENUMBER ASC", SQLCMD_CREATE_TMP_CST005);


    }   // Handler Initen

    private boolean copySALESWORKcst004(String tableName, String selection, String sortOrder, String createSQL) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content");
        builder.authority(PROVIDER);
        builder.appendPath("SALESWORK");
        builder.appendPath(tableName);
        Uri uri = builder.build();
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(uri, null, selection, null, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }   // try


        if (cursor == null) {
            return false;
        }   // if

        //Counting จำนวนทั้งของ Record
        int count = cursor.getCount();
        Log.d(myTAG, "Count ที่อ่านได้ จาก CSD004 = " + Integer.toString(count));


        cursor.moveToFirst();

        //Loop ตาม Count แต่ทำแค่ รอบเดียว
        for (int i = 0; i < count; i++) {

            Log.d(myTAG, "รอบที่ = " + Integer.toString(i));


            //ค่า SALESTTLQTY คือ ค่าที่บอก ใน 1 บิลขายกี่ชิ้น
            int offset = cursor.getColumnIndex("SALESTTLQTY");
            String mySALESTTLQTY = cursor.getString(offset);
            Log.d(myTAG, "mySALESTTLQTY (จำนวนชิ้นที่ขาย)==> " + mySALESTTLQTY);

            //ลองทดสอบ ดึ่งค่า SALESTTLAMT
            int intSALESTTLAMT = cursor.getColumnIndex("SALESTTLAMT");
            String strSALESTTLAMT = cursor.getString(intSALESTTLAMT);
            Log.d(myTAG, "SALESTTLAMT (จำนวนเงินทั้งหมดในบิล) ==> " + strSALESTTLAMT);

            intCount = Integer.parseInt(mySALESTTLQTY);

            cursor.moveToNext();


        }   //for

        cursor.close();

        return true;

    }   // copySALESWORKcst004


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

        Log.w(myTAG, "Count ที่ได้จาก CST005 ==> " + Integer.toString(count));

        cursor.moveToFirst();

        //ทดสอบหา จำนวน ItemName
        intITEMNAMEcount = 0;

        for (int i = 0; i < count; i++) {

            String strMyITEMNAME = cursor.getString(cursor.getColumnIndex("ITEMNAME"));

            if (strMyITEMNAME != null) {
                intITEMNAMEcount += 1;
            }

            cursor.moveToNext();

        }   // for


        Log.w(myTAG3, "Count ที่ได้จาก CST005 ==> " + Integer.toString(count));
        Log.w(myTAG3, "จำนวน ITMENAME == " + Integer.toString(intITEMNAMEcount));


        cursor.moveToFirst();

        // จองหน่วยความจำให้ Array
        arrayITEMTYPE = new int[intITEMNAMEcount];
        ITEMNAMEStrings = new String[intITEMNAMEcount];

        strItemName = new String[intITEMNAMEcount];

        for (int i = 0; i < intITEMNAMEcount; i++) {


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

            //ได้ค่าของ QTY จำนวนที่สั่ง
            int intQTY = cursor.getColumnIndex("QTY");
            myQTY = cursor.getString(intQTY);

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

            cursor.moveToNext();

        }   //for


        //***********************************************************
        // ส่งค่าไปพิมพ์
        // ***********************************************************

        Log.w(myTAG3, "myCONSECNUMBER ==> " + myCONSECNUMBER);


        for (int i = 0; i < arrayITEMTYPE.length; i++) {
            Log.w(myTAG3, "ITEMNAMEString (" + Integer.toString(i) + ") ==> " + ITEMNAMEStrings[i]);
            Log.w(myTAG3, "arrayITEMTYPE (" + Integer.toString(i) + ") ==> " + Integer.toString(arrayITEMTYPE[i]));
        }

        Log.w(myTAG3, "intITEMNAMEcount ==> " + Integer.toString(intITEMNAMEcount));

        //***********************************************************
        // ส่งค่าไปพิมพ์ เริ่มตรงนี่
        // ***********************************************************

        //จัดระเบียบ Array
        String[] finalITEMNAME = forITEMNAMEstring(ITEMNAMEStrings);



        int[] finalArrayITEMTYPE = forITEMTYPE(arrayITEMTYPE);


        forPrintLabelCondiment(myCONSECNUMBER, finalITEMNAME, finalITEMNAME.length, finalArrayITEMTYPE);

        cursor.close();


        return true;


    }    // Method copySalseWork

    private int[] forITEMTYPE(int[] arrayITEMTYPE) {

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
    }

    private String[] forITEMNAMEstring(String[] itemnameStrings) {

        String TAG5 = "test1";
        Log.w(TAG5, "ส่งเข้ามากี่ค่า ==> " + Integer.toString(itemnameStrings.length));


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

        Log.w(TAG5, "ส่งค่าออก ==> " + Integer.toString(strResult.length));
        return strResult;
    }


    private void forPrintLabelCondiment(String myCONSECNUMBER, String[] itemNameStrings, int intLoop, int[] arrayITEMTYPE) {

        //Check Argument
        String TAG4 = "Epson";
        Log.w(TAG4, "จำนวนของ arrayITEMTYPE ==>>" + Integer.toString(arrayITEMTYPE.length));
        Log.w(TAG4, "ค่าวนลูป การพิมพ์ == " + Integer.toString(intLoop));


        //เปิด Port ที่ใช้สำหรับ เชื่อต่อ Printer Epson
        SerialCom com = new SerialCom();
        int ret = com.open(SerialCom.SERIAL_TYPE_COM2, 1, "localhost");
        if (ret == 0) {     // เชื่อมต่อสำเร็จ

            com.connectCom(SerialCom.SERIAL_BOUDRATE_19200,
                    SerialCom.SERIAL_BITLEN_8,
                    SerialCom.SERIAL_PARITY_NON,
                    SerialCom.SERIAL_STOP_1,
                    SerialCom.SERIAL_FLOW_NON);

            byte ESC = 0x1B;
            data = new ByteArrayOutputStream();


            int intPrintLoop = 0, intMyLoop = 0;    // นี่คือจำนวน Label ที่ต้องการพิ่มพ์
            while (intPrintLoop < arrayITEMTYPE.length) {

                //ตรวจ ว่า arrayITEM ต้องไม่เท่ากับ 0 นะ
                if (arrayITEMTYPE[intPrintLoop] == 0) {
                    intMyLoop += 1;
                }

                intPrintLoop += 1;
            }   // while

            int ConQty, CheckLineFeed = 0, MaxCon = 4, test, count = 0;

            //##################################################
            // เริ่ม วนลูป พิ่มพ์
            //##################################################
            for (int LoopCheckRec = 0; LoopCheckRec < intLoop; LoopCheckRec++) {   //Check Loop < Count-2
                test = 0;
                if (arrayITEMTYPE[LoopCheckRec] == 0) {
                    count++;

                    //##################################################
                    //Print myCONSECNUMBER
                    //##################################################
                    char[] charConsecNumber = ("#" + myCONSECNUMBER + "         (" + count + "/" + intMyLoop + ")").toCharArray();
                    for (int y = 0; y < charConsecNumber.length; y++) {
                        data.write(charConsecNumber[y]);
                    }   //for
                    spaceLine();

                    //##################################################
                    //Print ProductName
                    //##################################################
                    char[] charItemName = (itemNameStrings[LoopCheckRec]).toCharArray();
                    for (int y = 0; y < charItemName.length; y++) {
                        data.write(charItemName[y]);
                    }   //for
                    spaceLine();


                    // เอามาตัก  Contiment เป้น
                    test = 1;
                }//if

                ConQty = 0;

                while (LoopCheckRec + 1 < intLoop && arrayITEMTYPE[LoopCheckRec + 1] != 0 && ConQty < MaxCon && test == 1) {

                    LoopCheckRec++;
                    ConQty++;

                    //##################################################
                    //Print Condiment
                    //##################################################
                    char[] charCon = ("  " + (itemNameStrings[LoopCheckRec])).toCharArray();

                    for (int y = 0; y < charCon.length; y++) {
                        data.write(charCon[y]);
                        Log.w(myTAG, "Y-Con ==> " + charCon[y]);
                    }   //for
                    spaceLine();
                } //End while


                data.write(0x1C);//Feed paper to the label peeling position
                data.write(0x28);
                data.write(0x4C);
                data.write(0x02);
                data.write(0x00);
                data.write(0x41);
                data.write(0x30);

            } //End for นอก

            //ของเดิม
            byte[] out = data.toByteArray();
            com.writeData(out, out.length);


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            com.close();
        } else
            Log.i("print connect", "fail");


    }  // forPrintLabelCondiment

    private void spaceLine() {
        data.write(0x0d);
        data.write(0x0a);
    }

//    private void forPrintLabel(String myCONSECNUMBER, String[] itemNameString, int intLoop, int[] arrayITEMTYPE) {
//
//        //เปิด Port ที่ใช้สำหรับ เชื่อต่อ Printer Epson
//        SerialCom com = new SerialCom();
//        int ret = com.open(SerialCom.SERIAL_TYPE_COM2, 1, "localhost");
//        if (ret == 0) {     // เชื่อมต่อสำเร็จ
//
//            com.connectCom(SerialCom.SERIAL_BOUDRATE_19200,
//                    SerialCom.SERIAL_BITLEN_8,
//                    SerialCom.SERIAL_PARITY_NON,
//                    SerialCom.SERIAL_STOP_1,
//                    SerialCom.SERIAL_FLOW_NON);
//
//
//            byte ESC = 0x1B;
//             data = new ByteArrayOutputStream();
//
//
//            //Print Label
//            for (int k = 0; k < intLoop; k++) {
//
//                //Print myCONSECNUMBER
//                char[] charConsecNumber = ("ConsencNumber = " + myCONSECNUMBER).toCharArray();
//                for (int y = 0; y < charConsecNumber.length; y++) {
//                    data.write(charConsecNumber[y]);
//                }   //for
//               spaceLine();
//
//
//                //Print itemName
//                char[] charItemName = ("itemName = " + itemNameString[k]).toCharArray();
//                for (int i = 0; i < charItemName.length; i++) {
//                    data.write(charItemName[i]);
//                }
//                spaceLine();
//                spaceLine();
//                spaceLine();
//                spaceLine();
//
//
//                //Print Count
//                char[] charCount = (Integer.toString(k + 1) + "/" + Integer.toString(intLoop)).toCharArray();
//                for (int i = 0; i < charCount.length; i++) {
//                    data.write(charCount[i]);
//                }
//
//
//
//
//               //การจบแต่ละบิล
//                data.write(0x1b);   //ESC
//                data.write(0x64);   //Feed ling
//                data.write(1);      //กำหนดการเว้นบรรทัด
//                // ควรจบด้วยแบบนี่
//
//
//            }   // for
//
//            //ของเดิม
//            byte[] out = data.toByteArray();
//            com.writeData(out, out.length);
//
//
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            com.close();
//        } else
//            Log.i("print connect", "fail");
//
//    }   // forPrintLabel


    private void forPrintLabelByEPSON(String myCONSECNUMBER, String itemName, String strQTY, String myUnitPrice, String strCount) {


    }   // forPrintLabelByEPSON


    private void forPrintByEPSON_ITEMTYPE2(String myCONSECNUMBER, String itemName, String strQTY, String myUnitPrice, String strCount) {

    }

    private void forPrintByEPSON_ITEMTYPE1(String myCONSECNUMBER, String itemName, String strQTY, String myUnitPrice, String strCount) {

    }


//    boolean isPrintKPNo2(String strPrinted) {
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("content");
//        builder.authority(PROVIDER);
//        builder.appendPath("SETTING");
//        builder.appendPath("CIA001");
//        Uri uri = builder.build();
//        Cursor cursor = null;
//        String selection = String.format("ITEMCODE='%s'", strPrinted);
//
//        try {
//            cursor = getContentResolver().query(uri, null, selection, null, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (cursor == null) {
//            return false;
//        }
//        int count = cursor.getCount();
//        cursor.moveToFirst();
//        if (count == 0) {
//            return false;
//        }
//        for (int i = 0; i < count; i++) {
//
//            //ค่า ItemName ที่มี Parameter
//            int offset = cursor.getColumnIndex("ITEMPARMCODE");
//            String itemParamCode = cursor.getString(offset);
//            cursor.moveToNext();
//            Log.i("test", "ITEMPARMCODE=" + itemParamCode);
//        }
//
//        cursor.close();
//        return true;
//    }
}    // Main
