package nl.mwsoft.www.chatster.modelLayer.database.contact;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.util.contact.ChatsterContactsUtil;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;

public class ContactDatabaseLayer {

    public ContactDatabaseLayer() {
    }

    public String getContactProfilePicUriById(Context context, long contactId) {
        String contactProfilePicUri = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CONTACT + " WHERE " + DBOpenHelper.CONTACT_ID + "=" + contactId, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactProfilePicUri = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_PROFILE_PIC));
            }
        }

        cursor.close();
        db.close();

        return contactProfilePicUri;
    }

    public String getContactNameById(Context context, long contactId) {
        String name = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT " + DBOpenHelper.CONTACT_NAME + " FROM " + DBOpenHelper.TABLE_CONTACT +
                " WHERE " + DBOpenHelper.CONTACT_ID + "=" + contactId, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_NAME));
            }
        }

        cursor.close();
        db.close();

        return name;
    }

    public Contact getContactById(Context context, long contactId) {
        Contact contact = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CONTACT + " WHERE " + DBOpenHelper.CONTACT_ID + "=" + contactId, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contact = new Contact();
                contact.setUserId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_ID))));
                contact.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_NAME)));
                contact.setStatusMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_STATUS_MESSAGE)));
            }
        }

        cursor.close();
        db.close();

        return contact;
    }

    public ArrayList<Contact> getAllContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CONTACT + " ORDER BY " + DBOpenHelper.CONTACT_NAME + " COLLATE NOCASE", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setUserId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_ID))));
                contact.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_NAME)));
                contact.setStatusMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_STATUS_MESSAGE)));
                contact.setIsChatsterContact(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_TYPE))));
                contacts.add(contact);
            }
        }

        cursor.close();
        db.close();

        return contacts;
    }

    public ArrayList<Long> getAllContactIds(Context context) {
        ArrayList<Long> contactIds = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CONTACT, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long contactId = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_ID)));
                contactIds.add(contactId);
            }
        }

        cursor.close();
        db.close();

        return contactIds;
    }

    public ArrayList<Contact> getAllContactsByGroupChat(Context context, String groupChatId) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CONTACT + " c "
                + " INNER JOIN " + DBOpenHelper.TABLE_GROUP_CHAT_MEMBER + " gcm ON " +
                " c._id=gcm.group_chat_member_id WHERE gcm.group_chat_id LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setUserId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_ID))));
                contact.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_NAME)));
                contact.setStatusMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CONTACT_STATUS_MESSAGE)));
                contacts.add(contact);
            }
        }

        cursor.close();
        db.close();

        return contacts;
    }

    public void deleteContactById(long contactId, Context context){
        String contactSelectionUpdate = DBOpenHelper.CONTACT_ID + "=" + contactId;
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_CONTACT, contactSelectionUpdate, null);
    }

    public void updateContacts(ArrayList<Long> chatsterContacts, Context context){
        for(long contactId : chatsterContacts){
            String selection = DBOpenHelper.CONTACT_ID + "=" + contactId;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.CONTACT_TYPE, 1);
            context.getContentResolver().update(ChatsProvider.CONTENT_URI_CONTACT, contentValues, selection, null);
        }
    }

    public void insertContact(long contactId, String contactName, String contactStatusMessage, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.CONTACT_ID, contactId);
        setValues.put(DBOpenHelper.CONTACT_NAME, contactName);
        setValues.put(DBOpenHelper.CONTACT_STATUS_MESSAGE, contactStatusMessage);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_CONTACT, setValues);
    }

    public String insertContacts(Context context){
        ArrayList<Long> myContacts = new ArrayList<>();
        if(ChatsterContactsUtil.getAllContactsWithPhoneNumber(context) != null){
            for(Contact cnt : ChatsterContactsUtil.getAllContactsWithPhoneNumber(context)){
                if(!myContacts.contains(cnt.getUserId())){
                    myContacts.add(cnt.getUserId());
                    insertContact(cnt.getUserId(),cnt.getUserName(),cnt.getStatusMessage(), context);
                }
            }
        }
        return ConstantRegistry.DONE;
    }

}
