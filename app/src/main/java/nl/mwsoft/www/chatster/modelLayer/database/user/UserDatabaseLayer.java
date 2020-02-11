/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

}
