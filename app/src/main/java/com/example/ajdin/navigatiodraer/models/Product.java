package com.example.ajdin.navigatiodraer.models;

import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by ajdin on 6.3.2018..
 */
public class Product implements Saleable, Serializable {
    @SerializedName("naziv")
    private String Naziv;
    private int Artikal_id;
    @SerializedName("barkod")
    private String Barkod;
    private String JM;
    @SerializedName("Naziv")
    private String KategoriaId;
    @SerializedName("cijena")
    private String Cijena;
    @SerializedName("image")
    private String ImageUrl;
    private String ImageDevice;
    @SerializedName("snizeno")
    private String snizeno;
    @SerializedName("datum_kreiranja")
    private String datum_kreiranja;

    public String getDatum_kreiranja() {
        return datum_kreiranja;
    }

    public void setDatum_kreiranja(String datum_kreiranja) {
        this.datum_kreiranja = datum_kreiranja;
    }

    public String getSnizeno() {
        return snizeno;
    }

    public void setSnizeno(String snizeno) {
        this.snizeno = snizeno;
    }

    public Product(String naziv, int artikal_id, String barkod, String JM, String kategorija, String cijena, String imageUrl, String imageDevice, String snizeno,String datum_kreiranja) {
        Naziv = naziv;
        Artikal_id = artikal_id;
        Barkod = barkod;
        this.JM = JM;
        KategoriaId = kategorija;
        this.snizeno=snizeno;

        Cijena = cijena;
        ImageUrl = imageUrl;
        ImageDevice = imageDevice;
        this.datum_kreiranja=datum_kreiranja;
    }

    public String getNaziv() {
        return Naziv;
    }

    public void setNaziv(String naziv) {
        Naziv = naziv;
    }

    public int getArtikal_id() {
        return Artikal_id;
    }

    public void setArtikal_id(int artikal_id) {
        Artikal_id = artikal_id;
    }

    public String getBarkod() {
        return Barkod;
    }

    public void setBarkod(String barkod) {
        Barkod = barkod;
    }

    public String getJM() {
        return JM;
    }

    public void setJM(String JM) {
        this.JM = JM;
    }

    public String getKategorija() {
        return KategoriaId;
    }

    public void setKategorija(String kategorija) {
        KategoriaId = kategorija;
    }

    public String getCijena() {
        return Cijena;
    }

    public void setCijena(String cijena) {
        Cijena = cijena;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getImageDevice() {
        return ImageDevice;
    }

    public void setImageDevice(String imageDevice) {
        ImageDevice = imageDevice;
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal pprice=BigDecimal.valueOf(Double.valueOf(this.Cijena));
        return pprice;
    }

    @Override
    public String getName() {
        return Naziv;
    }
}
