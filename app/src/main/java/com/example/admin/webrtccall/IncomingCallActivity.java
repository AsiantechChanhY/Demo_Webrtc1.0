package com.example.admin.webrtccall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Admin on 7/26/2016.
 */
public class IncomingCallActivity extends AppCompatActivity {
    private String callerName;
    private TextView mCallerId;
    private String userName;
    private String userId;
    private Socket socket;
    private String callerId;
    private Vibrator vib;
    private MediaPlayer mMediaPlayer;
    private ImageButton mAcceptCall, mRejectCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        Bundle extras = getIntent().getExtras();
        callerId = extras.getString("CALLER_ID");
        userId = extras.getString("USER_ID");
        callerName = extras.getString("CALLER_NAME");
        userName = extras.getString("USER_NAME");

        mCallerId = (TextView) findViewById(R.id.callerName);
        mCallerId.setText(callerName);



        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.skype_call);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        vib.vibrate(pattern, 0);

        mAcceptCall = (ImageButton) findViewById(R.id.takeBtn);
        mRejectCall = (ImageButton) findViewById(R.id.rejectBtn);

        mAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptCall();
            }
        });

        mRejectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectCall();
            }
        });
    }

    private void acceptCall(){
        vib.cancel();
        mMediaPlayer.stop();
        finish();

        Intent intent = new Intent(IncomingCallActivity.this, RtcActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("name", userName);
        intent.putExtra("callerIdchat", callerId);
        startActivity(intent);

        String host = "http://" + getResources().getString(R.string.host);
        host += (":" + getResources().getString(R.string.port) + "/");

        try {
            socket = IO.socket(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();
        try {
            JSONObject message = new JSONObject();
            message.put("myId", userId);
            message.put("callerID", callerId);
            socket.emit("acceptcall", message);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void rejectCall(){
        vib.cancel();
        mMediaPlayer.stop();

        String host = "http://" + getResources().getString(R.string.host);
        host += (":" + getResources().getString(R.string.port) + "/");

        try {
            socket = IO.socket(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            JSONObject message = new JSONObject();
            message.put("myId", userId);
            message.put("callerID", callerId);
            socket.emit("acceptcall", message);
        } catch (JSONException e){
            e.printStackTrace();
        }

        finish();
        Intent intent= new Intent(IncomingCallActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        socket.connect();
    }
}
