package com.example.admin.webrtccall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 7/25/2016.
 */
public class RegisterActivity extends Activity {

    private UserRegisterTask mAuthTask = null;

    private TextView mLogin, mRegister;
    private EditText mUsername, mPassword;
    private View mRegisterFormView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        mLogin = (TextView) findViewById(R.id.login_button);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mRegister = (Button) findViewById(R.id.register_button);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        if (mAuthTask != null) {
            return;
        }

        mPassword.setError(null);
        mUsername.setError(null);

        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)){
            mPassword.setError("This password is too short");
            focusView = mPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)){
            mUsername.setError("This password is too short");
            focusView = mUsername;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            mAuthTask = new UserRegisterTask(username, password);
            mAuthTask.execute((Void)null);
        }

    }

    private void showProgress(final boolean show){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPassword;
        private final String mUsername;

        UserRegisterTask(String username, String password){
            this.mUsername = username;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                HttpClient httpClient = new DefaultHttpClient();

                String host = "http://" + getResources().getString(R.string.host);
                host += (":" + getResources().getString(R.string.port) + "/");
                HttpPost httpPost = new HttpPost(host + "register");

                // post data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("username", mUsername));
                nameValuePairs.add(new BasicNameValuePair("password", mPassword));

                //encoding post data
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    String json_string = EntityUtils.toString(response.getEntity());
                    JSONObject json_data = new JSONObject(json_string);

                    int status = json_data.getInt("status");
                    if (status == 1){

                        String id = json_data.getString("id");
                        String name = mUsername;
                        SharedPreferences sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("USER_ID", id);
                        edit.putString("USER_NAME", name);
                        edit.apply();

                        Intent intent =new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (ClientProtocolException e) {

                } catch (IOException e){
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
            mAuthTask = null;
            showProgress(false);

            if (aBoolean) {
//                finish();
            }
            else {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Error")
                        .setMessage("Cannot regiter, please try again!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}
