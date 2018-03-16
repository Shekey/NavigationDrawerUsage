package com.example.ajdin.navigatiodraer.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.models.Product;

import java.io.File;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ajdin on 13.3.2018..
 */

public class SnizenoAdapter extends ArrayAdapter {

    private List<Product> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public SnizenoAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);
            holder.ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivIconSn);
            holder.tvMovie = (TextView)convertView.findViewById(R.id.tvMovieSn);
            holder.tvTagline = (TextView)convertView.findViewById(R.id.tvTaglineSn);
            holder.tvYear = (TextView)convertView.findViewById(R.id.tvYearSn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

        // Then later, when you want to display image
        final ViewHolder finalHolder = holder;
        File file = new File(movieModelList.get(position).getImageDevice());
        holder.ivMovieIcon.setImageURI(Uri.parse(file.getAbsolutePath()));
        progressBar.setVisibility(View.GONE);
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

}