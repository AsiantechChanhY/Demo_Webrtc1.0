package com.example.admin.webrtccall;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private UserLoginTask mAuthTask = null;

    private TextView mRegister;
    private AppCompatButton mLogin;
    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRegister = (TextView) findViewById(R.id.email_register_button);
        mLogin = (AppCompatButton) findViewById(R.id.email_sign_in_button);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);

        mPassword = (EditText) findViewById(R.id.password);
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        populateAutoComplete();
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void attemptLogin() {
        if (mAuthTask != null){
            return;
        }
        mEmail.setError(null);
        mPassword.setError(null);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPassworldValid(password)){
            mPassword.setError("This password is too short");
            focusView = mPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)){
            mEmail.setError("This field is required");
            focusView = mEmail;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPassworldValid(String password){
        return password.length() > 4;
    }

    private void showProgress(final boolean show){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<String> emails = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast()){
            emails.add(data.getString(ProfileQuery.ADDRESS));
            data.moveToFirst();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void addEmailsToAutoComplete(List<String> emailAddressColection){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,
                android.R.layout.simple_dropdown_item_1line, emailAddressColection);

        mEmail.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY
        };
        int ADDRESS = 0;
    }

    public class UserLoginTask extends AsyncTask<Void , Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password){
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
           try {
               HttpClient httpClient = new DefaultHttpClient();

               String host = "http://" + getResources().getString(R.string.host);
               host += (":" +getResources().getString(R.string.port) + "/");
               HttpPost httpPost = new HttpPost(host + "login");

               List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
               nameValuePairs.add(new BasicNameValuePair("username",mEmail ));
               nameValuePairs.add(new BasicNameValuePair("password", mPassword));

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

                   if (status == 1) {
                       String id = json_data.getString("id");
                       String name = json_data.getString("name");
                       SharedPreferences sp = getSharedPreferences("SHARED_PREFS", MODE_PRIVATE);
                       SharedPreferences.Editor edit = sp.edit();
                       edit.putString("USER_ID", id);
                       edit.putString("USER_NAME", name);
                       edit.apply();

                       Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                       startActivity(intent);
                   }
                   else {
                       return false;
                   }
               } catch (ClientProtocolException e) {
                   e.printStackTrace();
               } catch (IOException e){
                   e.printStackTrace();
               }
           } catch (Exception e){}

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean aBoolean) {
            mAuthTask = null;
            showProgress(false);

            if (aBoolean) {
                finish();
            }
            else {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error")
                        .setMessage("User name or/and password is not correct!")
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
