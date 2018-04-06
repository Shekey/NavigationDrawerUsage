package com.example.ajdin.navigatiodraer.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajdin on 7.4.2018..
 */

import java.util.HashMap;
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
        "image",
        "2",
        "artikalId"
})
public class Slike {

    @JsonProperty("0")
    private String _0;
    @JsonProperty("Id")
    private String id;
    @JsonProperty("1")
    private String _1;
    @JsonProperty("image")
    private String image;
    @JsonProperty("2")
    private String _2;
    @JsonProperty("artikalId")
    private String artikalId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Slike() {
    }

    /**
     *
     * @param id
     * @param artikalId
     * @param image
     * @param _0
     * @param _1
     * @param _2
     */
    public Slike(String _0, String id, String _1, String image, String _2, String artikalId) {
        super();
        this._0 = _0;
        this.id = id;
        this._1 = _1;
        this.image = image;
        this._2 = _2;
        this.artikalId = artikalId;
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

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("2")
    public String get2() {
        return _2;
    }

    @JsonProperty("2")
    public void set2(String _2) {
        this._2 = _2;
    }

    @JsonProperty("artikalId")
    public String getArtikalId() {
        return artikalId;
    }

    @JsonProperty("artikalId")
    public void setArtikalId(String artikalId) {
        this.artikalId = artikalId;
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