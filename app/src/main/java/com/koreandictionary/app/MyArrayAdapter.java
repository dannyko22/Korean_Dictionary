package com.koreandictionary.app;


import java.util.ArrayList;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by Danny on 11/03/14.
 */
public class MyArrayAdapter extends ArrayAdapter<DictionaryData> {
    private final Context context;
    private ArrayList<DictionaryData> itemsArrayList;
    protected String searchKey;
    private InterstitialAd interstitialAd;

    public MyArrayAdapter(Context context, ArrayList<DictionaryData> itemsArrayList, String search, InterstitialAd _interstitial) {

        super(context, R.layout.row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.searchKey = search;
        interstitialAd = _interstitial;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        LinearLayout rowView = (LinearLayout) inflater.inflate(R.layout.row, parent, false);

        // 3. Get the two text view from the rowView
        //TextView textViewJapaneseChars = (TextView) rowView.findViewById(R.id.textViewJapaneseChars);
        TextView textViewEnglishChars = (TextView) rowView.findViewById(R.id.textViewEnglishChars);
        TextView textViewKoreanChars = (TextView) rowView.findViewById(R.id.textViewKoreanChars);

        if (itemsArrayList.get(0)._id != -1) {
            // 4. Set the text for textView
            textViewEnglishChars.setText(itemsArrayList.get(position).getEnglishCharsSpannable(searchKey));
            textViewKoreanChars.setText(itemsArrayList.get(position).getKoreanCharsSpannable(searchKey));
        } else {
            //textViewJapaneseChars.setText("");
            textViewEnglishChars.setText("");
            textViewKoreanChars.setText("");
        }

        // setup click listener for each row
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (itemsArrayList.get(0)._id != -1) {
                    if (interstitialAd!=null && interstitialAd.isLoaded())
                    {
                        interstitialAd.show();
                    }

                    Intent i = new Intent(context, DictionaryItemActivity.class);
                    i.putExtra("koreanWords", itemsArrayList.get(position).getKoreanChars(searchKey));
                    i.putExtra("englishWords", itemsArrayList.get(position).getEnglishChars(searchKey));
                    context.startActivity(i);
                }
            }

        });

        // 5. return rowView
        return rowView;
    }
}


