package nl.mwsoft.www.chatster.modelLayer.database.creators;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.contact.ContactDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;

public class CreatorsDatabaseLayer {

    private ContactDatabaseLayer contactDatabaseLayer;

    public CreatorsDatabaseLayer() {
    }

    public CreatorsDatabaseLayer(ContactDatabaseLayer contactDatabaseLayer) {
        this.contactDatabaseLayer = contactDatabaseLayer;
    }

    public String getCreatorsContactProfilePicUriById(Context context, String creatorContactId) {
        String creatorContactProfilePicUri = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CREATOR_CONTACT +
                " WHERE " + DBOpenHelper.CREATOR_CONTACT_ID + " LIKE '" + creatorContactId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                creatorContactProfilePicUri =
                        cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_PROFILE_PIC));
            }
        }

        cursor.close();
        db.close();

        return creatorContactProfilePicUri;
    }


    public void insertCreatorPostIsLiked(String uuid, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.CREATOR_POST_UUID, uuid);
        setValues.put(DBOpenHelper.CREATOR_POST_IS_LIKED, 1);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_CREATOR_POSTS_LIKED, setValues);
    }

    public void updateCreatorPostIsLiked(String uuid, int status, Context context) {
        ContentValues setValues = new ContentValues();
        String selection = DBOpenHelper.CREATOR_POST_UUID.
                concat(ConstantRegistry.CHATSTER_EQUALS).concat(ConstantRegistry.CHATSTER_QUESTION_MARK);
        setValues.put(DBOpenHelper.CREATOR_POST_IS_LIKED, status);
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_CREATOR_POSTS_LIKED, setValues, selection,
                new String[]{uuid});
    }

    public int getCreatorsPostIsLiked(Context context, String postUUID) {
        int creatorsPostIsLiked = 0;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CREATOR_POSTS_LIKED +
                " WHERE " + DBOpenHelper.CREATOR_POST_UUID + " LIKE '" + postUUID + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                creatorsPostIsLiked =
                        Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_POST_IS_LIKED)));
            }
        }

        cursor.close();
        db.close();

        return creatorsPostIsLiked;
    }


    public boolean getCreatorsPostExists(Context context, String postUUID) {
        boolean postExists = false;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CREATOR_POSTS_LIKED +
                " WHERE " + DBOpenHelper.CREATOR_POST_UUID + " LIKE '" + postUUID + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                postExists = true;
            }
        }

        cursor.close();
        db.close();

        return postExists;
    }

    public CreatorContact getCreatorContactById(Context context, String creatorContactId) {
        CreatorContact creatorContact = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CREATOR_CONTACT
                + " WHERE " + DBOpenHelper.CREATOR_CONTACT_ID + " LIKE '" + creatorContactId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                creatorContact = new CreatorContact();
                creatorContact.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_ID)));
                creatorContact.setStatusMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_STATUS_MESSAGE)));
                creatorContact.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_PROFILE_PIC)));
            }
        }

        cursor.close();
        db.close();

        return creatorContact;
    }

    public ArrayList<CreatorContact> getAllCreatorContacts(Context context) {
        ArrayList<CreatorContact> creatorContacts = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CREATOR_CONTACT + " ORDER BY "
                + DBOpenHelper.CREATOR_CONTACT_ID + " COLLATE NOCASE", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CreatorContact creatorContact = new CreatorContact();
                creatorContact.setCreatorId(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_ID)));
                creatorContact.setStatusMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_STATUS_MESSAGE)));
                creatorContact.setProfilePic(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_PROFILE_PIC)));
                creatorContacts.add(creatorContact);
            }
        }

        cursor.close();
        db.close();

        return creatorContacts;
    }

    public ArrayList<String> getAllCreatorContactIds(Context context) {
        ArrayList<String> contactIds = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_CREATOR_CONTACT, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.CREATOR_CONTACT_ID));
                contactIds.add(contactId);
            }
        }

        cursor.close();
        db.close();

        return contactIds;
    }

    public void disconnectCreatorContactById(String contactId, Context context){
        String contactSelectionUpdate = DBOpenHelper.CONTACT_ID + "='" + contactId + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_CREATOR_CONTACT,
                contactSelectionUpdate,
                null);
    }

    public void insertCreatorContact(String creatorContactId, String creatorContactProfilePic,
                                     String creatorContactStatusMessage, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.CREATOR_CONTACT_ID, creatorContactId);
        setValues.put(DBOpenHelper.CREATOR_CONTACT_PROFILE_PIC, creatorContactProfilePic);
        setValues.put(DBOpenHelper.CREATOR_CONTACT_STATUS_MESSAGE, creatorContactStatusMessage);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_CONTACT, setValues);
    }

    public String insertCreatorContacts(Context context){
        ArrayList<Long> myContacts = new ArrayList<>();
        if(contactDatabaseLayer.getAllContacts(context) != null){
            for(Contact contact : contactDatabaseLayer.getAllContacts(context)){
                if(contact.getIsChatsterContact() == 1){
                    myContacts.add(contact.getUserId());
                    if(!myContacts.contains(contact.getUserId())){
                        insertCreatorContact(
                                contact.getUserName(),
                                contactDatabaseLayer.getContactProfilePicUriById(context, contact.getUserId()),
                                contact.getStatusMessage(),
                                context);
                    }
                }
            }
        }

        return ConstantRegistry.DONE;
    }
}
