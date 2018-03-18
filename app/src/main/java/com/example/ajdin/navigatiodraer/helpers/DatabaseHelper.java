package com.example.ajdin.navigatiodraer.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.ajdin.navigatiodraer.models.Product;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        db.execSQL("PRAGMA encoding='UTF-16'");


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA encoding='UTF-16'");
        db.execSQL("CREATE TABLE `Artikli` (\n " +
                "\t`Artikal_id`\t INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`Naziv`\tTEXT NOT NULL UNIQUE DEFAULT '---',\n" +
                "\t`JM`\tTEXT NOT NULL DEFAULT '---',\n" +
                "\t datumkreiranja DATE NOT NULL,\n" +
                "\t isSnizeno INTEGER DEFAULT 0 ,\n"+
                "\t`Cijena`\tTEXT NOT NULL DEFAULT 0,\n" +
                "\t`Kategorija`\tTEXT DEFAULT '----',\n" +
                "\t`Bar_kod`\tTEXT NOT NULL, \n" +
                "\t`ImageUrl`\tTEXT NOT NULL, \n" +
                "\t`ImageDevice`\tTEXT  \n" + ");");

    }
    public void replace(ArrayList<Product> productList){
        SQLiteDatabase db = this.getWritableDatabase();
        for (Product p:productList) {

            db.execSQL("REPLACE INTO Artikli(Naziv,isSnizeno,Cijena,Bar_kod,ImageUrl,ImageDevice,datumkreiranja) VALUES('"+p.getNaziv()+"','"
                    +p.getSnizeno()+"','"
                    +p.getCijena()+"','"
                    +p.getBarkod()+"','"
                    +p.getImageUrl()+"','"+
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/YourFolderName/"+ p.getCijena()+".jpg"+"','"+
                    p.getDatum_kreiranja()+
                    "');");

        }
        ArrayList<Product> allproducts=getAll();
        int sync_product=productList.size();
        String barkod_delete="";
        int database_size=allproducts.size();
        if (sync_product<database_size) {
            for (Product p : allproducts) {
                boolean pronadjen=false;
            for (Product r:productList){
                if (p.getBarkod().equals(r.getBarkod())){
                    pronadjen=true;
                }else{
                    barkod_delete=p.getBarkod();
                }
            }
            if (!pronadjen){
                db.execSQL("DELETE FROM Artikli WHERE Bar_kod ='"+barkod_delete+"';");
                pronadjen=false;
                barkod_delete="";

            }
            }

        }
    }

    public void clearDatabase(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL("drop table Artikli");
        database.execSQL("PRAGMA encoding='UTF-16'");
        database.execSQL("CREATE TABLE `Artikli` (\n " +
                "\t`Artikal_id`\t INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`Naziv`\tTEXT NOT NULL DEFAULT '---',\n" +
                "\t`JM`\tTEXT NOT NULL DEFAULT '---',\n" +
                "\t datumkreiranja REAL  DEFAULT 'julianday()',\n" +
                "\t isSnizeno INTEGER DEFAULT 0 ,\n"+
                "\t`Cijena`\tTEXT NOT NULL DEFAULT 0,\n" +
                "\t`Kategorija`\tTEXT ,\n" +
                "\t`Bar_kod`\tTEXT NOT NULL, \n" +
                "\t`ImageUrl`\tTEXT NOT NULL, \n" +
                "\t`ImageDevice`\tTEXT  \n" + ");");


        database.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("delete database " + Database_name);
        onCreate(db);
    }


    public void ReadFile() throws IOException {


        String csvFile = Environment.getExternalStorageDirectory().toString() + "/racuni/art.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<Product> products = new ArrayList<Product>();
        Product product;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] lista = line.split(cvsSplitBy);
                String cijena = lista[3];
                cijena = cijena.replace(",", ".");
