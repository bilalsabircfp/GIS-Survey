package com.example.dzone.gis.Navgation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.sax.StartElementListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzone.gis.Database.DatabaseHelper;
import com.example.dzone.gis.JsonParser.Check_internet_connection;
import com.example.dzone.gis.JsonParser.JsonParser;
import com.example.dzone.gis.Login.Login;
import com.example.dzone.gis.R;
import com.example.dzone.gis.url.Url;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Offline_Data extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //database helper object
    private DatabaseHelper db;


    String lat, lon, area_,t_food_,s_name_,date_,shop_,owner_,nic_,num_,num_worker_,other_="", all, remarks_,img_path;
    int status_, id_,intent_data;boolean check = true;

    //View objects
    private Button buttonSave,btn2;
    private ListView listViewNames;

    //List to store all the names
    private List<Name> names;
    ImageView t_img;
    String ImageName="image_name", ImagePath="image_path";
    Bitmap myBitmap;


    Dialog dialog;

    //adapterobject for list view
    private NameAdapter nameAdapter;

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
    SharedPreferences preferences; String surv_name,surv_name2;
    JSONObject jp_obj;
    JSONArray jar_array;
    String server_response="0"
            ,server_response_text,u_id;
    boolean server_check=false;Button send,cross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        

        preferences = this.getSharedPreferences("DataStore" , Context.MODE_PRIVATE);
        surv_name = preferences.getString("name", "Ni Mila Kuch");
        surv_name2 = preferences.getString("name2", "Ni Mila Kuch");
        u_id = preferences.getString("u_id", "Ni Mila Kuch");


        db = new DatabaseHelper(this);
        names = new ArrayList<>();

        buttonSave = findViewById(R.id.button2);
        listViewNames = findViewById(R.id.listview2);


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        loadNames();

        listViewNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {


                listview_click(l,id);
//
                Name name;
                name = nameAdapter.getItem(id);


                area_ = name.getArea();
                t_food_ = name.getFood();
                s_name_  = name.getName();
                shop_ = name.getShop();
                owner_ = name.getOwner();
                nic_ = name.getNic();
                num_ = name.getNum();
                lat = name.getLat();
                lon = name.getLon();
                num_worker_ = name.getNum_workers();
                all = name.getAll();
                remarks_ = name.getRemarks();
                status_ = name.getStatus();
                id_= name.getID();
                img_path = name.getImg_path();
                date_ = name.getDate();


                dialog = new Dialog(Offline_Data.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.dialog_submit_report);

                TextView t_area  = (TextView) dialog.findViewById(R.id.area);
                TextView t_f_name  = (TextView) dialog.findViewById(R.id.f_typ);
                TextView t_shop  = (TextView) dialog.findViewById(R.id.shop_n);
                TextView t_own  = (TextView) dialog.findViewById(R.id.shop_o);
                TextView t_num  = (TextView) dialog.findViewById(R.id.numb);
                TextView t_worker  = (TextView) dialog.findViewById(R.id.worker);
                TextView t_remarks  = (TextView) dialog.findViewById(R.id.remak);
                t_img  = (ImageView) dialog.findViewById(R.id.mg);
                cross  = (Button) dialog.findViewById(R.id.cross);
                send  = (Button) dialog.findViewById(R.id.btn);


                t_area.setText(area_);
                t_f_name.setText(t_food_);
                t_shop.setText(shop_);
                t_own.setText(owner_);
                t_num.setText(num_);
                t_worker.setText(num_worker_);
                if(remarks_.equals("")){
                    remarks_ = "N/A";
                    t_remarks.setText("N/A");
                }
                t_remarks.setText(remarks_);



                File imgFile = new  File(img_path);
//
                if(imgFile.exists()){

                    myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    t_img.setImageBitmap(myBitmap);
                }
                else {
                    t_img.setImageResource(R.drawable.no_image);
//                    Toast.makeText(Offline_Data.this, "not found", Toast.LENGTH_SHORT).show();
                }


                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });


                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (new Check_internet_connection(Offline_Data.this).isNetworkAvailable()) {

                            new RegisterUser_offline().execute();

                        } else {

                            Toast.makeText(Offline_Data.this,
                                    "Connect to Internet", Toast.LENGTH_LONG).show();
                        }
                    }
                });





                dialog.show();


            }
        });



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //////////////////////////////////////
        SharedPreferences preferences = Offline_Data.this.getSharedPreferences("DataStore" , Context.MODE_PRIVATE);
        String surv_name = preferences.getString("name", "Ni Mila Kuch");
        String surv_name2 = preferences.getString("name2", "Ni Mila Kuch");

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView test = (TextView) header.findViewById(R.id.s_name);
        test.setText(surv_name + "\t" + surv_name2);
        navigationView.setNavigationItemSelectedListener(this);
    }




    private void loadNames() {
        names.clear();
        int uid = Integer.parseInt(u_id);
        Cursor cursor = db.getUnsyncedNames(uid);
        if (cursor.moveToFirst()) {
            do {
                Name name = new Name(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AREA)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FOOD)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SHOP)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OWNER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NIC)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NUM)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LAT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LON)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NUM_WORKER)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ALL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REMARKS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                );
                names.add(name);
            } while (cursor.moveToNext());
        }

        nameAdapter = new NameAdapter(this, R.layout.names, names);
        listViewNames.setAdapter(nameAdapter);
    }




    public void listview_click(long id,int position){

        if(id == 1) {


        }

    }


