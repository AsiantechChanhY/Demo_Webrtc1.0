package com.example.admin.webrtccall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.webrtccall.adapter.HistoryAdapter;
import com.example.admin.webrtccall.model.HistoryItem;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Admin on 7/25/2016.
 */
public class MainActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private String userName;
    private String userId;
    private Button mVideoCall, mAudioCall;
    private TextView mUsercall;
    private ListView mHistoryList;
    private HistoryAdapter mHistoryAdapter;
    public ArrayList<HistoryItem> arrayOfUsers;
    private Socket client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        this.mSharedPreferences = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
        if (!this.mSharedPreferences.contains("USER_ID")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mVideoCall = (Button) findViewById(R.id.btnVideoCall);
        mAudioCall = (Button) findViewById(R.id.btnAudioCall);
        mHistoryList = (ListView) findViewById(R.id.opponentsList);

        this.userId = this.mSharedPreferences.getString("USER_ID", "");
        this.userName = this.mSharedPreferences.getString("USER_NAME", "");

        mUsercall = (TextView) findViewById(R.id.selectUsertextView);
        mUsercall.setText(userName);

        arrayOfUsers = new ArrayList<HistoryItem>();
        String json_friend = "";
        try {
            try {
                json_friend = new ListFriendsTask().execute(userId).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            JSONArray jsonarr = new JSONArray(json_friend);
            for (int i = 0; i < jsonarr.length(); i++) {
                JSONObject jsonobj = jsonarr.getJSONObject(i);
                String id = jsonobj.getString("friend_id");
                String name = "";
                String status = "";
                try {
                    try {
                        name = new RetrieveName().execute(id).get();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                HistoryItem x = new HistoryItem(id,name, status);
                arrayOfUsers.add(x);
            }
        } catch (Exception e) {
        }

        String host = "http://" + getResources().getString(R.string.host);
        host += (":" + getResources().getString(R.string.port) + "/");
        try {
            client = IO.socket(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        client.on("receiveCall", onReceiveCall);

        client.connect();
        try {
            JSONObject message = new JSONObject();
            message.put("myId", userId);
            client.emit("resetId", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mHistoryAdapter = new HistoryAdapter(this, arrayOfUsers);
        mHistoryList.setAdapter(mHistoryAdapter);

    }

    private void videoCall(final String callnum){
        mVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHistoryAdapter.getSelected().isEmpty()){
                    Toast.makeText(MainActivity.this, "Choose one opponent", Toast.LENGTH_SHORT ).show();
                }
                if (mHistoryAdapter.getSelected().size() > 6){
                    Toast.makeText(MainActivity.this, "khong duoc qua 6", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(MainActivity.this,RtcActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("name", userName);
                intent.putExtra("number", callnum);
                startActivity(intent);
            }
        });
    }

    public void makeCall(String id) {
        String callNum = id;
        if (callNum.isEmpty() || callNum.equals(this.userId)) {
            Toast.makeText(MainActivity.this,"Enter a valid user ID to call.", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            videoCall(callNum);
        }
    }


    class ListFriendsTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String... urls) {
            String json_string = "";
            String user = urls[0];
            try {
                HttpClient httpClient = new DefaultHttpClient();
                // replace with your url
                String host = "http://" + getResources().getString(R.string.host);
                host += (":" + getResources().getString(R.string.port) + "/");
                HttpPost httpPost = new HttpPost(host + "friends");

                //Post Data
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                nameValuePair.add(new BasicNameValuePair("username", user));

                //Encoding POST data
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                } catch (UnsupportedEncodingException e) {
                    // log exception
                    e.printStackTrace();
                }
                //making POST request.
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    json_string = EntityUtils.toString(response.getEntity());


                } catch (ClientProtocolException e) {
                    // Log exception

                    //Log.d("minh_res", "error");
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                    //Log.d("minh_res", e.getMessage());

                }
            } catch (Exception e) {
            }
            return json_string;
        }

        protected void onPostExecute(String feed) {
        }
    }
    class RetrieveName extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String... urls) {
            String json_string = "";
            String user = urls[0];

            try {
                HttpClient httpClient = new DefaultHttpClient();
                // replace with your url
                String host = "http://" + getResources().getString(R.string.host);
                host += (":" + getResources().getString(R.string.port) + "/");
                HttpPost httpPost = new HttpPost(host + "friend_name");

                //Post Data
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
                nameValuePair.add(new BasicNameValuePair("username", user));

                //Encoding POST data
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                } catch (UnsupportedEncodingException e) {
                    // log exception
                    e.printStackTrace();
                }
                //making POST request.
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    json_string = EntityUtils.toString(response.getEntity());
                    JSONObject x = new JSONObject(json_string);
                    json_string = x.getString("name");

                } catch (ClientProtocolException e) {
                    // Log exception
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }
            } catch (Exception e) {
            }
            return json_string;
        }

        protected void onPostExecute(String feed) {
        }
    }


}
