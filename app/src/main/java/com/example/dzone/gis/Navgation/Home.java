package com.example.dzone.gis.Navgation;

import android.Manifest;
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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dzone.gis.Database.DatabaseHelper;
import com.example.dzone.gis.JsonParser.Check_internet_connection;
import com.example.dzone.gis.JsonParser.GPSTracker;
import com.example.dzone.gis.JsonParser.JsonParser;
import com.example.dzone.gis.Login.Login;
import com.example.dzone.gis.R;
import com.example.dzone.gis.Volley.VolleySingleton;
import com.example.dzone.gis.url.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.CAMERA;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Bitmap myBitmap;
    Uri picUri;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    GPSTracker gps;
    EditText area, t_food, date, shop, owner, nic, num, num_wroker, other, remarks;
    String area_, t_food_, s_name_, date_, shop_, owner_, nic_, num_, num_worker_, other_ = "", all, remarks_;
    TextView s_name;
    CheckBox c1, c2, c3, c4, c5, c6, c7, c8, c9;
    double lat = 0, lon = 0;
    Button submit;
    String u_id, cc1 = "", cc2 = "", cc3 = "", cc4 = "", cc5 = "", cc6 = "", cc7 = "", cc8 = "", cc9 = "", surv_name, surv_name2;
    boolean check = true;
    ProgressDialog progressDialog;
    String ImageName = "image_name";
    String ImagePath = "image_path";
    JSONObject jp_obj;
    ImageView imageView;
    JSONArray jar_array;
    Intent intent;
    boolean server_check = false;
    FloatingActionButton camera_btn;


    //database helper object
    private DatabaseHelper db;
    //View objects
    private Button buttonSave, btn2;
    String saved_image_path;
    private ListView listViewNames;
    //List to store all the names
    private List<Name> names;
    int flag = 1;
    double lati, longi;

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    SharedPreferences preferences;
    String currentDateTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //get current date and time
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        Toast.makeText(this, currentDateTimeString, Toast.LENGTH_SHORT).show();


        //initializing views and objects
        db = new DatabaseHelper(this);
        names = new ArrayList<>();


        preferences = Home.this.getSharedPreferences("DataStore", Context.MODE_PRIVATE);
        surv_name = preferences.getString("name", "Mr");
        surv_name2 = preferences.getString("name2", "No.Body");
        u_id = preferences.getString("u_id", "Ni Mila Kuch");



        //check if gps is enabled////////////////
        check_gps();


        camera_btn = (FloatingActionButton) findViewById(R.id.fab);
        area = (EditText) findViewById(R.id.area);
        t_food = (EditText) findViewById(R.id.f_type);
        s_name = (TextView) findViewById(R.id.s_name);
        date = (EditText) findViewById(R.id.date);
        shop = (EditText) findViewById(R.id.shop);
        owner = (EditText) findViewById(R.id.shop_ow);
        nic = (EditText) findViewById(R.id.nic);
        num = (EditText) findViewById(R.id.num);
        other = (EditText) findViewById(R.id.et_other);
        remarks = (EditText) findViewById(R.id.remarks);
        num_wroker = (EditText) findViewById(R.id.no_workers);
        submit = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imagview);
        c1 = (CheckBox) findViewById(R.id.c1);
        c2 = (CheckBox) findViewById(R.id.c2);
        c3 = (CheckBox) findViewById(R.id.c3);
        c4 = (CheckBox) findViewById(R.id.c4);
        c5 = (CheckBox) findViewById(R.id.c5);
        c6 = (CheckBox) findViewById(R.id.c6);
        c7 = (CheckBox) findViewById(R.id.c7);
        c8 = (CheckBox) findViewById(R.id.c8);
        c9 = (CheckBox) findViewById(R.id.c9);


        s_name.setText(surv_name + "\t" + surv_name2);



        requestPermission();


        c9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag > 0){
                    other.setVisibility(View.VISIBLE);
                    flag = 0;
                }
                else {

                    other.setVisibility(View.INVISIBLE);
                    flag = 1;

                }

            }
        });


        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                permissions.add(CAMERA);
                permissionsToRequest = findUnAskedPermissions(permissions);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (permissionsToRequest.size() > 0)
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()])
                                , ALL_PERMISSIONS_RESULT);
                }

                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GPSTracker gpsTracker = new GPSTracker(Home.this);

                lati=gpsTracker.getLatitude();
                longi=gpsTracker.getLongitude();
                String str = "Latitude: "+lati+" Longitude: "+longi;
                Toast.makeText(Home.this, str, Toast.LENGTH_SHORT).show();



                //permission for android >= 6
                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                } else {
                    // permission has been granted, continue as usual
                }


                other_ = other.getText().toString().trim();

                if (other_.equals("")) {

                    check_box_click();
                    all = cc1 + "," + cc2 + "," + cc3 + "," + cc4 + "," + cc5 + "," + cc6 + "," + cc7 + "," + cc8;

                } else {

                    check_box_click();
                    all = cc1 + "," + cc2 + "," + cc3 + "," + cc4 + "," + cc5 + "," + cc6 + "," +
                            cc7 + "," + cc8 + "," + "other:" + other_;

                }

                area_ = area.getText().toString().trim();
                t_food_ = t_food.getText().toString().trim();
                s_name_ = s_name.getText().toString().trim();
                date_ = date.getText().toString().trim();
                shop_ = shop.getText().toString().trim();
                owner_ = owner.getText().toString().trim();
                nic_ = nic.getText().toString().trim();
                num_ = num.getText().toString().trim();
                remarks_ = remarks.getText().toString().trim();
                num_worker_ = num_wroker.getText().toString().trim();


                if ((area.getText().toString().length() == 0)){
                    Toast.makeText(Home.this, "Enter Area", Toast.LENGTH_SHORT).show();
                    area.setError("Enter Area");
                    area.setFocusableInTouchMode(true);
                    area.setFocusable(true);
                    area.requestFocus();}

                else if ((t_food.getText().toString().length() == 0)){
                    Toast.makeText(Home.this, "Enter Food Type", Toast.LENGTH_SHORT).show();
                    t_food.setError("Enter Food Type");
                    t_food.setFocusableInTouchMode(true);
                    t_food.setFocusable(true);
                    t_food.requestFocus();}

                else if ((s_name.getText().toString().length() == 0)){
                    Toast.makeText(Home.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    s_name.setError("Enter Name");
                    s_name.setFocusableInTouchMode(true);
                    s_name.setFocusable(true);
                    s_name.requestFocus();}

                else if ((shop.getText().toString().length() == 0)){
                    Toast.makeText(Home.this, "Enter Shop Name", Toast.LENGTH_SHORT).show();
                    shop.setError("Enter Shop Name");
                    shop.setFocusableInTouchMode(true);
                    shop.setFocusable(true);
                    shop.requestFocus();}

                else if ((owner.getText().toString().length() == 0)){
                    Toast.makeText(Home.this, "Enter Shop Owner Name", Toast.LENGTH_SHORT).show();
                    owner.setError("Enter Shop Owner Name");
                    owner.setFocusableInTouchMode(true);
                    owner.setFocusable(true);
                    owner.requestFocus();}


                else if ((!nic_.equals("") &&
                        nic.getText().toString().trim().length() < 13)){
                    Toast.makeText(Home.this, "Enter Valid CNIC", Toast.LENGTH_SHORT).show();
                    nic.setError("Enter Valid CNIC");
                    nic.setFocusableInTouchMode(true);
                    nic.setFocusable(true);
                    nic.requestFocus();}


                else if ((num.getText().toString().length() == 0 ||
                                num.getText().toString().trim().length() < 11)){
                    Toast.makeText(Home.this, "Enter Valid Number", Toast.LENGTH_SHORT).show();
                    num.setError("Enter Valid Number");
                    num.setFocusableInTouchMode(true);
                    num.setFocusable(true);
                    num.requestFocus();}

                else if ((num_wroker.getText().toString().length() == 0 ||
                                num_worker_.equals("0"))){
                    Toast.makeText(Home.this, "Enter Number of Workers", Toast.LENGTH_SHORT).show();
                    num_wroker.setError("Enter Number of Workers");
                    num_wroker.setFocusableInTouchMode(true);
                    num_wroker.setFocusable(true);
                    num_wroker.requestFocus();}

                
                else if(all.equals(",,,,,,,")){
                    c1.setFocusableInTouchMode(true);
                    c1.setFocusable(true);
                    c1.requestFocus();
                    Toast.makeText(Home.this, "Select At least 1 option", Toast.LENGTH_SHORT).show();}


                else if ((flag == 0 && other.getText().toString().equals(""))){
                    Toast.makeText(Home.this, "Enter Other Food Type", Toast.LENGTH_SHORT).show();
                    other.setError("Enter Other Food Type");
                    other.setFocusableInTouchMode(true);
                    other.setFocusable(true);
                    other.requestFocus();}


                else if (lati == 0 || longi == 0) {
                    check_gps();
                    Toast.makeText(Home.this, "Cant Get Your Location Try Again After A Few Seconds", Toast.LENGTH_SHORT).show();
                } else {

                    if (new Check_internet_connection(getApplicationContext()).isNetworkAvailable()) {

                        if (myBitmap != null) {

                            if (nic_.equals("")) {
                                nic_ = "N/A";
                            }
                            if (remarks_.equals("")){
                                remarks_ = "N/A";
                            }

                            ///send data
                            new RegisterUser().execute();

                        } else {
                            Toast.makeText(Home.this, "No image taken", Toast.LENGTH_SHORT).show();
                        }


                    } else {

                        if (myBitmap != null) {

                            if (nic_.equals("")) {

                                nic_ = "N/A";
                            }

                            dialog_sql();
                            Toast.makeText(getApplicationContext(),
                                    "Check your Internet Connection & Try again", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Home.this, "Select image", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
//                }
            }
        });



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView test = (TextView) header.findViewById(R.id.s_name);
        test.setText(surv_name + "\t" + surv_name2);
        navigationView.setNavigationItemSelectedListener(this);
    }



//////////////////////////////////////////////////////////////
String server_response="0"
        ,server_response_text;
    public class RegisterUser extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(Home.this);
            progressDialog.setTitle("Uploading!");
            progressDialog.setCancelable(false);
            progressDialog.show();

            submit.setEnabled(false);

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                JSONObject obj     = new JSONObject();

                obj.put("operation","survey");

                obj.put("lat", lati);
                obj.put("lon", longi);
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

                    other.setVisibility(View.GONE);
                    flag = 1;
//                    Toast.makeText(Home.this, "Uploading Data!",
//                            Toast.LENGTH_SHORT).show();


                    ImageUploadToServerFunction();



                }else {
                    dialog_sql();
                    Toast.makeText(Home.this, server_response_text, Toast.LENGTH_SHORT).show();

                }

            }else {

                dialog_sql();
                Toast.makeText(Home.this, "Internet Issue", Toast.LENGTH_SHORT).show();
            }

        }
    }







    //saving the name to local storage
    private void saveNameToLocalStorage(String _area, String _t_food, String _name, String _shop,
                                        String _owner, String _nic, String _num, String _lat,
                                        String _lon, String _num_workers, String _all, String _remarks,
                                        int status, String img_path, String date) {

        int id=0, uid = Integer.parseInt(u_id);

        db.addName(_area, _t_food, _name, _shop, _owner, _nic, _num, _lat, _lon, _num_workers,
                _all,_remarks,0, saved_image_path, currentDateTimeString, uid);
        Name n = new Name(_area, _t_food, _name, _shop, _owner, _nic, _num, _lat, _lon, _num_workers,
                _all,_remarks,0,id,saved_image_path, currentDateTimeString);
        names.add(n);

        area.setText("");
        t_food.setText("");
        shop.setText("");
        owner.setText("");
        nic.setText("");
        num.setText("");
        c1.setChecked(false);
        c2.setChecked(false);
        c3.setChecked(false);
        c4.setChecked(false);
        c5.setChecked(false);
        c6.setChecked(false);
        c7.setChecked(false);
        c8.setChecked(false);
        c9.setChecked(false);
        other.setText("");
        num_wroker.setText("");
        remarks.setText("");
        imageView.setImageResource(R.drawable.no_image);

        Toast.makeText(this, "Data Stored Offline", Toast.LENGTH_SHORT).show();

    }

