package com.example.ajdin.navigatiodraer.models;

/**
 * Created by ajdin on 7.4.2018..
 */


import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.ajdin.navigatiodraer.helpers.Saleable;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.SerializedName;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Naziv",
        "Barkod",
        "Id",
        "Snizeno",
        "Stanje",
        "Datum",
        "Kategorija",
        "Jedinica",
        "slike",
        "Cijena"
})
public class Artikli implements Serializable,Saleable {

    @SerializedName("Naziv")
    private String naziv;
    @SerializedName("Barkod")
    private String barkod;
    @SerializedName("Id")
    private String id;
    @SerializedName("Snizeno")
    private String snizeno;
    @SerializedName("Stanje")
    private String stanje;
    @SerializedName("Datum")
    private String datum;
    @SerializedName("Kategorija")
    private String kategorija;
    @SerializedName("Jedinica")
    private String jedinica;
    @SerializedName("slike")
    private List<Slike> slike = null;
    @SerializedName("Cijena")
    private String cijena;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Artikli() {
    }

    /**
     *
     * @param jedinica
     * @param id
     * @param naziv
     * @param slike
     * @param barkod
     * @param stanje
     * @param kategorija
     * @param datum
     * @param cijena
     * @param snizeno
     */
    public Artikli(String naziv, String barkod, String id, String snizeno, String stanje, String datum, String kategorija, String jedinica, List<Slike> slike, String cijena) {
        super();
        this.naziv = naziv;
        this.barkod = barkod;
        this.id = id;
        this.snizeno = snizeno;
        this.stanje = stanje;
        this.datum = datum;
        this.kategorija = kategorija;
        this.jedinica = jedinica;
        this.slike = slike;
        this.cijena = cijena;
    }


    public String getNaziv() {
        return naziv;
    }

    @JsonProperty("Naziv")
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    @JsonProperty("Barkod")
    public String getBarkod() {
        return barkod;
    }

    @JsonProperty("Barkod")
    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public BigDecimal getPrice() {
        return BigDecimal.valueOf(Double.valueOf(cijena));
    }

    @JsonProperty("Snizeno")
    public String getSnizeno() {
        return snizeno;
    }

    @Override
    public String getJM() {
        return jedinica;
    }

    @JsonProperty("Snizeno")
    public void setSnizeno(String snizeno) {
        this.snizeno = snizeno;
    }

    @JsonProperty("Stanje")
    public String getStanje() {
        return stanje;
    }

    @JsonProperty("Stanje")
    public void setStanje(String stanje) {
        this.stanje = stanje;
    }

    @JsonProperty("Datum")
    public String getDatum() {
        return datum;
    }

    @JsonProperty("Datum")
    public void setDatum(String datum) {
        this.datum = datum;
    }

    @JsonProperty("Kategorija")
    public String getKategorija() {
        return kategorija;
    }

    @Override
    public String getImageDevice() {
        return null;
    }

    @Override
    public String getDatum_kreiranja() {
        return datum;
    }

    @Override
    public String getName() {
        return naziv;
    }

    @JsonProperty("Kategorija")
    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    @JsonProperty("Jedinica")
    public String getJedinica() {
        return jedinica;
    }

    @JsonProperty("Jedinica")
    public void setJedinica(String jedinica) {
        this.jedinica = jedinica;
    }

    @JsonProperty("slike")
    public List<Slike> getSlike() {
        return slike;
    }

    @JsonProperty("slike")
    public void setSlike(List<Slike> slike) {
        this.slike = slike;
    }

    @JsonProperty("Cijena")
    public String getCijena() {
        return cijena;
    }

    @JsonProperty("Cijena")
    public void setCijena(String cijena) {
        this.cijena = cijena;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}