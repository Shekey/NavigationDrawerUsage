package com.example.ajdin.navigatiodraer.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.models.Artikli;
import com.example.ajdin.navigatiodraer.models.Product;
import com.example.ajdin.navigatiodraer.models.Slike;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ajdin on 13.3.2018..
 */

public class NEWProductsAdapter extends ArrayAdapter {

    private List<Artikli> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    public NEWProductsAdapter(Context context, int resource, List<Artikli> objects) {
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
            holder.ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivIconNEW);
            holder.tvMovie = (TextView)convertView.findViewById(R.id.tvMovieNEW);
            holder.tvTagline = (TextView)convertView.findViewById(R.id.tvTaglineNEW);
            holder.tvYear = (TextView)convertView.findViewById(R.id.tvYearNEW);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBarNEW);

        // Then later, when you want to display image
        final ViewHolder finalHolder = holder;
        final ArrayList<Slike> slikes=new ArrayList<>(movieModelList.get(position).getSlike());
        final int size=slikes.size();
        if(size>=1) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + Environment.DIRECTORY_PICTURES,
                    File.separator + "YourFolderName" + File.separator + slikes.get(size - 1).getId() + ".jpg");
            if (file.exists()) {
                Picasso
                        .with(getContext())
                        .load(file)
                        .fit()
                        .centerInside()
                        .into(holder.ivMovieIcon);
                progressBar.setVisibility(View.GONE);
            }
        }


        holder.tvMovie.setText(movieModelList.get(position).getNaziv());
        holder.tvTagline.setText(movieModelList.get(position).getKategorija());
        holder.tvYear.setText("Cijena: " + movieModelList.get(position).getCijena()+" KM");

        return convertView;
    }


    class ViewHolder{
        private ImageView ivMovieIcon;
        private TextView tvMovie;
        private TextView tvTagline;
        private TextView tvYear;


    }

}