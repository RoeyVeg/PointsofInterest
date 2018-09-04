package com.example.pointsofinterest.AdaptersNServices;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pointsofinterest.Activities.SearchResultActivity;
import com.example.pointsofinterest.R;

public class PlaceDetailsListAdapter extends ArrayAdapter implements View.OnClickListener {

    private Context context;
    private TextView textView;
    private ImageView imageView;
    private String webSiteUrl;
    private int pressedPosition;

// ************************** custom list adapter for the place Details list, has 2 Action buttons (images) : phone intent and open web page
    // Google have changed the api and now the web page is available only with billable account so might not work

    public PlaceDetailsListAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        this.context = context;

    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView==null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.list_row,null);
        }

        textView = convertView.findViewById(R.id.listText);
        imageView = convertView.findViewById(R.id.listIcon);

        imageView.setOnClickListener(this);

        textView.setText(getItem(position).toString());

        // modify the icon according to the position - the data is fixed per position
        switch (position) {

            case 0:

                imageView.setImageResource(R.drawable.ic_rating);
                imageView.setVisibility(View.VISIBLE);
                imageView.setTag(0);

                break;

            case 1:

                imageView.setVisibility(View.GONE);
                imageView.setTag(1);


                break;

            case 2:

                imageView.setImageResource(R.drawable.ic_phone_details_name);
                imageView.setTag(2);


                break;

            case 3:

                imageView.setImageResource(R.drawable.ic_weburl_details_name);
                imageView.setTag(3);



                break;

            case 4:

                imageView.setImageResource(R.drawable.ic_openhours_details_name);
                imageView.setTag(4);



                break;


        }

        return convertView;
    }

    @Override
    public void onClick(View v) {

        int tagNum = (int) v.getTag();

        switch (tagNum) {

            case 0: // do nothing

                break;

            case 1:// do nothing

                break;

            case 2: // call intent

                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ getItem(2)));
                context.startActivity(callIntent);

                break;

            case 3:

                if (webSiteUrl==null) {
                    Toast.makeText(context, R.string.no_site_available, Toast.LENGTH_SHORT).show();

                } else {
                    Intent openWebPageIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(webSiteUrl));
                    context.startActivity(openWebPageIntent);
                }




                break;

            case 4: // do nothing

                break;



        }

    }
}
