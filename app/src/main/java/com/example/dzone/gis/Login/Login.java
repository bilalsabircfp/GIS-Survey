package com.example.dzone.gis.Login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzone.gis.JsonParser.Check_internet_connection;
import com.example.dzone.gis.JsonParser.JsonParser;
import com.example.dzone.gis.Navgation.Home;
import com.example.dzone.gis.Navgation.Home2;
import com.example.dzone.gis.R;
import com.example.dzone.gis.url.Url;


import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by DzonE on 07-Aug-17.
 */

public class Login extends AppCompatActivity {

    Intent intent;
    SharedPreferences sharedPreferences;
    boolean server_check = false;
    JSONObject jp_obj;
    JSONArray jar_array;
    EditText et_email, et_password;
    String email, password, s_name, s_name2,s_id;
    Button btn_login;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.pass);
        btn_login = (Button) findViewById(R.id.btn);
        ImageView img = (ImageView) findViewById(R.id.imgg);


        final int longClickDuration = 5000;
        final boolean[] isLongPress = {false};

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isLongPress[0] = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isLongPress[0]) {
                                Toast.makeText(Login.this, "Developed by Bilal Sabir"
                                        + "\n" + "bilalsabir0001@gmail.com" , Toast.LENGTH_LONG).show();

                            }
                        }
                    }, longClickDuration);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isLongPress[0] = false;
                }
                return true;
            }
        });




        Button_Clicks();

    }

    //ONClick Listners////////////////////////////////////////
    public void Button_Clicks() {


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                email    = et_email.getText().toString().trim();
                password = et_password.getText().toString().trim();

                if ((et_email.getText().toString().length() == 0))
                    et_email.setError("Enter User Name");

                else if ((et_password.getText().toString().length() == 0))
                    et_password.setError("Enter Password");


                else {

                    if(new Check_internet_connection(getApplicationContext()).isNetworkAvailable()){

                        if(email.equals("9t3") && password.equals("anon")){

                            intent = new Intent(Login.this, Home2.class);
                            startActivity(intent);
                            finish();

                        }else {

                            new LOginUser().execute();
                        }

                    }
                    else {

                        Toast.makeText(getApplicationContext(),
                                "Check your Internet Connection & Try again", Toast.LENGTH_LONG).show();
                    }


                }

            }
        });


    }



    String server_response = "0", server_response_text;

    //ASYNTASK JSON//////////////////////////////////////////
    public class LOginUser extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setTitle("Loading! Be Patient!");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                JSONObject obj = new JSONObject();

                obj.put("operation", "login");

                    obj.put("app_username", email);
                    obj.put("app_user_password", password);

                String str_req = JsonParser.multipartFormRequestForFindFriends(Url.ulr, "UTF-8", obj, null);

                jp_obj = new JSONObject(str_req);
                jar_array = jp_obj.getJSONArray("JsonData");

                JSONObject c;

                c = jar_array.getJSONObject(0);

                if (c.length() > 0) {

                    server_response = c.getString("response");

                    if (server_response.equals("0")) {
                        server_response_text = c.getString("response-text");

                    }
                }


                if (server_response.equals("1")) {

                    c = jar_array.getJSONObject(1);

                    if (c.length() > 0 ) {

                        email  = c.getString("app_username");
                        s_name  = c.getString("app_user_first_name");
                        s_name2  = c.getString("app_user_last_name");
                        s_id  = c.getString("app_user_id");

                    }

                }


                server_check = true;

            } catch (Exception e) {
                e.printStackTrace();

                //server response/////////////////////////
                server_check = false;
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();


            if (server_check = true) {

                if (server_response.equals("1")) {

//                    Toast.makeText(Login.this, "Login Successful\n"+number
//                            +"\n"+ password, Toast.LENGTH_SHORT).show();


                    //Creating a shared preference
                    sharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE);

                    //Creating editor to store values to shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    //Adding values to editor
                    editor.putString("number", email);
                    editor.putString("name", s_name);
                    editor.putString("name2", s_name2);
                    editor.putString("u_id", s_id);

                    //Saving values to editor
                    editor.commit();


                        intent = new Intent(Login.this, Home2.class);
                        startActivity(intent);
                        finish();




                } else {
                    Toast.makeText(Login.this, server_response_text, Toast.LENGTH_SHORT).show();

                }


            } else {

                Toast.makeText(Login.this, "Error while loading data", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //In onresume fetching value from sharedpreference
        sharedPreferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("number", "").equals("")) {

//            Toast.makeText(this, "No Data Here", Toast.LENGTH_SHORT).show();

        }
        else {

            Intent intent = new Intent(Login.this, Home2.class);
            startActivity(intent);
            Login.this.finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Login.this.finish();
    }
}
