package com.example.ajdin.navigatiodraer.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.MainActivity;
import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.models.Slike;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ajdin on 6.3.2018..
 */

public class MenuAdapter extends ArrayAdapter {

    private List<Artikli> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public MenuAdapter(Context context, int resource, List<Artikli> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.ivMovieIcon = (ImageView) convertView.findViewById(R.id.ivIconM);
            holder.tvMovie = (TextView) convertView.findViewById(R.id.tvMovieM);
            holder.tvTagline = (TextView) convertView.findViewById(R.id.tvTaglineM);
            holder.tvYear = (TextView) convertView.findViewById(R.id.tvYearM);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBarM);

        // Then later, when you want to display image
        final ViewHolder finalHolder = holder;


        holder.tvMovie.setText(movieModelList.get(position).getNaziv());
        holder.tvTagline.setText(movieModelList.get(position).getKategorija());
        holder.ivMovieIcon.setImageResource(R.drawable.nemaslike);
        final ArrayList<Slike> slikes=new ArrayList<>(movieModelList.get(position).getSlike());
        final int size=slikes.size();
                   if(size>=1) {

            File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + Environment.DIRECTORY_PICTURES,
                    File.separator + "YourFolderName" + File.separator + slikes.get(size - 1).getId() + ".jpg");
            if (file.exists()) {

//            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(columnIndex));
                Picasso
                        .with(getContext())
                        .load(file)
                        .fit()
                        .centerInside()
                        .into(holder.ivMovieIcon);
//            Bitmap bMap = BitmapFactory.decodeFile(file.getAbsolutePath());
////            holder.ivMovieIcon.setImageBitmap(bMap);
//            ImageLoader.getInstance().displayImage("file:///"+file.getAbsolutePath(),holder.ivMovieIcon);

//            Bitmap bMap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            holder.ivMovieIcon.setImageBitmap(bMap);

            }
        }
//        else {
//
//            Log.d("NEMASLIKE", "slika broj: "+slikes.get(size-1).getId()+".jpg");
//            ImageLoader.getInstance().displayImage("http://nurexport.com/demo/"+slikes.get(size-1).getImage(), holder.ivMovieIcon, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    progressBar.setVisibility(View.GONE);
//                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    progressBar.setVisibility(View.GONE);
//                    finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
//                    saveImage(slikes.get(size - 1).getId(), finalHolder.ivMovieIcon);
//
//
//
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                    progressBar.setVisibility(View.GONE);
//                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
//                }
//            });








//            ImageLoader.getInstance().displayImage("http://nurexport.com/demo/upload/"+.getImage(), holder.ivMovieIcon, new ImageLoadingListener() {
//
//                @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                progressBar.setVisibility(View.VISIBLE);
//                finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                progressBar.setVisibility(View.GONE);
//                finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                progressBar.setVisibility(View.GONE);
//                finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
//                saveImage(movieModelList.get(position).getBarkod(),finalHolder.ivMovieIcon);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                progressBar.setVisibility(View.GONE);
//                finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
//            }
//        });
//       }

        progressBar.setVisibility(View.GONE);
        holder.tvYear.setText("Cijena: " + movieModelList.get(position).getCijena()+ " KM");

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
        File dir=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Environment.DIRECTORY_PICTURES,
                File.separator + "YourFolderName/"+filenamejpg+".jpg");
        String fileName = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/"+Environment.DIRECTORY_PICTURES+"/YourFolderName/"+filenamejpg+".jpg";
        File outFile = new File(fileName);
        try {
            outStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
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