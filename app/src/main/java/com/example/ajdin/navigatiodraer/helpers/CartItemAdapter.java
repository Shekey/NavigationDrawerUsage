package com.example.ajdin.navigatiodraer.helpers;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.ajdin.navigatiodraer.R;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CartItemAdapter extends BaseAdapter {
    private static final String TAG = "CartItemAdapter";

    public List<CartItem> cartItems = Collections.emptyList();

    private final Context context;

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public CartItemAdapter(Context context) {
        this.context = context;
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public CartItem getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView tvName;
        ImageView ivMovieIcon;
        TextView tvTotalPrice;
        TextView tvPrice;
        TextView tvKolicina;

//adapter_cart_item layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_cart_item, parent, false);
            tvName = (TextView) convertView.findViewById(R.id.tvCartItemName);
            tvKolicina = (TextView) convertView.findViewById(R.id.tvKolicina);
            ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivIconcart);
            tvPrice=(TextView) convertView.findViewById(R.id.tvPrice);

//            tvUnitPrice = (TextView) convertView.findViewById(R.id.tvCartItemUnitPrice);
//            tvQuantity = (TextView) convertView.findViewById(R.id.tvCartItemQuantity);
                tvTotalPrice = (TextView) convertView.findViewById(R.id.tvCartItemTotalPrice);
            convertView.setTag(new ViewHolder(tvName, tvTotalPrice,tvKolicina,tvPrice,ivMovieIcon));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            tvName = viewHolder.tvCartItemName;
            ivMovieIcon=viewHolder.ivMovieIcon;
            tvPrice=viewHolder.tvCartItemPrice;
            tvTotalPrice = viewHolder.tvCartItemTotalPrice;
            tvKolicina = viewHolder.tvKolicina;
        }
        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBarcart);
        final Cart cart = CartHelper.getCart();

        final CartItem cartItem = getItem(position);
        tvName.setText(cartItem.getProduct().getName());
        tvKolicina.setText("Količina: "+cartItem.getQuantity().toString()+" "+cartItem.getProduct().getJM());
        tvPrice.setText("Cijena :"+String.valueOf(cartItem.getProduct().getCijena())+" KM");
        tvTotalPrice.setText("Ukupno - " +String.valueOf(cart.getCost(cartItem.getProduct()).setScale(2, BigDecimal.ROUND_HALF_UP)+" KM"));
        File file = new File(cartItem.getProduct().getImageDevice());
        ivMovieIcon.setImageURI(Uri.parse(file.getAbsolutePath()));
        progressBar.setVisibility(View.GONE);
        return convertView;
    }

    private static class ViewHolder {
        public final TextView tvCartItemName;
        public final TextView tvKolicina;
        private ImageView ivMovieIcon;
        public final TextView tvCartItemTotalPrice;
        public final TextView tvCartItemPrice;

        public ViewHolder(TextView tvCartItemName, TextView tvCartItemTotalPrice, TextView tvKolicina,TextView tvCartItemPrice, ImageView ivMovieIcon) {
            this.tvCartItemName = tvCartItemName;
            this.tvKolicina = tvKolicina;
            this.ivMovieIcon=ivMovieIcon;
            this.tvCartItemPrice=tvCartItemPrice;
            this.tvCartItemTotalPrice = tvCartItemTotalPrice;
        }
    }
}
