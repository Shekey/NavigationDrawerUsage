package com.example.ajdin.navigatiodraer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.models.Product;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ajdin on 6.3.2018..
 */

public class MovieAdapter extends ArrayAdapter {

    private List<Product> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public MovieAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.ivMovieIcon = convertView.findViewById(R.id.ivIcon);
            holder.tvMovie = convertView.findViewById(R.id.tvMovie);
            holder.tvTagline = convertView.findViewById(R.id.tvTagline);
            holder.tvYear = convertView.findViewById(R.id.tvYear);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ProgressBar progressBar = convertView.findViewById(R.id.progressBar);

        // Then later, when you want to display image
        final ViewHolder finalHolder = holder;
        ImageLoader.getInstance().displayImage("http://192.168.1.103:80/artikli/"+movieModelList.get(position).getImageUrl(), holder.ivMovieIcon, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
                finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
                saveImage(movieModelList.get(position).getCijena(),finalHolder.ivMovieIcon);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
                finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
            }
        });

        holder.tvMovie.setText(movieModelList.get(position).getNaziv());
        holder.tvTagline.setText(movieModelList.get(position).getBarkod());
        holder.tvYear.setText("Cijena: " + movieModelList.get(position).getCijena());

        // rating bar




        return convertView;
    }


    class ViewHolder{
        private ImageView ivMovieIcon;
        private TextView tvMovie;
        private TextView tvTagline;
        private TextView tvYear;

    }
    public void saveImage(String filenamejpg,ImageView iv){

        BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
        dir.mkdirs();
        String fileName = filenamejpg+".jpg";
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}