//                InsertArtikal(lista[4], lista[2], lista[1], cijena, null);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    InsertArtikal(products);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done");
    }


    public void UbaciArtikal(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Barkod", p.getBarkod());
        contentValues.put("JM", "---");
        contentValues.put("Kategorija", "---");
        contentValues.put("ImageUrl", "---");
        contentValues.put("Naziv", "---");
        contentValues.put("Cijena", p.getCijena().toString());
        contentValues.put("ImageDevice", "---");
        long result = db.insert("Artikli", null, contentValues);

        Log.d(TAG, "Inserted " + result);
    }


    public void InsertArtikal(ArrayList<Product> products) {
        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String danas =dateFormat.format(date);

        for (Product p : products) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("Bar_kod", "---");
            contentValues.put("JM", "KOM");
            contentValues.put("Kategorija", "----");
            contentValues.put("ImageUrl", p.getImageUrl());
            contentValues.put("datumkreiranja", p.getDatum_kreiranja());
            contentValues.put("Naziv", p.getNaziv());
            contentValues.put("Cijena", p.getCijena());
            contentValues.put("isSnizeno", p.getSnizeno());
            contentValues.put("ImageDevice", Environment.getExternalStorageDirectory().getAbsolutePath() + "/YourFolderName/" + p.getCijena() + ".jpg");
//            contentValues.put("Barkod", p.getBarkod());
//            contentValues.put("JM", p.getJM());
//            contentValues.put("Kategorija", p.getKategorija());
//            contentValues.put("ImageUrl", p.getImageUrl());
//            contentValues.put("Naziv", p.getNaziv());
//            contentValues.put("Cijena", p.getCijena().toString());
//            contentValues.put("ImageDevice", p.getImageDevice());
            long result = db.insert("Artikli", null, contentValues);

            Log.d(TAG, "Inserted " + result);
        }


    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM Artikli";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if (icount > 0) {
            return false;
        } else {
            return true;
        }

    }

    public boolean InsertArtikal(String bar_kod, String jedinica_mjere, String naziv_artikla, String cijena, String ImageDevice, String ImageUrl, String Kategorija) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Barkod", bar_kod);
        contentValues.put("JM", jedinica_mjere);
        contentValues.put("Kategorija", Kategorija);
        contentValues.put("ImageUrl", ImageUrl);
        contentValues.put("Naziv", naziv_artikla);
        contentValues.put("Cijena", cijena);
        contentValues.put("ImageDevice", ImageDevice);
        long result = db.insertWithOnConflict("Artikli", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        //  long result = db.insert("Artikli", null, contentValues);

        if (result == -1) {

            return false;
        } else {

            return true;
        }
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

        return list;
    }
    public boolean razlikaDana(String real){
        SQLiteDatabase db = this.getWritableDatabase();
       Cursor razlike = db.rawQuery("Select julianday()-julianday("+real+")",null);
       razlike.moveToFirst();
       double razlikaCalc= Double.valueOf(razlike.getString(0));
       if (razlikaCalc>7){
           return false;
       }
       else {
           return true;
       }
// select * from Artikli where julianday()-julianday(d1) >7
    }
    public ArrayList<Product> getAllSnizeno() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Product> list = new ArrayList<Product>();
        Cursor productList = db.rawQuery("select * from Artikli WHERE isSnizeno = 1", null);
        if (productList!=null){
        productList.moveToFirst();
        while (!productList.isAfterLast()) {
            Product product = new Product(productList.getString(productList.getColumnIndex("Naziv")), productList.getInt(productList.getColumnIndex("Artikal_id")),
                    productList.getString(productList.getColumnIndex("Bar_kod")), productList.getString(productList.getColumnIndex("JM")), productList.getString(productList.getColumnIndex("Kategorija")), productList.getString(productList.getColumnIndex("Cijena")), productList.getString(productList.getColumnIndex("ImageUrl")), productList.getString(productList.getColumnIndex("ImageDevice")),productList.getString(productList.getColumnIndex("isSnizeno")),productList.getString(productList.getColumnIndex("datumkreiranja")));
            list.add(product);
            productList.moveToNext();
        }
        }
        productList.close();

        return list;
    }
    public void getdatum(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor lista=db.rawQuery("SELECT * from Artikli",null);
        lista.moveToFirst();
        while (!lista.isAfterLast()){
            String datumkrairanja =lista.getString(lista.getColumnIndex("datumkreiranja"));
            lista.moveToNext();
        }

    }
    public ArrayList<String> GetKategorije(){
        SQLiteDatabase db=this.getWritableDatabase();
        ArrayList<String> kategorije =new ArrayList<>();
        Cursor listKategory = db.rawQuery("select distinct Kategorija from Artikli ",null);
        if (listKategory!=null){
            listKategory.moveToFirst();
            while (!listKategory.isAfterLast()){
                String data = listKategory.getString(listKategory.getColumnIndex("Kategorija"));
                kategorije.add(data);
            }
        }
        return kategorije;

    }
    public ArrayList<Product> getAllNEW() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Product> list = new ArrayList<Product>();
        Cursor productList = db.rawQuery("select * from Artikli WHERE julianday()-julianday(datumkreiranja)<=4", null);
        productList.moveToFirst();
        while (!productList.isAfterLast()) {

            Product product = new Product(productList.getString(productList.getColumnIndex("Naziv")), productList.getInt(productList.getColumnIndex("Artikal_id")),
                    productList.getString(productList.getColumnIndex("Bar_kod")), productList.getString(productList.getColumnIndex("JM")), productList.getString(productList.getColumnIndex("Kategorija")), productList.getString(productList.getColumnIndex("Cijena")), productList.getString(productList.getColumnIndex("ImageUrl")), productList.getString(productList.getColumnIndex("ImageDevice")),productList.getString(productList.getColumnIndex("isSnizeno")),productList.getString(productList.getColumnIndex("datumkreiranja")));
            list.add(product);
            productList.moveToNext();
        }
        productList.close();

        return list;
    }


//    public Product getData(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor productList = db.rawQuery("select * from Artikli  where Bar_kod='" + id + "'", null);
//        if (productList.getCount()==0){
//            Product product=null;
//            return product;
//        }
//        productList.moveToFirst();
//        BigDecimal decimal= BigDecimal.valueOf(Double.valueOf(productList.getString(3)));
////        Product product=new Product();
//        db.close();
//        return product;
//
//    }

    public void update_database(String bar_kod, String jedinica_mjere, String naziv_artikla, String cijena,String ImageUrl, String Kategorija){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Artikli " +

                "Naziv ='"+naziv_artikla+"' , "+
                "Cijena ='"+ cijena+"' , "+
                "ImageUrl = '"+ImageUrl+"' , "+

                "WHERE Bar_kod='"+bar_kod+"'");
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from Artikli ", null);
        if (res.moveToFirst()) {
            do {
                String data = res.getString(res.getColumnIndex("data"));
                // do what ever you want here
            } while (res.moveToNext());
        }
        res.close();

        return res;

    }


    public void showMessage(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
}