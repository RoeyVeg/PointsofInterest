package com.example.pointsofinterest.AdaptersNServices;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pointsofinterest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.pointsofinterest.AdaptersNServices.DetailsIntentService.KEY;

public class PlacePicturesAdapter extends RecyclerView.Adapter<PlacePicturesAdapter.PlacePicturesVieHolder> {

    ArrayList<String> pictures = new ArrayList<String>();
    Context context;
    private String apiCall = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100";

    public PlacePicturesAdapter(Context context) {
        this.context = context;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlacePicturesVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.picture_row_in_place_detail,parent,false);

        return new PlacePicturesVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacePicturesVieHolder holder, int position) {

        holder.bind(pictures.get(position));

    }

    @Override
    public int getItemCount() {

        if (pictures==null) return 0;

        return pictures.size();
    }

    public class PlacePicturesVieHolder extends RecyclerView.ViewHolder {

        ImageView imageView;


        public PlacePicturesVieHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.placePictureImageView);

        }

        public void bind(String photoReference){


            String url = apiCall+"&photoreference="+photoReference+"&key="+KEY;


            Picasso.with(context).
                    load(url).
                    resize(100,100).
                    centerCrop().
                    error(R.drawable.ic_phone_details_name).
                    into(imageView);

        }
    }
}
