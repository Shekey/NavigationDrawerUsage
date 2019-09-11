package com.example.ajdin.navigatiodraer.helpers;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.models.Slike;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ajdin on 6.3.2018..
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public SQLiteDatabase dbb;
    public final String TAG = "DATABASE HELPER";
    public static final String Table_name = "Artikal";
    public static final String Bar_kod = "bar_kod";
    public static final String Cijena = "cijena";
    public static final String ID = "id";
    public static final String Naziv_artikla = "naziv";
    public static final String Zaliha = "zaliha";
    public static final String Jedinica_mjere = "jedinica_mjere";
    public static final String Database_name = "NUR.db";
    public static final int Database_version = 1;
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    // racun create
    public static String Table_Name_racun = "Racun";
    public static String RACUN_ID = "racun_id";
    public static String Datum_Izdavanja = "racun_datum";
    public static String Kupac = "racun_kupac";
    public static String Iznos_racuna = "iznos_racuna";


    ////


    public DatabaseHelper(Context context) {
        super(context, Database_name, null, Database_version);
        dbb = this.getWritableDatabase();

    }

    //
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("PRAGMA synchronous = 'OFF'");
        db.execSQL("PRAGMA temp_store = 'MEMORY'");
        db.execSQL("PRAGMA cache_size = '500000'");
        db.execSQL("PRAGMA encoding='UTF-8'");


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA encoding='UTF-8'");
        db.execSQL("CREATE TABLE `Artikli` (\n " +
                "\t`Artikal_id`\t INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`Naziv`\tTEXT NOT NULL DEFAULT '---',\n" +
                "\t`Id`\tTEXT NOT NULL UNIQUE ,\n" +
                "\t`JM`\tTEXT NOT NULL DEFAULT '---',\n" +
                "\t datumkreiranja DATE NOT NULL,\n" +
                "\t isSnizeno INTEGER DEFAULT 0 ,\n"+
                "\t`Cijena`\tTEXT NOT NULL DEFAULT 0,\n" +
                "\t`Stanje`\tTEXT NOT NULL DEFAULT 0,\n" +
                "\t`Kategorija`\tTEXT DEFAULT '----',\n" +
                "\t`Bar_kod`\tTEXT NOT NULL \n" + ");");

       // db.execSQL("CREATE TABLE `Artikli` (\n " +
        //        "\t`Artikal_id`\t INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
        //       "\t`Naziv`\tTEXT NOT NULL DEFAULT '---',\n" +
           //     "\t`JM`\tTEXT NOT NULL DEFAULT '---',\n" +
             //   "\t datumkreiranja DATE NOT NULL,\n" +
            //    "\t isSnizeno INTEGER DEFAULT 0 ,\n"+
             //   "\t`Cijena`\tTEXT NOT NULL DEFAULT 0,\n" +
             //   "\t`Kategorija`\tTEXT DEFAULT '----',\n" +
             //   "\t`Bar_kod`\tTEXT NOT NULL UNIQUE, \n" +
             //   "\t`ImageUrl`\tTEXT NOT NULL, \n" +
               // "\t`ImageDevice`\tTEXT  \n" + ");");

        db.execSQL("CREATE TABLE `FileStack` (\n " +
                "\t`Stack_id`\t INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`Path`\tTEXT NOT NULL DEFAULT '---',\n" +
                "\t Synced INTEGER DEFAULT 0 "+");");

        db.execSQL("CREATE TABLE `Images` (\n " +
                "\t`ImageId`\t INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`ImagePath`\tTEXT NOT NULL ,\n" +
                "\t`IdSlika`\tTEXT NOT NULL UNIQUE,\n" +
                "\t Artikal_ID INTEGER not null "+");");

    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ "Images");
        db.execSQL("delete from "+ "Artikli");
        db.execSQL("delete from "+ "FileStack");
        db.close();
    }
    public void addToStack(String path){
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("Path",path );
            contentValues.put("Synced", "0");

            long result = db.insert("FileStack", null, contentValues);
        //here
        db.close();

            Log.d(TAG, "Inserted " + result);
        }
    public ArrayList<String> getAllStacked() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> list = new ArrayList<String>();
        Cursor productList = db.rawQuery("select * from FileStack WHERE Synced=0 ", null);
        productList.moveToFirst();
        while (!productList.isAfterLast()) {
            list.add(productList.getString(productList.getColumnIndex("Path")));
            productList.moveToNext();
        }
        productList.close();
        //here
        db.close();
        return list;
    }
    public void setStacked(String path){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE FileStack SET Synced = 1"+" WHERE Path='"+path+"'");
        //here
        db.close();
    }

    public ArrayList<Slike> replaceSlike(List<Slike> productList)  {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Slike> downloadSlike=new ArrayList<>();
        int pocetniSize=getAllSlike().size();
        ArrayList<Slike> sveSlike=getAllSlike();
        if (productList.size()>pocetniSize) {

//
            for (Slike s : productList) {


                db.execSQL("REPLACE INTO Images(ImagePath,IdSlika,Artikal_ID) VALUES('" + s.getImage() + "','" + s.getId() + "','" + s.getArtikalId() + " ');");
                int trenutniSize = getAllSlike().size();
                if (trenutniSize == pocetniSize+1) {
                    downloadSlike.add(s);
                    pocetniSize++;
                }

            }
        }


        int sync_product=productList.size();
        String id_delete="";
        String id_image="";

        int database_size=sveSlike.size();
        if (sync_product<database_size) {
            for (Slike p : sveSlike) {
                boolean pronadjen=false;
                for (Slike r:productList){
                    if (p.getImage().equals(r.getImage())){
                        pronadjen=true;
                        break;
                    }else{
                        id_delete=p.getId();

                    }
                }
                if (!pronadjen){

                    db.execSQL("DELETE FROM Images WHERE IdSlika ='"+id_delete+"';");

                   pronadjen=false;
                    id_delete="";

                }
            }

        }
        //here
        db.close();
        return downloadSlike;

    }

    public void replace1(ArrayList<Artikli> productList){
        SQLiteDatabase db = this.getWritableDatabase();

        //518
        for (Artikli p:productList) {

            db.execSQL("REPLACE INTO Artikli(Naziv,isSnizeno,Cijena,Bar_kod,datumkreiranja,Stanje,Kategorija,Id,JM) VALUES('"+p.getNaziv()+"','"
                    +p.getSnizeno()+"','"
                    +p.getCijena()+"','"
                    +p.getBarkod()+"','"
                    +p.getDatum()+"','"+p.getStanje()+"','"+p.getKategorija()+"','"+
                    p.getId()+"','"+p.getJedinica()+" ');");



        }



        ArrayList<Artikli> allproducts=getAll1();//518
        int sync_product=productList.size();
        String id_delete="";
        int database_size=allproducts.size();
        if (sync_product<database_size) {
            for (Artikli p : allproducts) {
                boolean pronadjen=false;
                for (Artikli r:productList){
                    if (p.getBarkod().equals(r.getBarkod())){
                        pronadjen=true;
                    }else{
                        id_delete=p.getId();
                    }
                }
                if (!pronadjen){

                    db.execSQL("UPDATE  Images SET ImagePath=1 WHERE Artikal_ID ='"+id_delete+"';");
                    db.execSQL("DELETE FROM Artikli WHERE Id ='"+id_delete+"';");
                    pronadjen=false;
                    id_delete="";

                }
            }

        }
        //here
        db.close();
    }

    public Artikli getData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor productList = db.rawQuery("select * from Artikli  where Bar_kod='" + id + "'", null);
        if (productList.getCount()==0){
            Artikli product=null;
            return product;
        }
        productList.moveToFirst();
        Cursor slike = db.rawQuery("select * from Images WHERE Artikal_ID="+productList.getString(productList.getColumnIndex("Id")), null);
        slike.moveToFirst();
        List<Slike> images=new ArrayList<Slike>();
        while (!slike.isAfterLast()) {
            Slike slike1=new Slike(slike.getString(slike.getColumnIndex("ImagePath")),slike.getString(slike.getColumnIndex("Artikal_ID")),slike.getString(slike.getColumnIndex("IdSlika")));
            images.add(slike1);
            slike.moveToNext();
        }
        slike.close();
        Artikli product = new Artikli(productList.getString(productList.getColumnIndex("Naziv")),
                productList.getString(productList.getColumnIndex("Bar_kod")),
                productList.getString(productList.getColumnIndex("Id")),
                productList.getString(productList.getColumnIndex("isSnizeno")),
                productList.getString(productList.getColumnIndex("Stanje")),
                productList.getString(productList.getColumnIndex("datumkreiranja")),
                productList.getString(productList.getColumnIndex("Kategorija")),
                productList.getString(productList.getColumnIndex("JM"))
                ,images,productList.getString(productList.getColumnIndex("Cijena")));

        db.close();
        return product;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("delete database " + Database_name);
        //here
        db.close();
        onCreate(db);
    }

    public void InsertArtikal1(ArrayList<Artikli> products) {

    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Artikli";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        return icount <= 0;

    }

    public ArrayList<Slike> getAllSlike(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Slike> listaSlike = new ArrayList<Slike>();
        Cursor productList = db.rawQuery("select * from Images", null);
        productList.moveToFirst();
        while (!productList.isAfterLast()) {
            Slike slike1=new Slike(productList.getString(productList.getColumnIndex("ImagePath")),productList.getString(productList.getColumnIndex("Artikal_ID")),productList.getString(productList.getColumnIndex("IdSlika")));
            listaSlike.add(slike1);
            productList.moveToNext();

        }
        productList.close();
       return listaSlike;



    }
    public ArrayList<Slike> getAllSlikeForDelete(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Slike> listaSlike = new ArrayList<Slike>();
        Cursor productList = db.rawQuery("select * from Images WHERE ImagePath=1", null);
        productList.moveToFirst();
        while (!productList.isAfterLast()) {
            Slike slike1=new Slike(productList.getString(productList.getColumnIndex("ImagePath")),productList.getString(productList.getColumnIndex("Artikal_ID")),productList.getString(productList.getColumnIndex("IdSlika")));
            listaSlike.add(slike1);
            productList.moveToNext();

        }
        productList.close();
        return listaSlike;



    }
    public ArrayList<Artikli> getAll1() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Artikli> list = new ArrayList<Artikli>();
        Cursor productList = db.rawQuery("select * from Artikli", null);

        productList.moveToFirst();

        while (!productList.isAfterLast()) {
            Cursor slike = db.rawQuery("select * from Images WHERE Artikal_ID="+productList.getString(productList.getColumnIndex("Id")), null);
            slike.moveToFirst();
            List<Slike> images=new ArrayList<Slike>();
            while (!slike.isAfterLast()) {
                Slike slike1=new Slike(slike.getString(slike.getColumnIndex("ImagePath")),slike.getString(slike.getColumnIndex("Artikal_ID")),slike.getString(slike.getColumnIndex("IdSlika")));
                images.add(slike1);
                slike.moveToNext();
            }
            slike.close();
            Artikli product = new Artikli(productList.getString(productList.getColumnIndex("Naziv")),
                    productList.getString(productList.getColumnIndex("Bar_kod")),
                    productList.getString(productList.getColumnIndex("Id")),
                    productList.getString(productList.getColumnIndex("isSnizeno")),
                    productList.getString(productList.getColumnIndex("Stanje")),
                    productList.getString(productList.getColumnIndex("datumkreiranja")),
                    productList.getString(productList.getColumnIndex("Kategorija")),
                    productList.getString(productList.getColumnIndex("JM"))
                    ,images,productList.getString(productList.getColumnIndex("Cijena")));
            list.add(product);
            productList.moveToNext();
        }
        productList.close();


        return list;
    }
    public ArrayList<Product> getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Product> list = new ArrayList<Product>();
        Cursor productList = db.rawQuery("select * from Artikli", null);
        productList.moveToFirst();
        while (!productList.isAfterLast()) {
            Product product = new Product(productList.getString(productList.getColumnIndex("Naziv")), productList.getInt(productList.getColumnIndex("Artikal_id")),
                    productList.getString(productList.getColumnIndex("Bar_kod")), productList.getString(productList.getColumnIndex("JM")), productList.getString(productList.getColumnIndex("Kategorija")), productList.getString(productList.getColumnIndex("Cijena")), productList.getString(productList.getColumnIndex("ImageUrl")), productList.getString(productList.getColumnIndex("ImageDevice")),productList.getString(productList.getColumnIndex("isSnizeno")),productList.getString(productList.getColumnIndex("datumkreiranja")));
            list.add(product);
            productList.moveToNext();
        }
        productList.close();
        //here
        db.close();
        return list;
    }
    public ArrayList<Artikli> getAllSnizenoArtikli() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Artikli> list = new ArrayList<Artikli>();
        Cursor productList = db.rawQuery("select * from Artikli WHERE isSnizeno = 1", null);
        if (productList!=null){
            productList.moveToFirst();
            while (!productList.isAfterLast()) {
                Cursor slike = db.rawQuery("select * from Images WHERE Artikal_ID="+productList.getString(productList.getColumnIndex("Id")), null);
                slike.moveToFirst();
                List<Slike> images=new ArrayList<Slike>();
                while (!slike.isAfterLast()) {
                    Slike slike1=new Slike(slike.getString(slike.getColumnIndex("ImagePath")),slike.getString(slike.getColumnIndex("Artikal_ID")),slike.getString(slike.getColumnIndex("IdSlika")));
                    images.add(slike1);
                    slike.moveToNext();
                }
                slike.close();
                Artikli product = new Artikli(productList.getString(productList.getColumnIndex("Naziv")),
                        productList.getString(productList.getColumnIndex("Bar_kod")),
                        productList.getString(productList.getColumnIndex("Id")),
                        productList.getString(productList.getColumnIndex("isSnizeno")),
                        productList.getString(productList.getColumnIndex("Stanje")),
                        productList.getString(productList.getColumnIndex("datumkreiranja")),
                        productList.getString(productList.getColumnIndex("Kategorija")),
                        productList.getString(productList.getColumnIndex("JM")),
                        images,
                        productList.getString(productList.getColumnIndex("Cijena")));
                list.add(product);
                productList.moveToNext();
            }
        }
        productList.close();
        //here
        db.close();
        return list;
    }
    public List<Artikli> getProductsKategoryFiltered1(String kategory,List<Artikli> filtered){
        List<Artikli> labels = new ArrayList<Artikli>();


        // Select All Query

      for (Artikli p:filtered){
          if (p.getKategorija().equals(kategory))
              labels.add(p);
      }

        // returning lables
        return labels;
    }
    public List<String> getAllLabels(){
        List<String> labels = new ArrayList<String>();

        // Select All Query

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct Kategorija from Artikli",null);
        labels.add("Izaberite kategoriju");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
    public ArrayList<Artikli> getAllNEWArtikli() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Artikli> list = new ArrayList<Artikli>();
        Cursor productList = db.rawQuery("select * from Artikli WHERE julianday()-julianday(datumkreiranja)<=15", null);
        productList.moveToFirst();
        while (!productList.isAfterLast()) {
            Cursor slike = db.rawQuery("select * from Images WHERE Artikal_ID="+productList.getString(productList.getColumnIndex("Id")), null);
            slike.moveToFirst();
            List<Slike> images=new ArrayList<Slike>();
            while (!slike.isAfterLast()) {
                Slike slike1=new Slike(slike.getString(slike.getColumnIndex("ImagePath")),slike.getString(slike.getColumnIndex("Artikal_ID")),slike.getString(slike.getColumnIndex("IdSlika")));
                images.add(slike1);
                slike.moveToNext();
            }
            slike.close();
            Artikli product = new Artikli(productList.getString(productList.getColumnIndex("Naziv")),
                    productList.getString(productList.getColumnIndex("Bar_kod")),
                    productList.getString(productList.getColumnIndex("Id")),
                    productList.getString(productList.getColumnIndex("isSnizeno")),
                    productList.getString(productList.getColumnIndex("Stanje")),
                    productList.getString(productList.getColumnIndex("datumkreiranja")),
                    productList.getString(productList.getColumnIndex("Kategorija")),
                    productList.getString(productList.getColumnIndex("JM")),
                    images,
                    productList.getString(productList.getColumnIndex("Cijena")));
            list.add(product);
            productList.moveToNext();
        }
        productList.close();
        //here
        db.close();
        return list;
    }
}