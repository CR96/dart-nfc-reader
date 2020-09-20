package com.grizzhacks.dartreader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.AnimationDrawable;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private AnimationDrawable animationDrawable;

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

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        // TODO: Animated gradient is currently broken
        // animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        // animationDrawable.setEnterFadeDuration(3000);
        // animationDrawable.setExitFadeDuration(2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);

            if (animationDrawable != null && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            final MifareUltralight ultralight = MifareUltralight.get(tag);

            if (ultralight != null) {

                // You can only run one of these. You can't open two connections simultaneously.

                // WARNING: The writeTag() method will overwrite activation information.
                // Do not use on a DART pass you wish to keep.

                // writeTagInfo(ultralight);

                getTagInfo(ultralight);
            }
        }
    }

    private void writeTagInfo(final MifareUltralight ultralight) {
        Thread thread = new Thread(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        try {
                            ultralight.connect();

                            // Test writing data to a userdata page on the NFC tag
                            ultralight.writePage(3, "test".getBytes(StandardCharsets.UTF_8));

                            ultralight.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        thread.start();
    }

    private void getTagInfo(final MifareUltralight ultralight) {
        Thread thread = new Thread(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        try {
                            ultralight.connect();
                            byte[] byteArray = ultralight.readPages(3);
                            Log.d("DATA: ", new String(byteArray, StandardCharsets.UTF_8));
                            ultralight.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        thread.start();
    }
}