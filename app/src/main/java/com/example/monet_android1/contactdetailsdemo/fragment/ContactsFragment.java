package com.example.monet_android1.contactdetailsdemo.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private static TextView tv_noContact, tv_addContact;
    private static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private static ContactsAdapter contactsAdapter;
    private EditText search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        tv_noContact = view.findViewById(R.id.tv_noContact);
        tv_addContact = view.findViewById(R.id.tv_addContact);
        recyclerView = view.findViewById(R.id.rv_contacts);
        search = view.findViewById(R.id.edt_contactSearch);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        readFromDb(getActivity());

        tv_addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContact();
            }
        });

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

                if(count > 0){
                    tv_addContact.setVisibility(View.GONE);
                }else{
                    tv_addContact.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void filter(String text) {
       ContactList temp = new ContactList();

       if(text.matches("[a-zA-Z]+"))
        for (int i = 0; i < contactList.getName().size() ; i++) {
            if (contactList.getName().get(i).toLowerCase().contains(text.toLowerCase())) {
                temp.setName(contactList.getName().get(i));
                temp.setMobile(contactList.getMobile().get(i));
            }
        }else if(text.matches("[0-9]+")){
           for (int i = 0; i < contactList.getMobile().size() ; i++) {
               if (contactList.getMobile().get(i).contains(text)) {
                   temp.setName(contactList.getName().get(i));
                   temp.setMobile(contactList.getMobile().get(i));
               }
           }
       }

        updateList(temp, text);
    }

    public void updateList(ContactList list, String text) {
        if(list.getName().size() > 0){
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

    public void readFromDb(Context context) {

        contactsAdapter = new ContactsAdapter(context, contactList);
        recyclerView.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();
        if(contactList.getName().size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            tv_noContact.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.GONE);
            tv_noContact.setVisibility(View.VISIBLE);
        }
    }

    public void addNewContact() {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        startActivityForResult(contactIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Contact added successfully", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Contact not added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        readFromDb(getActivity());
        super.onResume();
    }
}
