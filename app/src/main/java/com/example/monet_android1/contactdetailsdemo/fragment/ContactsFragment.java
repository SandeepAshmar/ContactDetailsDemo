package com.example.monet_android1.contactdetailsdemo.fragment;


import android.content.Context;
import android.os.Bundle;
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

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.user.ContactList;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private static TextView tv_noContact;
    private static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
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
       ContactList temp = new ContactList();
        for (int i = 0; i < contactList.getName().size() ; i++) {
            if (contactList.getName().get(i).toLowerCase().contains(text.toLowerCase())) {
                temp.setName(contactList.getName().get(i));
                temp.setMobile(contactList.getMobile().get(i));
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

    public static void readFromDb(Context context) {

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

    @Override
    public void onResume() {
        super.onResume();
    }
}