//////////////////////////////////////////////////////////////////////////////////////////////

    ProgressDialog progressDialog1;
    //image ulpoading
    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        imageView.buildDrawingCache();
        Bitmap bmap = imageView.getDrawingCache();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {


                progressDialog1 = new ProgressDialog(Home.this);
                progressDialog1.setTitle("Uploading Data!");
                progressDialog1.setCancelable(false);
                progressDialog1.show();

            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog1.dismiss();
                submit.setEnabled(true);

                Toast.makeText(Home.this,"Data Uploaded",Toast.LENGTH_LONG).show();
                area.setText("");
                t_food.setText("");
                shop.setText("");
                owner.setText("");
                nic.setText("");
                num.setText("");
                c1.setChecked(false);
                c2.setChecked(false);
                c3.setChecked(false);
                c4.setChecked(false);
                c5.setChecked(false);
                c6.setChecked(false);
                c7.setChecked(false);
                c8.setChecked(false);
                c9.setChecked(false);
                other.setText("");
                num_wroker.setText("");
                remarks.setText("");
                imageView.setImageResource(R.drawable.no_image);

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

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



    //set orientation of rotated image
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }


        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            if(getPickImageResultUri(data) == null){
                Toast.makeText(Home.this, "null data", Toast.LENGTH_SHORT).show();
            }

            else if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    myBitmap = rotateImageIfRequired(myBitmap, picUri);
