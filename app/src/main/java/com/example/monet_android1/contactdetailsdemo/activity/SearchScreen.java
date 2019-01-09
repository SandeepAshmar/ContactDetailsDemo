package com.example.monet_android1.contactdetailsdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.adapter.SearchAdapter;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;

public class SearchScreen extends AppCompatActivity {

    private EditText search;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private LinearLayoutManager layoutManager;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        search = findViewById(R.id.edt_search);
        recyclerView = findViewById(R.id.rv_search);
        back = findViewById(R.id.img_searchBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = search.getText().toString();
                filter(title);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void filter(String text) {
        ContactList temp = new ContactList();

        if (text.matches("[a-zA-Z]+"))
            for (int i = 0; i < contactList.getName().size(); i++) {
                if (contactList.getName().get(i).toLowerCase().contains(text.toLowerCase())) {
                    temp.setName(contactList.getName().get(i));
                    temp.setMobile(contactList.getMobile().get(i));
                }
            }
        else if (text.matches("[0-9]+")) {
            for (int i = 0; i < contactList.getMobile().size(); i++) {
                if (contactList.getMobile().get(i).contains(text)) {
                    temp.setName(contactList.getName().get(i));
                    temp.setMobile(contactList.getMobile().get(i));
                }
            }
        }

        updateList(temp);
    }

    public void updateList(ContactList list) {
        recyclerView.setVisibility(View.VISIBLE);
        searchAdapter = new SearchAdapter(this, list);
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }
}
