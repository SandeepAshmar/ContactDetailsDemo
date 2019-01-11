package com.example.monet_android1.contactdetailsdemo.helper;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.activity.SearchScreen;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class AppUtils {

    public static boolean isConnectionAvailable(Context ctx) {
        ConnectivityManager mManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mManager.getActiveNetworkInfo();
        return (mNetworkInfo != null) && (mNetworkInfo.isConnected());
    }

    public static String filterNumber(String number) {
        if (number == null) {
            number = "112";
            return number;
        } else {
            number = number.replace(" ", "");
            number = number.replace("-", "");
            number = number.replace("(", "");
            number = number.replace(")", "");
            number = number.replace("+91", "");
            String upToNCharacters = number.substring(0, Math.min(number.length(), 2));
            if (number.length() == 12) {
                if (upToNCharacters.equals("91")) {
                    StringBuilder str = new StringBuilder(number);
                    str.delete(0, 2);
                    number = str.toString();
                }
            } else if (number.length() == 11) {
                if (upToNCharacters.equals("09") || upToNCharacters.equals("08") ||
                        upToNCharacters.equals("07") || upToNCharacters.equals("06")) {
                    StringBuilder str = new StringBuilder(number);
                    str.delete(0, 1);
                    number = str.toString();
                }
            }
            return number;
        }
    }

    public static int getContactIDFromNumber(String contactNumber, Context context) {
        contactNumber = Uri.encode(contactNumber);
        int phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contactNumber), new String[]
                {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
        while (contactLookupCursor.moveToNext()) {
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        return phoneContactID;
    }

    public static int hasWhatsApp(String contactID, Context context) {
        int whatsAppExists = 0;
        boolean hasWhatsApp;

        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[]{contactID, "com.whatsapp"};
        Cursor cursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null) {
            hasWhatsApp = cursor.moveToNext();
            if (hasWhatsApp) {
                whatsAppExists = 1;
            }
            cursor.close();
        }
        return whatsAppExists;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static String checkUnsavedNumberOnWhatsapp(Context context, String mobile) {
        String close = "";
        if(isConnectionAvailable(context)){
            String upToNCharacters = mobile.substring(0, Math.min(mobile.length(), 1));
            if (upToNCharacters.equals("9") || upToNCharacters.equals("8") ||
                    upToNCharacters.equals("7") || upToNCharacters.equals("6")){
                PackageManager packageManager = context.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone=" +
                            "+91 " + mobile + "&text=" + URLEncoder.encode("", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        context.startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                close = "no";
                Toast.makeText(context, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
            }
        }else{
            close = "no";
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
        return close;
    }

    public static boolean whatsApplicationCheck(Context context) {
        String uri = "com.whatsapp";
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    public static Bitmap getPhoto(String mobile, Context context) {
        long contactId = getContactIDFromNumber(mobile, context);
        if(!(contactId < 0)){
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
            Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            Cursor cursor = context.getContentResolver().query(photoUri,
                    new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
            if (cursor == null) {
                return null;
            }
            try {
                if (cursor.moveToFirst()) {
                    byte[] data = cursor.getBlob(0);
                    if (data != null) {
                        return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public static void voiceSearch(Activity activity){
        /* Call Activity for Voice Input */
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            activity.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity, "Oops! Your device doesn't support Speech to Text",Toast.LENGTH_SHORT).show();
        }
    }

    public static void callUser(String Mobile, Context context){
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + Mobile));
            context.startActivity(callIntent);
        } else {
            Toast.makeText(context, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendSMS(String mobile, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); // Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra("address", mobile);
            sendIntent.setType("text/plain");

            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            context.startActivity(sendIntent);

        } else // For early versions, do what worked for you before.
        {
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", mobile);
            context.startActivity(smsIntent);
        }
    }

    public void updateContact (String contactId, String newNumber, String name , Activity act)
            throws RemoteException, OperationApplicationException {

        //ASSERT: @contactId alreay has a work phone number
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String selectPhone = ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "='"  +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'" + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=?";
        String[] phoneArgs = new String[]{contactId, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)};
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, name)
                .build());
        act.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    }
}
