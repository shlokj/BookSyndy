package co.in.prodigyschool.passiton.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.R;

public class Filters {

    private String category = null;
    private String city = null;
    private int price = -1;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    private boolean isText = false;
    private boolean isNotes = false;
    private int bookGrade = -1;
    private int bookBoard = -1;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasCity() {
        return !(TextUtils.isEmpty(city));
    }

    public boolean hasPrice() {
        return (price > 0);
    }

    public boolean hasBookBoard(){return (bookBoard > 0);}

    public boolean hasBookGrade(){return (bookGrade > 0);}

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
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
            desc.append(" in ");
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

    public void setBookGrade(int gradeNumber) {
        this.bookGrade = gradeNumber;
    }

    public int getBookGrade(){
        return this.bookGrade;
    }

    public void setBookBoard(int boardNumber) {
        this.bookBoard = boardNumber;
    }

    public int getBookBoard(){
        return this.bookBoard;
    }
}
