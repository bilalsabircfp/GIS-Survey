package com.example.dzone.gis.Navgation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dzone.gis.JsonParser.JsonParser;
import com.example.dzone.gis.R;
import com.example.dzone.gis.url.Url;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by Belal on 1/27/2017.
 */

public class NameAdapter extends ArrayAdapter<Name> {

    //storing all the names in the list
    private List<Name> names;
    private Context context;
    Name name;int status_,id_;

    //constructor
    public NameAdapter(Context context, int resource, List<Name> names) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
    }


    public Name getItem(int position) {
        return names.get(position);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem            = inflater.inflate(R.layout.names, null, true);
        TextView textViewShopName    =  listViewItem.findViewById(R.id.textViewName);
        TextView textViewAreaName2   =  listViewItem.findViewById(R.id.textViewAddress);
        TextView date                =  listViewItem.findViewById(R.id.date);
        ImageView imageViewStatus    = listViewItem.findViewById(R.id.imageViewStatus);
        ImageView imageShop          =  listViewItem.findViewById(R.id.shop_img);
        Button send                  = listViewItem.findViewById(R.id.send);

        //getting the current name
        name = names.get(position);

        //setting the name to textview
        textViewShopName.setText(name.getShop());
        textViewAreaName2.setText(name.getArea());
        date.setText(name.getDate());

        String path = name.getImg_path();

        File imgFile = new  File(path);

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    imageShop.setImageBitmap(myBitmap);
                }
                else {
                    imageShop.setImageResource(R.drawable.no_image);
//                    Toast.makeText(Offline_Data.this, "not found", Toast.LENGTH_SHORT).show();
                }



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((ListView)parent).performItemClick(v,position,1);
            }
        });


        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (name.getStatus() == 1)
            imageViewStatus.setBackgroundResource(R.drawable.success);
        else
            imageViewStatus.setBackgroundResource(R.drawable.stopwatch);

        return listViewItem;
    }




}
