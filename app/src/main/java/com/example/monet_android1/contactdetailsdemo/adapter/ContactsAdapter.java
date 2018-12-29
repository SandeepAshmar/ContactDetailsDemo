package com.example.monet_android1.contactdetailsdemo.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.example.monet_android1.contactdetailsdemo.fragment.CallDetailsFragment;
import com.example.monet_android1.contactdetailsdemo.fragment.ContactsFragment;
import com.example.monet_android1.contactdetailsdemo.user.Contacts;

import java.util.List;

import static com.example.monet_android1.contactdetailsdemo.activity.MainActivity.myContactAppDatabase;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private List<Contacts> contactsList;
    private PopupMenu popup;
    private int i = 0;

    public ContactsAdapter(Context context, List<Contacts> contactsList) {
        this.context = context;
        this.contactsList = contactsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_contacts, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactsAdapter.ViewHolder holder, final int position) {
        i = i+1;
        holder.id.setText(String.valueOf(i));
        holder.name.setText(contactsList.get(position).getName());
        holder.mobile.setText(contactsList.get(position).getMobile());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopup(holder, contactsList.get(position).getId(), position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView id, name, mobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_contactId);
            name = itemView.findViewById(R.id.tv_contactName);
            mobile = itemView.findViewById(R.id.tv_contactMobile);
        }
    }

    private void showPopup(ViewHolder holder, final int id, final int position) {
        final PopupMenu popup = new PopupMenu(context, holder.itemView);
        popup.getMenuInflater()
                .inflate(R.menu.contact_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("Delete")){
                    deleteUser(id);
                }else{
                    updateUser(id, position);
                }
                return true;
            }
        });
        popup.show();
    }

    private void updateUser(final int id, int position) {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.update_layout);
        final EditText name, mobile;
        Button update, cancel;
        name = dialog.findViewById(R.id.edt_updateName);
        mobile = dialog.findViewById(R.id.edt_updateMobile);
        update = dialog.findViewById(R.id.btn_update);
        cancel = dialog.findViewById(R.id.btn_updateCancel);
        final Contacts contacts = new Contacts();

        name.setText(contactsList.get(position).getName());
        mobile.setText(contactsList.get(position).getMobile());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show();
                }else if(mobile.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                }else if(mobile.getText().length() < 10){
                    Toast.makeText(context, "Please enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                }else{
                    contacts.setId(id);
                    contacts.setName(name.getText().toString());
                    contacts.setMobile(mobile.getText().toString());
                    myContactAppDatabase.myContactDao().updateContactUser(contacts);
                    Toast.makeText(context, "contact number updated successfully", Toast.LENGTH_SHORT).show();
                    CallDetailsFragment.readFromDb(context);
                    ContactsFragment.readFromDb(context);
                    dialog.dismiss();
                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void deleteUser(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Are you sure to delete this contact number");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Contacts contacts = new Contacts();
                contacts.setId(id);
                myContactAppDatabase.myContactDao().deleteContactUser(contacts);
                Toast.makeText(context, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                CallDetailsFragment.readFromDb(context);
                ContactsFragment.readFromDb(context);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });

        builder.show();
    }


}