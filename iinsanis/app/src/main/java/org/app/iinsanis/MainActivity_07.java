package org.app.iinsanis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity_07 extends AppCompatActivity {
    TextView checkproductname;
    String intentprice, intentsale, intentname;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Tag tag;
    private IsoDep tagcomm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main07);

        checkproductname = (TextView) findViewById(R.id.checkproductname);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Button nfcyes=(Button)findViewById(R.id.nfcyes);
        nfcyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity_07.this, MainActivity_08.class);
                intentname=checkproductname.getText().toString();
                intent.putExtra("intentname", intentname);
                intent.putExtra("intentprice", intentprice);
                intent.putExtra("intentsale", intentsale);
                startActivity(intent);
                finish();
            }
        });

        Intent targetIntent = new Intent(this, MainActivity_07.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, targetIntent, 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("fail", e);
        }

        mFilters = new IntentFilter[] { ndef };

        mTechLists = new String[][] { new String[] { NfcA.class.getName() } };
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (nfcAdapter != null) {

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    public void onNewIntent(Intent passedIntent) {
        // ????????? ??????????????? NFC ????????? ????????????.
        super.onNewIntent(passedIntent);
        Tag tag = passedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            byte[] tagId = tag.getId();
            checkproductname.append("");
        }
        if (passedIntent != null) {
            processTag(passedIntent);
        }

    }

    public static final String CHARS = "0123456789ABCDEF";

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {


            sb.append(CHARS.charAt((data[i] >> 4) & 0x0f)).append(
                    CHARS.charAt(data[i] & 0x0f));
        }
        return sb.toString();
    }

    private void processTag(Intent passedIntent) {
        Parcelable[] rawMsgs = passedIntent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        // ????????? ???????????? ????????? ?????? ???????????????.
        if (rawMsgs == null) {
            return;
        }

        NdefMessage[] msgs;

        // ????????? INTENT??? ????????? ??????????????? NdefMessage???????????? ???????????????.
        if (rawMsgs != null) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                /**
                 * tag??? ????????? ???????????? ????????? ?????? ?????????
                 */
                showTag(msgs[i]);
            }
        }


    }

    private int showTag(NdefMessage mMessage) {
        /**
         * NDEFMESSAGE???????????? ????????? ????????? parse?????? ???????????? ???????????? ???????????? ????????????.
         */
        List<ParsedRecord> records = NdefMessageParser.parse(mMessage);
        final int size = records.size();

        /**
         * ????????? ??????????????? ???????????? ????????? ????????????.
         */
        for (int i = 0; i < size; i++) {
            ParsedRecord record = records.get(i);

            /**
             * ???????????? ???????????? ??????
             */
            int recordType = record.getType();

            String recordStr = "";
            if (recordType == ParsedRecord.TYPE_TEXT) {
                recordStr = ((TextRecord) record).getText();
            } else if (recordType == ParsedRecord.TYPE_URI) {
                recordStr = "URI :" + ((UriRecord) record).getUri().toString();
            }
            // ???????????? ????????? ?????? TextView??? ?????????.
            String[] splitcomma=recordStr.split(",");
            checkproductname.setText("\n"+splitcomma[0]);
            intentprice=splitcomma[1];
            intentsale=splitcomma[2];
        }
        return size;
    }
}