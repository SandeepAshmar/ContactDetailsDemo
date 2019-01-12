package com.example.monet_android1.contactdetailsdemo.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.adapter.SearchAdapter;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.hideSoftKeyboard;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.voiceSearch;

public class SearchScreen extends AppCompatActivity {

    private EditText search;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private LinearLayoutManager layoutManager;
    private ImageView back, mic;
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        search = findViewById(R.id.edt_search);
        recyclerView = findViewById(R.id.rv_search);
        back = findViewById(R.id.img_searchBack);
        mic = findViewById(R.id.img_mic);
        noData = findViewById(R.id.tv_seachNoData);

        String text = getIntent().getStringExtra("searchResult");
        try{
            if(!text.isEmpty()){
                search.setText(text);
                filter(text);

            }
        }catch (Exception e){

        }

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceSearch(SearchScreen.this);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    String yourResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    search.setText(yourResult);
                    filter(yourResult);
                }
                break;
            }
        }
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
        if(search.getText().length() == 0){
            noData.setText("Please enter valid text to serach contact");
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else if(list.getName().size() == 0){
            noData.setText("No matches found");
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            searchAdapter = new SearchAdapter(this, list);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }
    }
}