//                    myBitmap = getResizedBitmap(myBitmap, 500);

                    imageView.setImageBitmap(myBitmap);
//                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {


                bitmap = (Bitmap) data.getExtras().get("data");

                myBitmap = bitmap;

                if (imageView != null) {
                    imageView.setImageBitmap(myBitmap);
                }

                imageView.setImageBitmap(myBitmap);

            }

        }

    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }



    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }



        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }




    public void check_box_click(){


        if(c1.isChecked())
        {
            cc1 ="Beverages";
        }
        if(c2.isChecked())
        {
            cc2 ="Desserts";
        }
        if(c3.isChecked())
        {
            cc3 ="Snack food";
        }
        if(c4.isChecked())
        {
            cc4 ="Meat items";
        }
        if(c5.isChecked())
        {
            cc5 ="Desi/Traditional food";
        }
        if(c6.isChecked())
        {
            cc6 ="Fast food";
        }
        if(c7.isChecked())
        {
            cc7 ="Chinese food";
        }
        if(c8.isChecked())
        {
            cc8 ="Sea food";
        }

    }



    public  void check_gps(){


        GPSTracker gps = new GPSTracker(Home.this);

//        gps = new GPSTracker(Home.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            lat = gps.getLatitude();
            lon = gps.getLongitude();
//            Toast.makeText(this, lat+"/"+lon, Toast.LENGTH_SHORT).show();
//
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }


    /////////////////////////////////////////////
    ////saving image to interna; storage

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
        }
    }


    public void saveImage_Function(){

        FileOutputStream fos= null;
        File file = getDisc();
        if(!file.exists() && !file.mkdirs()) {
            //Toast.makeText(this, "Can't create directory to store image", Toast.LENGTH_LONG).show();
            //return;
//            Toast.makeText(Home.this, "file not created", Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "FileName"+date+".jpg";
        saved_image_path = file.getAbsolutePath()+"/"+name;
        File new_file = new File(saved_image_path);
        Log.e("[path",saved_image_path);
//        Toast.makeText(Home.this, "new_file created", Toast.LENGTH_LONG).show();

        try {
            fos= new FileOutputStream(new_file);
            Bitmap bitmap = viewToBitmap(imageView, imageView.getWidth(), imageView.getHeight() );
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            Toast.makeText(LoadImage.this, "Save success", Toast.LENGTH_LONG).show();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
//            Toast.makeText(LoadImage.this, "FNF", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);


    }


    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }


    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;}

    public static Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }



    private File getDisc(){
        String t= getCurrentDateAndTime();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "GIS");
    }

////////////////////////////////////////////////////////////



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exit();
//            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

    int id = item.getItemId();
    
    if(id == R.id.nav_home){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
    }else {

        switch (id) {

            case R.id.nav_home:
                Intent h = new Intent(Home.this, Home.class);
                startActivity(h);
                break;
            case R.id.nav_gallery:
                Intent g = new Intent(Home.this, Offline_Data.class);
                startActivity(g);
                break;
            case R.id.nav_import:
                logout();
                break;

        }
    }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void dialog_sql(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Save Data!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setMessage("Do you want to save data offline?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        saveImage_Function();
                        saveNameToLocalStorage(area_,t_food_,s_name_,shop_,owner_,nic_,num_,lati+""
                                ,longi+"",
                        num_worker_,all+"",remarks_,0,saved_image_path, currentDateTimeString);

                        other.setVisibility(View.GONE);
                        flag = 1;

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
                        Intent intent = new Intent(Home.this, Login.class);
                        startActivity(intent);
                        Home.this.finish();
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



    private void exit(){

        //Creating an alert dialog to confirm logout

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Confirmation!");
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setMessage("Are you sure you want to Exit?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Home.this.finish();
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





    //get location





}