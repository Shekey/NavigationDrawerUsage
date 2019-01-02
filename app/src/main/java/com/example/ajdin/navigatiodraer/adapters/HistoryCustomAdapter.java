package com.example.ajdin.navigatiodraer.adapters;

/**
 * Created by ajdin on 30.3.2018..
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.ajdin.navigatiodraer.R;
import com.example.ajdin.navigatiodraer.models.PreviewModel;
import com.example.ajdin.navigatiodraer.tasks.DropboxClient;
import com.example.ajdin.navigatiodraer.tasks.UploadTask;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by ajdin on 28.3.2018..
 */

public class HistoryCustomAdapter extends ArrayAdapter {

    private List<String> movieModelList;
    private int resource;
    private LayoutInflater inflater;
    ConnectivityManager wifi = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = wifi.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    private Date date2;

    public HistoryCustomAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(resource, null);

            holder.tvIMP = (TextView) convertView.findViewById(R.id.tVIMP);
            holder.tvDtm = (TextView) convertView.findViewById(R.id.tvDtm);
            holder.button = (Button) convertView.findViewById(R.id.btnSync);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Then later, when you want to display image
        final ViewHolder finalHolder = holder;
        String ime[] = movieModelList.get(position).split("[0-9_-]");
        String datum[] = movieModelList.get(position).split("[a-zA-Z-]");

        holder.tvDtm.setText(datum[datum.length - 1]);
        holder.tvIMP.setText(ime[1]);
        if (info.isConnected()) {
            Pattern MY_PATTERN = Pattern.compile("\\-(.*)");
            Matcher m = MY_PATTERN.matcher(movieModelList.get(position));
            String s = new String();
            while (m.find()) {
                s = m.group(0);
            }

            String input = s, extracted;
            DateFormat df = new SimpleDateFormat("dd MM yyyy HH:mm");
            extracted = input.substring(3, 19);
            try {
                date2 = df.parse(extracted);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {

                    holder.button.setVisibility(View.INVISIBLE);

            }
        }
        else{
                holder.button.setVisibility(View.INVISIBLE);

            }
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (info.isConnected()) {
                        String pat = Environment.getExternalStorageDirectory().getAbsolutePath() + "/racunidevice/" + movieModelList.get(position);
                        File file = new File(pat);
                        new UploadTask(DropboxClient.getClient("aLRppJLoiTAAAAAAAAAADkJLNGAbqPzA0hZ_oVvVlEhNiyiYA94B9ndRUrIXxV8G"), file, getContext().getApplicationContext()).execute();
                    }
                }
            });

            return convertView;
        }

    class ViewHolder {

        private TextView tvIMP;
        private TextView tvDtm;
        private Button button;

    }

}