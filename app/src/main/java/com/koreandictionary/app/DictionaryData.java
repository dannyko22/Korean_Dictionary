package com.koreandictionary.app;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

/**
 * Created by Danny on 08/03/14.
 */
public class DictionaryData {
    //private variables
    int _id;
    String line;

    // constructor.  empty data.
    public DictionaryData(){
        this._id = -1;
        this.line = null;
    }

    // constructor
    public DictionaryData(int id, String phrase){
        this._id = id;
        this.line = phrase;
    }

    //get only the english characters
    public String getEnglishChars(String searchKey) {
        int beginPosition = 0;
        int endPosition = line.indexOf("#") - 1;

        String englishChars = line.substring(beginPosition, endPosition);
        englishChars.trim();
        return englishChars;
    }

    // get korean characters.
    public String getKoreanChars(String searchKey) {
        int beginPosition = line.indexOf("#")+2;
        int endPosition = line.length();

        String koreanChars = line.substring(beginPosition, endPosition);
        koreanChars.trim();
        return koreanChars;
    }

    public SpannableString getEnglishCharsSpannable(String searchKey)
    {
        searchKey = searchKey.trim();
        int beginPosition = 0;
        int endPosition = line.indexOf("#")-1;
        SpannableString characters = new SpannableString("");

        String englishChars = line.substring(beginPosition, endPosition);
        englishChars.trim();

        String finalEnglishChars = " " + englishChars;
        characters = new SpannableString(finalEnglishChars);
        int englishCharLength = englishChars.length() + 1;
        characters.setSpan(new ForegroundColorSpan(Color.RED), 1, englishCharLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //find highlight
        int beginSearchKeyPosition = finalEnglishChars.toLowerCase().indexOf(searchKey.toLowerCase(), 0);
        int endSearchKeyPosition = beginSearchKeyPosition + searchKey.length();

        // if search key is found, color the search key
        if (beginSearchKeyPosition != -1) {
            characters.setSpan(new BackgroundColorSpan(Color.YELLOW), beginSearchKeyPosition, endSearchKeyPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return characters;
    }

    public SpannableString getKoreanCharsSpannable(String searchKey)
    {
        searchKey = searchKey.trim();
        int beginPosition = line.indexOf("#")+1;
        int endPosition = line.length();
        SpannableString characters = new SpannableString("");

        String koreanChars = line.substring(beginPosition, endPosition);
        koreanChars.trim();

        String koreanCharsFinal = "" + koreanChars;

        characters = new SpannableString(koreanCharsFinal);
        int koreanCharLength = koreanChars.length();

        characters.setSpan(new ForegroundColorSpan(Color.BLUE), 0, koreanCharLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //find first highlight
        int beginFirstSearchKeyPosition = koreanCharsFinal.toLowerCase().indexOf(searchKey.toLowerCase(), 0);
        int endFirstSearchKeyPosition = beginFirstSearchKeyPosition + searchKey.length();

        // if search key is found, color the search key
        if (beginFirstSearchKeyPosition != -1) {
            characters.setSpan(new BackgroundColorSpan(Color.YELLOW), beginFirstSearchKeyPosition, endFirstSearchKeyPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return characters;
    }


}
