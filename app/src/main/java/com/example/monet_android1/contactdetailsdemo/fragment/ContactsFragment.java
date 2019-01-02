package com.example.monet_android1.contactdetailsdemo.fragment;


import android.content.Context;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;
import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myContactAppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private static TextView tv_noContact;
    private static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private static List<Contacts> contactsList;
    private static ContactsAdapter contactsAdapter;
    private EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        tv_noContact = view.findViewById(R.id.tv_noContact);
        recyclerView = view.findViewById(R.id.rv_contacts);
        search = view.findViewById(R.id.edt_contactSearch);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        readFromDb(getActivity());

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = search.getText().toString();

                if(title.length() == 0){
                    readFromDb(getActivity());
                }else{
                    filter(title);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    void filter(String text) {
        ArrayList<Contacts> temp = new ArrayList();
        for (Contacts d : contactsList) {
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }

        updateList(temp, text);
    }

    public void updateList(ArrayList<Contacts> list, String text) {
        if(list.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            tv_noContact.setVisibility(View.GONE);
            contactsAdapter = new ContactsAdapter(getActivity(), list);
            recyclerView.setAdapter(contactsAdapter);
            contactsAdapter.notifyDataSetChanged();
        }else{
            recyclerView.setVisibility(View.GONE);
            tv_noContact.setVisibility(View.VISIBLE);
            tv_noContact.setText("No Contact's Found Regarding "+text);
        }

    }

    public static void readFromDb(Context context) {

        contactsList = myContactAppDatabase.myContactDao().getContactUsers();
        Collections.sort(contactsList, new Comparator<Contacts>() {
            @Override
            public int compare(Contacts lhs, Contacts rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        contactsAdapter = new ContactsAdapter(context, contactsList);
        recyclerView.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();
        if(contactsList.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            tv_noContact.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.GONE);
            tv_noContact.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