////////////////////////////////////////////////////////////////////////////

    ProgressDialog progressDialog;
    public class RegisterUser_offline extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {


            progressDialog = new ProgressDialog(Offline_Data.this);
            progressDialog.setTitle("Uploading!");
            progressDialog.setCancelable(false);
            progressDialog.show();

            send.setEnabled(false);


        }

        @Override
        protected String doInBackground(String... params) {

            try {

                JSONObject obj     = new JSONObject();

                obj.put("operation","survey");

                obj.put("lat", lat);
                obj.put("lon", lon);
                obj.put("area", area_);
                obj.put("t_food", t_food_);
                obj.put("s_name", surv_name + "\t" + surv_name2);
                obj.put("shop", shop_);
                obj.put("owner", owner_);
                obj.put("nic", nic_);
                obj.put("num", num_);
                obj.put("other", all);
                obj.put("remarks", remarks_);
                obj.put("num_worker", num_worker_);


                String str_req = JsonParser.multipartFormRequestForFindFriends(Url.ulr , "UTF-8", obj,null);

                jp_obj    = new JSONObject(str_req);
                jar_array = jp_obj.getJSONArray("JsonData");

                JSONObject c;

                c = jar_array.getJSONObject(0);

                if(c.length()>0){

                    server_response    = c.getString("response");

                    if(server_response.equals("0")){
                        server_response_text    = c.getString("response-text");

                    }
                }

                server_check=true;

            } catch (Exception e) {
                e.printStackTrace();

                //server response/////////////////////////
                server_check =false;
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

                progressDialog.dismiss();

            if(server_check){

                if(server_response.equals("1")){

//                    Toast.makeText(Offline_Data.this, "Uploading Data!",
//                            Toast.LENGTH_SHORT).show();


                    int id;
                    id = id_;
                    db.updateNameStatus(id,1);


                    ImageUploadToServerFunction();



                }else {

                    Toast.makeText(Offline_Data.this, server_response_text, Toast.LENGTH_SHORT).show();

                }

            }else {

                Toast.makeText(Offline_Data.this, "Internet Issue", Toast.LENGTH_SHORT).show();
            }

        }
    }





    ProgressDialog progressDialog1;
    //image ulpoading
    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        t_img.buildDrawingCache();
        Bitmap bmap = t_img.getDrawingCache();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                progressDialog1 = new ProgressDialog(Offline_Data.this);
                progressDialog1.setTitle("Uploading Data!");
                progressDialog1.setCancelable(false);
                progressDialog1.show();

                send.setEnabled(true);

            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog1.dismiss();

                Toast.makeText(Offline_Data.this,"Data Uploaded",Toast.LENGTH_LONG).show();
                dialog.dismiss();
                loadNames();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new Offline_Data.ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageName, "image");

                HashMapParams.put("id", "00");

                HashMapParams.put(ImagePath, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(Url.ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
            loadNames();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //here is the main place where we need to work on.
        int id=item.getItemId();
        switch (id){

            case R.id.nav_home:
                Intent h= new Intent(Offline_Data.this,Home.class);
                //clear all activities in stack
                h.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(h);
                break;
            case R.id.nav_gallery:
                //leave empty
                break;
            case R.id.nav_import:
                logout();
                break;

            // after this lets start copying the above.
            // FOLLOW MEEEEE>>>
            //copy this now.
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){

        //Creating an alert dialog to confirm logout

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Logout!");
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences("DataStore", Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Putting blank value to number
                        editor.putString("number", "");
                        editor.putString("cat", "");

                        //Saving the sharedpreferences
                        editor.clear();
                        editor.apply();
                        finish();

                        //Starting login activity
                        Intent intent = new Intent(Offline_Data.this, Login.class);
                        startActivity(intent);
                        Offline_Data.this.finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


}
