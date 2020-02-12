package com.booksyndy.academics.android.util;

import android.content.Context;
import android.text.TextUtils;

import com.booksyndy.academics.android.R;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Filters {

    private String category = null;
    private String city = null;
    private int price = -1;
    private String sortBy = "Relevance";
    private Query.Direction sortDirection = null;
    private int distance = -1;

    private boolean isText = false;
    private boolean isNotes = false;
    private List<Integer> bookGrade;
    private List<Integer> bookBoard;


    public Filters() {
        bookGrade = new ArrayList<>();
        bookBoard = new ArrayList<>();

    }

    public static Filters getDefault() {
        Filters filters = new Filters();
       // filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasCity() {
        return !(TextUtils.isEmpty(city));
    }

    public boolean hasPrice() {
        return (this.price > 0);
    }

    public boolean hasBookBoard(){return (bookBoard.size() > 0);}

    public boolean hasBookGrade(){return (bookGrade.size() > 0);}

    public boolean hasBookDistance(){return  (distance > -1);}



    public boolean hasSortBy() {
        return !(sortBy.equalsIgnoreCase("Relevance"));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (category == null && city == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_books));
            desc.append("</b>");
        }

        if (category != null) {
            desc.append("<b>");
            desc.append(category);
            desc.append("</b>");
        }

        if (category != null && city != null) {
            desc.append(" booksyndy ");
        }

        if (city != null) {
            desc.append("<b>");
            desc.append(city);
            desc.append("</b>");
        }

        if (price > 0) {
            desc.append(" for ");
            desc.append("<b>");
         //   desc.append(BookUtil.getPriceString(price));
            desc.append("</b>");
        }

        return desc.toString();
    }


    public void setIsNotes(boolean notes) {
        this.isNotes = notes;
    }
    public boolean IsNotes(){
        return this.isNotes;
    }

    public void setIsText(boolean text) {
        this.isText = text;
    }
    public boolean IsText(){
        return this.isText;
    }

    public void setBookGrade(List<Integer> gradeList) {
        this.bookGrade = gradeList;
    }

    public List<Integer> getBookGrade(){
        return this.bookGrade;
    }

    public void setBookBoard(List<Integer> boardList) {
        this.bookBoard = boardList;
    }

    public List<Integer> getBookBoard(){
        return this.bookBoard;
    }


    public void setBookDistance(int selectedDistance) {
        this.distance = selectedDistance;
    }
    public int getBookDistance(){
        return distance;
    }


}
