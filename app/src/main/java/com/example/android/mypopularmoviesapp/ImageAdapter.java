package com.example.android.mypopularmoviesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ImageAdapter extends ArrayAdapter<ImageModel> {

    private ArrayList<ImageModel> listImageUrl;
    private int Resource;
    private Context context;
    private LayoutInflater vi;

//    public ImageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<ImageModel> objects) {
//        super(context,resource,objects);
//        //super(context, resource, objects);
//        listImageUrl = objects;
//        Resource = resource;
//        this.context = context;
//
//        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//
//    }

    ImageAdapter(Context context, int resource, ArrayList<ImageModel> objects) {
        super(context, resource, objects);
        listImageUrl = objects;
        Resource = resource;
        this.context = context;

        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    void updateResults(ArrayList<ImageModel> results) {
        listImageUrl = results;
        //Triggers the list update
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = vi.inflate(Resource, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        if(!(listImageUrl.get(position).getImagePath() == null)) {
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + listImageUrl.get(position).getImagePath()).into(holder.imageView);
        }else{
            holder.imageView.setImageBitmap(getImage(listImageUrl.get(position).getOfflineImage()));
        }

        return convertView;

    }
    private static class ViewHolder {
        ImageView imageView;
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
