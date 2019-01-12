package com.example.monet_android1.contactdetailsdemo.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.adapter.ContactsAdapter;
import com.example.monet_android1.contactdetailsdemo.adapter.RecyclerSectionItemDecoration;

import java.util.ArrayList;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.contactList;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.checkUnsavedNumberOnWhatsapp;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.hideSoftKeyboard;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.whatsApplicationCheck;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private static TextView tv_noContact, tv_addContact, tv_checkContact;
    private static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private static ContactsAdapter contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    @SuppressLint("NewApi")
    private void initView(View view) {
        tv_noContact = view.findViewById(R.id.tv_noContact);
        tv_addContact = view.findViewById(R.id.tv_addContact);
        recyclerView = view.findViewById(R.id.rv_contacts);
        tv_checkContact = view.findViewById(R.id.tv_checkContact);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        readFromDb(getActivity());

        recyclerView.setTouchscreenBlocksFocus(true);

        tv_addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContact();
            }
        });

        tv_checkContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whatsApplicationCheck(getActivity())) {
                    openDialog();
                } else {
                    Toast.makeText(getActivity(), "App is not currently installed on your phone", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openDialog() {
        final Dialog builder = new Dialog(getActivity(), R.style.DialogTheme);
        builder.setContentView(R.layout.check_whatsapp);
        builder.setCancelable(false);

        Button search, cancel;
        final EditText editText = builder.findViewById(R.id.edt_whatsappSearch);
        search = builder.findViewById(R.id.btn_search);
        cancel = builder.findViewById(R.id.btn_cancel);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() != 10) {
                    Toast.makeText(getContext(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkUnsavedNumberOnWhatsapp(getActivity(), editText.getText().toString()).equals("done")) {
                        hideSoftKeyboard(getActivity());
                        builder.dismiss();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();
    }

    public void readFromDb(Context context) {

        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(0,
                        true, getSectionCallback(contactList.getName()));
        recyclerView.addItemDecoration(sectionItemDecoration);

        contactsAdapter = new ContactsAdapter(context, contactList);
        recyclerView.setAdapter(contactsAdapter);
        contactsAdapter.notifyDataSetChanged();
        if (contactList.getName().size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tv_noContact.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tv_noContact.setVisibility(View.VISIBLE);
        }
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<String> people) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0
                        || people.get(position).toUpperCase()
                        .charAt(0) != people.get(position).toUpperCase()
                        .charAt(0);
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                return people.get(position).toUpperCase()
                        .subSequence(0,
                                1);
            }
        };
    }

    public void addNewContact() {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        startActivityForResult(contactIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onResume() {
        readFromDb(getActivity());
        super.onResume();
    }
}
