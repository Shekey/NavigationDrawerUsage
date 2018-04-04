package com.example.ajdin.navigatiodraer.helpers;

import android.util.Log;

import com.example.ajdin.navigatiodraer.models.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import static android.content.ContentValues.TAG;

/**
 * A representation of shopping cart.
 * <p/>
 * A shopping cart has a map of {@link Saleable} products to their corresponding quantities.
 */
public class Cart implements Serializable {
    private static final long serialVersionUID = 42L;
    private LinkedHashMap<Saleable, Double> cartItemMap = new LinkedHashMap<Saleable, Double>();
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private Double totalQuantity = 0.0;
    /**
     * Add a quantity of a certain {@link Saleable} product to this shopping cart
     *
     * @param sellable the product will be added to this shopping cart
     * @param quantity the amount to be added
     */
    public void add(final Saleable sellable, double quantity,String new_price) {
        Product pr = null;
        if (cartItemMap.containsKey(sellable)) {

                    cartItemMap.put(sellable, cartItemMap.get(sellable) + quantity);
                } else {
            cartItemMap.put(sellable, quantity);
        }

        if (new_price.trim().matches("")){

                totalPrice = totalPrice.add(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
                totalQuantity += quantity;

        }
        else {

            BigDecimal temp_price = BigDecimal.valueOf(Double.valueOf(new_price));
            totalPrice = totalPrice.add(temp_price.multiply(BigDecimal.valueOf(quantity)));
            totalQuantity += quantity;

        }

    }
    private List<CartItem> getCartItems(Cart cart) {
        List<CartItem> cartItems = new ArrayList<CartItem>();


        Map<Saleable, Double> itemMap = cart.getItemWithQuantity();


        for (Map.Entry<Saleable, Double> entry : itemMap.entrySet()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct((Product) entry.getKey());
            cartItem.setQuantity(entry.getValue());
            cartItems.add(cartItem);
        }


        return cartItems;
    }

    public void updateEdit(final Saleable sellable, double quantity,String new_price) {
        if (cartItemMap.containsKey(sellable)) {
            cartItemMap.put(sellable, quantity);

            if (new_price.trim().matches("")) {

                totalPrice = totalPrice.add(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
                totalQuantity = quantity;
            } else {
                BigDecimal temp_price = BigDecimal.valueOf(Double.valueOf(new_price.toString()));
                totalPrice = totalPrice.add(temp_price.multiply(BigDecimal.valueOf(quantity)));

                totalQuantity = quantity;
            }

        }
    }

    /**
     * Set new quantity for a {@link Saleable} product in this shopping cart
     *
     * @param sellable the product which quantity will be updated
     * @param quantity the new quantity will be assigned for the product
     * @throws ProductNotFoundException    if the product is not found in this shopping cart.
     * @throws QuantityOutOfRangeException if the quantity is negative
     */
    public void update(final Saleable sellable, Double quantity) throws ProductNotFoundException, QuantityOutOfRangeException {
        if (!cartItemMap.containsKey(sellable)) throw new ProductNotFoundException();
        if (quantity < 0)
            throw new QuantityOutOfRangeException(quantity + " is not a valid quantity. It must be non-negative.");

        Double productQuantity = cartItemMap.get(sellable);
        BigDecimal productPrice = sellable.getPrice().multiply(BigDecimal.valueOf(productQuantity));

        cartItemMap.put(sellable, quantity);

        totalQuantity = totalQuantity - productQuantity + quantity;
        totalPrice = totalPrice.subtract(productPrice).add(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
    }

    /**
     * Remove a certain quantity of a {@link Saleable} product from this shopping cart
     *
     * @param sellable the product which will be removed
     * @param quantity the quantity of product which will be removed
     * @throws ProductNotFoundException    if the product is not found in this shopping cart
     * @throws QuantityOutOfRangeException if the quantity is negative or more than the existing quantity of the product in this shopping cart
     */
    public void remove(final Saleable sellable, Double quantity) throws ProductNotFoundException, QuantityOutOfRangeException {
        if (!cartItemMap.containsKey(sellable)) throw new ProductNotFoundException();

        Double productQuantity = cartItemMap.get(sellable);

        if (quantity < 0 || quantity > productQuantity)
            throw new QuantityOutOfRangeException(quantity + " is not a valid quantity. It must be non-negative and less than the current quantity of the product in the shopping cart.");

        if (productQuantity == quantity) {
            cartItemMap.remove(sellable);
        } else {
            cartItemMap.put(sellable, productQuantity - quantity);
        }

        totalPrice = totalPrice.subtract(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
        totalQuantity -= quantity;
    }

    /**
     * Remove a {@link Saleable} product from this shopping cart totally
     *
     * @param sellable the product to be removed
     * @throws ProductNotFoundException if the product is not found in this shopping cart
     */
    public void remove(final Saleable sellable) throws ProductNotFoundException {
        if (!cartItemMap.containsKey(sellable)) throw new ProductNotFoundException();

        Double quantity = cartItemMap.get(sellable);
        cartItemMap.remove(sellable);
        totalPrice = totalPrice.subtract(sellable.getPrice().multiply(BigDecimal.valueOf(quantity)));
        totalQuantity -= quantity;
    }

    /**
     * Remove all products from this shopping cart
     */
    public void clear() {
        cartItemMap.clear();
        totalPrice = BigDecimal.ZERO;
        totalQuantity = 0.0;
    }

    /**
     * Get quantity of a {@link Saleable} product in this shopping cart
     *
     * @param sellable the product of interest which this method will return the quantity
     * @return The product quantity in this shopping cart
     * @throws ProductNotFoundException if the product is not found in this shopping cart
     */
    public Double getQuantity(final Saleable sellable) throws ProductNotFoundException {
        if (!cartItemMap.containsKey(sellable)) throw new ProductNotFoundException();
        return cartItemMap.get(sellable);
    }

    /**
     * Get total cost of a {@link Saleable} product in this shopping cart
     *
     * @param sellable the product of interest which this method will return the total cost
     * @return Total cost of the product
     * @throws ProductNotFoundException if the product is not found in this shopping cart
     */
    public BigDecimal getCost(final Saleable sellable) throws ProductNotFoundException {
        if (!cartItemMap.containsKey(sellable)) throw new ProductNotFoundException();
        return sellable.getPrice().multiply(BigDecimal.valueOf(cartItemMap.get(sellable)));
    }

    /**
     * Get total price of all products in this shopping cart
     *
     * @return Total price of all products in this shopping cart
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Get total quantity of all products in this shopping cart
     *
     * @return Total quantity of all products in this shopping cart
     */
    public Double getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * Get set of products in this shopping cart
     *
     * @return Set of {@link Saleable} products in this shopping cart
     */
    public Set<Saleable> getProducts() {
        return cartItemMap.keySet();
    }

    /**
     * Get a map of products to their quantities in the shopping cart
     *
     * @return A map from product to its quantity in this shopping cart
     */
    public LinkedHashMap<Saleable, Double> getItemWithQuantity() {
        LinkedHashMap<Saleable, Double> cartItemMap = new LinkedHashMap<Saleable, Double>();
        cartItemMap.putAll(this.cartItemMap);
        return cartItemMap;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (Entry<Saleable, Double> entry : cartItemMap.entrySet()) {
            strBuilder.append(String.format("Product: %s, Unit Price: %f, Quantity: %d%n", entry.getKey().getName(), entry.getKey().getPrice(), entry.getValue()));
        }
        strBuilder.append(String.format("Total Quantity: %d, Total Price: %f", totalQuantity, totalPrice));

        return strBuilder.toString();
    }
}
