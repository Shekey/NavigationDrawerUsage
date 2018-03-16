package com.example.ajdin.navigatiodraer.helpers;

import android.content.Context;


import com.example.ajdin.navigatiodraer.models.Product;

import java.util.ArrayList;
import java.util.List;

public final class Constant {
    public static final List<Integer> QUANTITY_LIST = new ArrayList<Integer>();
    Context ctx;


    static {
        for (int i = 1; i < 11; i++) QUANTITY_LIST.add(i);
    }

  //  public static final Product PRODUCT1 = new Product(1, "Samsung Galaxy S6", BigDecimal.valueOf(199.996), "barKOd1", "100","KOM");
   //public static final Product PRODUCT2 = new Product(2, "HTC One M8", BigDecimal.valueOf(449.9947), "barkod2","1001", "KOM");
    //public static final Product PRODUCT3 = new Product(3, "Xiaomi Mi3", BigDecimal.valueOf(319.998140), "barkod3", "201","KOM");

    public static final List<Product> PRODUCT_LIST = new ArrayList<Product>();



 //static {
   //     PRODUCT_LIST.add(PRODUCT1);
     //  PRODUCT_LIST.add(PRODUCT2);
     //PRODUCT_LIST.add(PRODUCT3);
 //}

    public static final String CURRENCY = "KM";
}
