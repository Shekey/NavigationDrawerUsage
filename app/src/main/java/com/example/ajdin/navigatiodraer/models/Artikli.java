package com.example.ajdin.navigatiodraer.models;

/**
 * Created by ajdin on 7.4.2018..
 */


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "0",
        "Id",
        "1",
        "naziv",
        "2",
        "barkod",
        "3",
        "cijena",
        "4",
        "snizeno",
        "5",
        "datum_kreiranja",
        "6",
        "NazivKategorije",
        "slike"
})
public class Artikli {

    @JsonProperty("0")
    private String _0;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("1")
    private String _1;
    @JsonProperty("naziv")
    private String naziv;
    @JsonProperty("2")
    private String _2;
    @JsonProperty("barkod")
    private String barkod;
    @JsonProperty("3")
    private String _3;
    @JsonProperty("cijena")
    private String cijena;
    @JsonProperty("4")
    private String _4;
    @JsonProperty("snizeno")
    private String snizeno;
    @JsonProperty("5")
    private String _5;
    @JsonProperty("datum_kreiranja")
    private String datumKreiranja;
    @JsonProperty("6")
    private String _6;
    @JsonProperty("NazivKategorije")
    private String nazivKategorije;
    @JsonProperty("slike")
    private List<Slike> slike = null;
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
     * @param nazivKategorije
     * @param id
     * @param naziv
     * @param slike
     * @param barkod
     * @param _3
     * @param _4
     * @param _5
     * @param _6
     * @param _0
     * @param cijena
     * @param _1
     * @param snizeno
     * @param _2
     * @param datumKreiranja
     */
    public Artikli(String _0, String id, String _1, String naziv, String _2, String barkod, String _3, String cijena, String _4, String snizeno, String _5, String datumKreiranja, String _6, String nazivKategorije, List<Slike> slike) {
        super();
        this._0 = _0;
        this.id = id;
        this._1 = _1;
        this.naziv = naziv;
        this._2 = _2;
        this.barkod = barkod;
        this._3 = _3;
        this.cijena = cijena;
        this._4 = _4;
        this.snizeno = snizeno;
        this._5 = _5;
        this.datumKreiranja = datumKreiranja;
        this._6 = _6;
        this.nazivKategorije = nazivKategorije;
        this.slike = slike;
    }

    @JsonProperty("0")
    public String get0() {
        return _0;
    }

    @JsonProperty("0")
    public void set0(String _0) {
        this._0 = _0;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("1")
    public String get1() {
        return _1;
    }

    @JsonProperty("1")
    public void set1(String _1) {
        this._1 = _1;
    }

    @JsonProperty("naziv")
    public String getNaziv() {
        return naziv;
    }

    @JsonProperty("naziv")
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    @JsonProperty("2")
    public String get2() {
        return _2;
    }

    @JsonProperty("2")
    public void set2(String _2) {
        this._2 = _2;
    }

    @JsonProperty("barkod")
    public String getBarkod() {
        return barkod;
    }

    @JsonProperty("barkod")
    public void setBarkod(String barkod) {
        this.barkod = barkod;
    }

    @JsonProperty("3")
    public String get3() {
        return _3;
    }

    @JsonProperty("3")
    public void set3(String _3) {
        this._3 = _3;
    }

    @JsonProperty("cijena")
    public String getCijena() {
        return cijena;
    }

    @JsonProperty("cijena")
    public void setCijena(String cijena) {
        this.cijena = cijena;
    }

    @JsonProperty("4")
    public String get4() {
        return _4;
    }

    @JsonProperty("4")
    public void set4(String _4) {
        this._4 = _4;
    }

    @JsonProperty("snizeno")
    public String getSnizeno() {
        return snizeno;
    }

    @JsonProperty("snizeno")
    public void setSnizeno(String snizeno) {
        this.snizeno = snizeno;
    }

    @JsonProperty("5")
    public String get5() {
        return _5;
    }

    @JsonProperty("5")
    public void set5(String _5) {
        this._5 = _5;
    }

    @JsonProperty("datum_kreiranja")
    public String getDatumKreiranja() {
        return datumKreiranja;
    }

    @JsonProperty("datum_kreiranja")
    public void setDatumKreiranja(String datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    @JsonProperty("6")
    public String get6() {
        return _6;
    }

    @JsonProperty("6")
    public void set6(String _6) {
        this._6 = _6;
    }

    @JsonProperty("NazivKategorije")
    public String getNazivKategorije() {
        return nazivKategorije;
    }

    @JsonProperty("NazivKategorije")
    public void setNazivKategorije(String nazivKategorije) {
        this.nazivKategorije = nazivKategorije;
    }

    @JsonProperty("slike")
    public List<Slike> getSlike() {
        return slike;
    }

    @JsonProperty("slike")
    public void setSlike(List<Slike> slike) {
        this.slike = slike;
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