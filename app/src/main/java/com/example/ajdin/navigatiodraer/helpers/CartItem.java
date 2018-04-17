package com.example.ajdin.navigatiodraer.helpers;


import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;

public class CartItem {
    private String Kupac_ime;
    private Artikli product;
    private Double quantity;
    private double cijena;
    private int Korpa_id;

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public int getKorpa_id() {
        return Korpa_id;
    }

    public void setKorpa_id(int korpa_id) {
        Korpa_id = korpa_id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Artikli getProduct() {
        return product;
    }

    public void setProduct(Artikli product) {
        this.product = product;
    }

    public void setKupac_ime(String kupac_ime) {
        Kupac_ime = kupac_ime;
    }

    public String getKupac_ime() {
        return Kupac_ime;
    }
}
