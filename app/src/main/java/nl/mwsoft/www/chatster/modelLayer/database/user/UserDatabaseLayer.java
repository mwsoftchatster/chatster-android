package nl.mwsoft.www.chatster.modelLayer.database.user;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.RegisterUserResponse;

public class UserDatabaseLayer {

    public UserDatabaseLayer() {
    }

    public long getUserId(Context context) {
        long id = 0;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.U_ID)));
            }
        }

        cursor.close();
        db.close();

        return id;
    }

    public String getUserName(Context context) {
        String userName = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                userName = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_NAME));
            }
        }

        cursor.close();
        db.close();

        return userName;
    }

    public String getUserStatusMessage(Context context) {
        String userStatusMessage = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userStatusMessage = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.STATUS_MESSAGE));
            }
        }

        cursor.close();
        db.close();

        return userStatusMessage;
    }

    public String getUserProfilePicUri(Context context) {
        String userProfilePicUri = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userProfilePicUri = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_PROFILE_PIC_URI));
            }
        }

        cursor.close();
        db.close();

        return userProfilePicUri;
    }

    public String getUserProfilePicUrl(Context context) {
        String userProfilePicUrl = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userProfilePicUrl = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_PROFILE_PIC_URL));
            }
        }

        cursor.close();
        db.close();

        return userProfilePicUrl;
    }

    public void updateUserStatusMessage(String newStatusMessage, Context context){
        ContentValues statusMessage = new ContentValues();
        statusMessage.put(DBOpenHelper.STATUS_MESSAGE, newStatusMessage);
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_USER, statusMessage, null, null);
    }

    public void updateUser(RegisterUserResponse result, Context context) {
        String selection = DBOpenHelper.U_ID + "=" + result.get_id();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.STATUS_MESSAGE, result.getStatusMessage());
        contentValues.put(DBOpenHelper.USER_NAME, result.getName());
        contentValues.put(DBOpenHelper.USER_PROFILE_PIC_URL, result.getProfilePic());
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_USER, contentValues, selection, null);
    }

    public void updateUser(ConfirmPhoneResponse result, Context context) {
        String selection = DBOpenHelper.U_ID + "=" + result.get_id();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.STATUS_MESSAGE, result.getStatusMessage());
        contentValues.put(DBOpenHelper.USER_NAME, result.getName());
        contentValues.put(DBOpenHelper.USER_PROFILE_PIC_URL, result.getProfilePic());
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_USER, contentValues, selection, null);
    }

    public void updateUserId(long phoneToVerify, Context context){
        String selection = DBOpenHelper.U_ID.concat(ConstantRegistry.CHATSTER_EQUALS).concat(ConstantRegistry.CHATSTER_ZERO);
        ContentValues statusMessage = new ContentValues();
        statusMessage.put(DBOpenHelper.U_ID, phoneToVerify);
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_USER, statusMessage, selection, null);
    }

    public void updateUserProfilePic(String uri, Context context){
        ContentValues profilePicUri = new ContentValues();
        profilePicUri.put(DBOpenHelper.USER_PROFILE_PIC_URI, uri);
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_USER, profilePicUri, null, null);
    }

    public OneTimeKeyPair getUserOneTimeKeyPair(Context context, long userId) {
        OneTimeKeyPair oneTimeKeyPair = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_USER_ID + "=" + userId, null);

        if (cursor != null) {
            oneTimeKeyPair = new OneTimeKeyPair();
            while (cursor.moveToNext()) {
                oneTimeKeyPair.setOneTimePrivateKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PRK)));
                oneTimeKeyPair.setOneTimePublicKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK)));
                oneTimeKeyPair.setUserId(userId);
                oneTimeKeyPair.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID)));
            }
        }

        cursor.close();
        db.close();

        return oneTimeKeyPair;
    }

    public byte[] getUserOneTimePublicPreKey(Context context, long userId) {
        byte[] userOneTimePublicPreKey = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT "+ DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK
                + " FROM " + DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_USER_ID + "=" + userId, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                userOneTimePublicPreKey =
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK));
            }
        }
        cursor.close();
        db.close();

        return userOneTimePublicPreKey;
    }

    public byte[] getUserOneTimePrivatePreKeyByUUID(Context context, String uuid) {
        byte[] userOneTimePrivatePreKey = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT "+ DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PRK
                + " FROM " + DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID + " LIKE '" + uuid + "'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                userOneTimePrivatePreKey = cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PRK));
            }
        }
        cursor.close();
        db.close();

        return userOneTimePrivatePreKey;
    }

    public byte[] getUserOneTimePublicPreKeyByUUID(Context context, String uuid) {
        byte[] userOneTimePublicPreKey = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT "+ DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK
                + " FROM " + DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID + " LIKE '" + uuid + "'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                userOneTimePublicPreKey =
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK));
            }
        }
        cursor.close();
        db.close();

        return userOneTimePublicPreKey;
    }

    public void insertUserOneTimeKeyPair(String uuid, long userId, byte[] userOneTimePrivatePreKey,
                                                byte[] userOneTimePublicPreKey, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID, uuid);
        setValues.put(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_USER_ID, userId);
        setValues.put(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PRK, userOneTimePrivatePreKey);
        setValues.put(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK, userOneTimePublicPreKey);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_USER_ONE_TIME_PRE_KEY_PAIR, setValues);
    }

    public void deleteOneTimeKeyPairByUUID(String uuid, Context context){
        String selection = DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID + "='" + uuid + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_USER_ONE_TIME_PRE_KEY_PAIR, selection, null);
    }

    public OneTimeKeyPair getUserOneTimeKeyPairByUUID(Context context, String uuid, long userId) {
        OneTimeKeyPair oneTimeKeyPair = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID + "='" + uuid + "'", null);

        if (cursor != null) {
            oneTimeKeyPair = new OneTimeKeyPair();
            while (cursor.moveToNext()) {
                oneTimeKeyPair.setOneTimePrivateKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PRK)));
                oneTimeKeyPair.setOneTimePublicKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_PBK)));
                oneTimeKeyPair.setUserId(userId);
                oneTimeKeyPair.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_UUID)));
            }
        }

        cursor.close();
        db.close();

        return oneTimeKeyPair;
    }

}
