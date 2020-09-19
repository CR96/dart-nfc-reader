package com.grizzhacks.dartreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    // Credit to this StackOverflow post for the NFC boilerplate code
    // https://stackoverflow.com/questions/5546932/how-to-read-and-write-android-nfc-tags

    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter == null) {
            // TODO: Show an error message if device does not support NFC
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
                this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            Log.d("NFC TAG:", tag.toString());
        }else{
            Log.wtf("NFC TAG:", "Couldn't read.");
        }
    }
}