package com.example.ajdin.navigatiodraer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ajdin.navigatiodraer.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ajdin on 30.3.2018..
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> images;

    public ViewPagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =inflater.inflate(R.layout.custom_layout,null);
        ImageView view1= view.findViewById(R.id.imageView2);

        File file=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Environment.DIRECTORY_PICTURES,
                File.separator + "YourFolderName" + File.separator+images.get(position)+".jpg");
        if (file.exists()) {
            Picasso
                    .with(context)
                    .load(file)
                    .fit()
                    .centerInside()
                    .into(view1);

        }
        else {
            view1.setImageResource(R.drawable.nemaslike);
        }


        ViewPager vp= (ViewPager) container;
        vp.addView(view,0);
        return  view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp= (ViewPager) container;
        View view= (View) object;
        vp.removeView(view);

    }
}
