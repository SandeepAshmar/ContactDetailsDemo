package com.example.monet_android1.contactdetailsdemo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myContactAppDatabase;
import static com.example.monet_android1.contactdetailsdemo.fragment.ContactsFragment.readFromDb;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment {

    private EditText name, mobile;
    private boolean isMatched = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);

        name = view.findViewById(R.id.edt_addName);
        mobile = view.findViewById(R.id.edt_addMobile);

        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });

        return view;
    }

    private void saveContact() {
        if(name.getText() == null || name.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Please enter name", Toast.LENGTH_SHORT).show();
        }else if(mobile.getText() == null || mobile.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
        }else if(mobile.getText().length() < 10){
            Toast.makeText(getActivity(), "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
        }else{

            List<Contacts> contactsList = myContactAppDatabase.myContactDao().getContactUsers();
            String number = mobile.getText().toString();
            for (Contacts usr : contactsList) {
                String userMobile = usr.getMobile();
                if (number.equals(userMobile)) {
                    isMatched = true;
                }else{
                    isMatched = false;
                }
            }

            if(isMatched){
                Toast.makeText(getActivity(), "Contact number already exist", Toast.LENGTH_SHORT).show();
            }else{
                Contacts contacts = new Contacts();
                contacts.setName(name.getText().toString());
                contacts.setMobile(mobile.getText().toString());
                myContactAppDatabase.myContactDao().addContactUser(contacts);
                Toast.makeText(getActivity(), "save successfully", Toast.LENGTH_SHORT).show();
                name.setText("");
                mobile.setText("");
                readFromDb(getActivity());
            }

        }


    }

}
