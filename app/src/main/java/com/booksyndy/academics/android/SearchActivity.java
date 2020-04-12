package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.booksyndy.academics.android.Data.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private EditText searchText;
    private ListView listView;
private static String TAG = "ALGOLIA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchText = findViewById(R.id.searchText);
        listView = findViewById(R.id.listView);


        Client client = new Client("B2XKIGCNXW","8cfa545e393f40c1e03c35f834b7c6b6");
        final Index index = client.getIndex("books_dev");

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Query query = new Query(s.toString())
                        .setHitsPerPage(20);
                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                        Log.d(TAG, "requestCompleted: algolia"+jsonObject.toString());
                        try {
                            JSONArray hits = jsonObject.getJSONArray("hits");
                            List<String> books = new ArrayList<>();
                            for(int i=0;i<hits.length();i++){
                                JSONObject bookObject = hits.getJSONObject(i);
                                String book = bookObject.getJSONObject("book").getString("bookName");
                                books.add(book);
                                ArrayAdapter<String> bookAdapter = new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,books);
                                listView.setAdapter(bookAdapter);
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }
}
