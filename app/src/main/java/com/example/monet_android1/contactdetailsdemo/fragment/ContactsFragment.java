package com.example.monet_android1.contactdetailsdemo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.user.CallLog;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myCallsAppDatabase;
import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myContactAppDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private TextView tv_noContact;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Contacts> contactsList;
    private ContactsAdapter contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        tv_noContact = view.findViewById(R.id.tv_noContact);
        recyclerView = view.findViewById(R.id.rv_contacts);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        readFromDb();

        return view;
    }

    private void readFromDb() {

        List<Contacts> contactsList = myContactAppDatabase.myContactDao().getContactUsers();
        contactsAdapter = new ContactsAdapter(getActivity(), contactsList);
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
