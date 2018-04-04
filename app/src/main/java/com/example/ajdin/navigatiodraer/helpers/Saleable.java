package com.example.ajdin.navigatiodraer.helpers;

import java.math.BigDecimal;

/**
 * Implements this interface for any product which can be added to shopping cart
 */
public interface Saleable {
    BigDecimal getPrice();
    String getSnizeno();
    String getJM();
    String getCijena();
    String getBarkod();
    String getKategorija();
    String getImageDevice();
    String getDatum_kreiranja();


    String getName();
}
