package com.example.dzone.gis.Navgation;

/**
 * Created by Belal on 1/27/2017.
 */

public class Name {
    //    area_,t_food_,s_name_,shop_,owner_,nic_,num_,num_worker_,all, remarks_,lat,lon
    private String area, t_food, name, shop, owner, nic, num, lat, lon, num_workers,
            all, remarks,img_path,date;
    private int status,id;

    public Name(String area, String t_food, String name, String shop,
                String owner, String nic, String num, String lat,
                String lon, String _num_workers, String all, String remarks,
                int status, int id, String img_path, String date) {

        this.name = name;
        this.area = area;
        this.t_food = t_food;
        this.shop = shop;
        this.owner = owner;
        this.nic = nic;
        this.num = num;
        this.lat = lat;
        this.lon = lon;
        this.num_workers = _num_workers;
        this.all = all;
        this.id = id;
        this.remarks = remarks;
        this.status = status;
        this.img_path = img_path;
        this.date = date;

    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getArea() {
        return area;
    }

    public String getFood() {
        return t_food;
    }

    public String getShop() {
        return shop;
    }

    public String getOwner() {
        return owner;
    }

    public String getNic() {
        return nic;
    }

    public String getNum() {
        return num;
    }

    public String getNum_workers() {
        return num_workers;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getAll() {
        return all;
    }

    public int getID() {
        return id;
    }

    public String getImg_path() {
        return img_path;
    }

    public String getDate() {
        return date;
    }

}
