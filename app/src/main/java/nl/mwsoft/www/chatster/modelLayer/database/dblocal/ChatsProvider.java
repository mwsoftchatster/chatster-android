package nl.mwsoft.www.chatster.modelLayer.database.dblocal;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.Nullable;

public class ChatsProvider extends ContentProvider {

    private static final String AUTHORITY = "nl.mwsoft.www.chatster.dblocal.chatsprovider";
    private static final String CONTACT_PATH = "contact";
    private static final String CHAT_PATH = "chat";
    private static final String GROUP_CHAT_PATH = "group_chat";
    private static final String GROUP_CHAT_MEMBER_PATH = "group_chat_member";
    private static final String MESSAGE_PATH = "message";
    private static final String MESSAGE_QUEUE_PATH = "message_queue";
    private static final String GROUP_MESSAGE_QUEUE_PATH = "group_message_queue";
    private static final String USER_PATH = "user";
    private static final String OFFLINE_CONTACT_RESPONSE_PATH = "offline_contact_response";
    private static final String RETRIEVED_OFFLINE_MESSAGE_UUID_PATH = "retrieved_offline_message_uuid";
    private static final String RECEIVED_ONLINE_MESSAGE_PATH = "received_online_message";
    private static final String RECEIVED_ONLINE_GROUP_MESSAGE_PATH = "received_online_group_message";
    private static final String CREATOR_CONTACT_PATH = "creator_contact";
    private static final String CREATOR_POSTS_LIKED_PATH = "creator_posts_liked";
    private static final String USER_ONE_TIME_PRE_KEY_PAIR_PATH = "user_one_time_pre_key_pair";
    private static final String GROUP_ONE_TIME_PRE_KEY_PAIR_PATH = "group_one_time_pre_key_pair";

    public static final Uri CONTENT_URI_CONTACT = Uri.parse("content://" + AUTHORITY + "/" + CONTACT_PATH);
    public static final Uri CONTENT_URI_CHAT = Uri.parse("content://" + AUTHORITY + "/" + CHAT_PATH);
    public static final Uri CONTENT_URI_GROUP_CHAT = Uri.parse("content://" + AUTHORITY + "/" + GROUP_CHAT_PATH);
    public static final Uri CONTENT_URI_GROUP_CHAT_MEMBER = Uri.parse("content://" + AUTHORITY + "/" + GROUP_CHAT_MEMBER_PATH);
    public static final Uri CONTENT_URI_MESSAGE = Uri.parse("content://" + AUTHORITY + "/" + MESSAGE_PATH);
    public static final Uri CONTENT_URI_MESSAGE_QUEUE = Uri.parse("content://" + AUTHORITY + "/" + MESSAGE_QUEUE_PATH);
    public static final Uri CONTENT_URI_GROUP_MESSAGE_QUEUE = Uri.parse("content://" + AUTHORITY + "/" + GROUP_MESSAGE_QUEUE_PATH);
    public static final Uri CONTENT_URI_USER = Uri.parse("content://" + AUTHORITY + "/" + USER_PATH);
    public static final Uri CONTENT_URI_OFFLINE_CONTACT_RESPONSE = Uri.parse("content://" + AUTHORITY + "/" + OFFLINE_CONTACT_RESPONSE_PATH);
    public static final Uri CONTENT_URI_RETRIEVED_OFFLINE_MESSAGE_UUID = Uri.parse("content://" + AUTHORITY + "/" + RETRIEVED_OFFLINE_MESSAGE_UUID_PATH);
    public static final Uri CONTENT_URI_RECEIVED_ONLINE_MESSAGE = Uri.parse("content://" + AUTHORITY + "/" + RECEIVED_ONLINE_MESSAGE_PATH);
    public static final Uri CONTENT_URI_RECEIVED_ONLINE_GROUP_MESSAGE = Uri.parse("content://" + AUTHORITY + "/" + RECEIVED_ONLINE_GROUP_MESSAGE_PATH);
    public static final Uri CONTENT_URI_CREATOR_CONTACT = Uri.parse("content://" + AUTHORITY + "/" + CREATOR_CONTACT_PATH);
    public static final Uri CONTENT_URI_CREATOR_POSTS_LIKED = Uri.parse("content://" + AUTHORITY + "/" + CREATOR_POSTS_LIKED_PATH);
    public static final Uri CONTENT_URI_USER_ONE_TIME_PRE_KEY_PAIR = Uri.parse("content://" + AUTHORITY + "/" + USER_ONE_TIME_PRE_KEY_PAIR_PATH);
    public static final Uri CONTENT_URI_GROUP_ONE_TIME_PRE_KEY_PAIR = Uri.parse("content://" + AUTHORITY + "/" + GROUP_ONE_TIME_PRE_KEY_PAIR_PATH);


    // ConstantRegistry to identify the requested operation
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;

    private static final int CHATS = 3;
    private static final int CHAT_ID = 4;

    private static final int GROUP_CHATS = 5;
    private static final int GROUP_CHAT_ID = 6;

    private static final int GROUP_CHAT_MEMBERS = 7;
    private static final int GROUP_CHAT_MEMBER_GROUP_CHAT_ID = 8;

    private static final int MESSAGES = 9;
    private static final int MESSAGE_ID = 10;

    private static final int MESSAGE_QUEUE_ITEMS = 11;
    private static final int MESSAGE_QUEUE_ITEM_ID = 12;

    private static final int GROUP_MESSAGE_QUEUE_ITEMS = 23;
    private static final int GROUP_MESSAGE_QUEUE_ITEM_ID = 24;

    private static final int USERS = 13;
    private static final int USER_ID = 14;

    private static final int OFFLINE_CONTACT_RESPONSES = 15;
    private static final int OFFLINE_CONTACT_RESPONSE_ID = 16;

    private static final int RETRIEVED_OFFLINE_MESSAGE_UUIDS = 17;
    private static final int RETRIEVED_OFFLINE_MESSAGE_UUID_ID = 18;

    private static final int RECEIVED_ONLINE_MESSAGES = 19;
    private static final int RECEIVED_ONLINE_MESSAGE_ID = 20;

    private static final int RECEIVED_ONLINE_GROUP_MESSAGES = 21;
    private static final int RECEIVED_ONLINE_GROUP_MESSAGE_ID = 22;

    private static final int CREATOR_CONTACTS = 25;
    private static final int CREATOR_CONTACT_ID = 26;

    private static final int CREATOR_POSTS_LIKEDS = 27;
    private static final int CREATOR_POSTS_LIKED_ID = 28;

    private static final int USER_ONE_TIME_PRE_KEY_PAIRS = 29;
    private static final int USER_ONE_TIME_PRE_KEY_PAIR_ID = 30;

    private static final int GROUP_ONE_TIME_PRE_KEY_PAIRS = 31;
    private static final int GROUP_ONE_TIME_PRE_KEY_PAIR_ID = 32;


    private static  final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String CONTENT_ITEM_CONTACT = "contact";
    private static final String CONTENT_ITEM_CHAT = "chat";
    private static final String CONTENT_ITEM_GROUP_CHAT = "group_chat";
    private static final String CONTENT_ITEM_GROUP_CHAT_MEMBER = "group_chat_member";
    private static final String CONTENT_ITEM_MESSAGE = "message";
    private static final String CONTENT_ITEM_MESSAGE_QUEUE = "message_queue";
    private static final String CONTENT_ITEM_GROUP_MESSAGE_QUEUE = "group_message_queue";
    private static final String CONTENT_ITEM_USER = "user";
    private static final String CONTENT_ITEM_OFFLINE_CONTACT_RESPONSE = "offline_contact_response";
    private static final String CONTENT_ITEM_RETRIEVED_OFFLINE_MESSAGE_UUID = "retrieved_offline_message_uuid";
    private static final String CONTENT_ITEM_RECEIVED_ONLINE_MESSAGE = "received_online_message";
    private static final String CONTENT_ITEM_RECEIVED_ONLINE_GROUP_MESSAGE = "received_online_group_message";
    private static final String CONTENT_ITEM_CREATOR_CONTACT = "creator_contact";
    private static final String CONTENT_ITEM_CREATOR_POSTS_LIKED = "creator_posts_liked";
    private static final String CONTENT_ITEM_USER_ONE_TIME_PRE_KEY_PAIR = "user_one_time_pre_key_pair";
    private static final String CONTENT_ITEM_GROUP_ONE_TIME_PRE_KEY_PAIR = "group_one_time_pre_key_pair";

    static {
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH, CONTACTS);
        uriMatcher.addURI(AUTHORITY, CONTACT_PATH + "/#", CONTACT_ID);

        uriMatcher.addURI(AUTHORITY, CHAT_PATH, CHATS);
        uriMatcher.addURI(AUTHORITY, CHAT_PATH + "/#", CHAT_ID);

        uriMatcher.addURI(AUTHORITY, GROUP_CHAT_PATH, GROUP_CHATS);
        uriMatcher.addURI(AUTHORITY, GROUP_CHAT_PATH + "/#", GROUP_CHAT_ID);

        uriMatcher.addURI(AUTHORITY, GROUP_CHAT_MEMBER_PATH, GROUP_CHAT_MEMBERS);
        uriMatcher.addURI(AUTHORITY, GROUP_CHAT_MEMBER_PATH + "/#", GROUP_CHAT_MEMBER_GROUP_CHAT_ID);

        uriMatcher.addURI(AUTHORITY, MESSAGE_PATH, MESSAGES);
        uriMatcher.addURI(AUTHORITY, MESSAGE_PATH + "/#", MESSAGE_ID);

        uriMatcher.addURI(AUTHORITY, MESSAGE_QUEUE_PATH, MESSAGE_QUEUE_ITEMS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_QUEUE_PATH + "/#", MESSAGE_QUEUE_ITEM_ID);

        uriMatcher.addURI(AUTHORITY, GROUP_MESSAGE_QUEUE_PATH, GROUP_MESSAGE_QUEUE_ITEMS);
        uriMatcher.addURI(AUTHORITY, GROUP_MESSAGE_QUEUE_PATH + "/#", GROUP_MESSAGE_QUEUE_ITEM_ID);

        uriMatcher.addURI(AUTHORITY, USER_PATH, USERS);
        uriMatcher.addURI(AUTHORITY, USER_PATH + "/#", USER_ID);

        uriMatcher.addURI(AUTHORITY, OFFLINE_CONTACT_RESPONSE_PATH, OFFLINE_CONTACT_RESPONSES);
        uriMatcher.addURI(AUTHORITY, OFFLINE_CONTACT_RESPONSE_PATH + "/#", OFFLINE_CONTACT_RESPONSE_ID);

        uriMatcher.addURI(AUTHORITY, RETRIEVED_OFFLINE_MESSAGE_UUID_PATH, RETRIEVED_OFFLINE_MESSAGE_UUIDS);
        uriMatcher.addURI(AUTHORITY, RETRIEVED_OFFLINE_MESSAGE_UUID_PATH + "/#", RETRIEVED_OFFLINE_MESSAGE_UUID_ID);

        uriMatcher.addURI(AUTHORITY, RECEIVED_ONLINE_MESSAGE_PATH, RECEIVED_ONLINE_MESSAGES);
        uriMatcher.addURI(AUTHORITY, RECEIVED_ONLINE_MESSAGE_PATH + "/#", RECEIVED_ONLINE_MESSAGE_ID);

        uriMatcher.addURI(AUTHORITY, RECEIVED_ONLINE_GROUP_MESSAGE_PATH, RECEIVED_ONLINE_GROUP_MESSAGES);
        uriMatcher.addURI(AUTHORITY, RECEIVED_ONLINE_GROUP_MESSAGE_PATH + "/#", RECEIVED_ONLINE_GROUP_MESSAGE_ID);

        uriMatcher.addURI(AUTHORITY, CREATOR_CONTACT_PATH, CREATOR_CONTACTS);
        uriMatcher.addURI(AUTHORITY, CREATOR_CONTACT_PATH + "/#", CREATOR_CONTACT_ID);

        uriMatcher.addURI(AUTHORITY, CREATOR_POSTS_LIKED_PATH, CREATOR_POSTS_LIKEDS);
        uriMatcher.addURI(AUTHORITY, CREATOR_POSTS_LIKED_PATH + "/#", CREATOR_POSTS_LIKED_ID);

        uriMatcher.addURI(AUTHORITY, USER_ONE_TIME_PRE_KEY_PAIR_PATH, USER_ONE_TIME_PRE_KEY_PAIRS);
        uriMatcher.addURI(AUTHORITY, USER_ONE_TIME_PRE_KEY_PAIR_PATH + "/#", USER_ONE_TIME_PRE_KEY_PAIR_ID);

        uriMatcher.addURI(AUTHORITY, GROUP_ONE_TIME_PRE_KEY_PAIR_PATH, GROUP_ONE_TIME_PRE_KEY_PAIRS);
        uriMatcher.addURI(AUTHORITY, GROUP_ONE_TIME_PRE_KEY_PAIR_PATH + "/#", GROUP_ONE_TIME_PRE_KEY_PAIR_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());

        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor _cursor;
        if(uriMatcher.match(uri) == CONTACT_ID){
            selection = DBOpenHelper.CONTACT_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == CHAT_ID){
            selection = DBOpenHelper.CHAT_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == GROUP_CHAT_ID){
            selection = DBOpenHelper.GROUP_CHAT_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == GROUP_CHAT_MEMBER_GROUP_CHAT_ID){
            selection = DBOpenHelper.GROUP_CHAT_MEMBER_GROUP_CHAT_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == MESSAGE_ID){
            selection = DBOpenHelper.MESSAGE_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == MESSAGE_QUEUE_ITEM_ID){
            selection = DBOpenHelper.MESSAGE_QUEUE_ITEM_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == GROUP_MESSAGE_QUEUE_ITEM_ID){
            selection = DBOpenHelper.GROUP_MESSAGE_QUEUE_ITEM_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == USER_ID){
            selection = DBOpenHelper.U_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == OFFLINE_CONTACT_RESPONSE_ID){
            selection = DBOpenHelper.OFFLINE_CONTACT_RESPONSE_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == RETRIEVED_OFFLINE_MESSAGE_UUID_ID){
            selection = DBOpenHelper.RETRIEVED_OFFLINE_MESSAGE_UUID_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == RECEIVED_ONLINE_MESSAGE_ID){
            selection = DBOpenHelper.RECEIVED_ONLINE_MESSAGE_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == RECEIVED_ONLINE_GROUP_MESSAGE_ID){
            selection = DBOpenHelper.RECEIVED_ONLINE_GROUP_MESSAGE_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == CREATOR_CONTACT_ID){
            selection = DBOpenHelper.CREATOR_CONTACT_ID + "=" + uri.getLastPathSegment();
        } else if(uriMatcher.match(uri) == CREATOR_POSTS_LIKED_ID){
            selection = DBOpenHelper.CREATOR_POSTS_LIKED_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == USER_ONE_TIME_PRE_KEY_PAIR_ID){
            selection = DBOpenHelper.USER_ONE_TIME_PRE_KEY_PAIR_ID + "=" + uri.getLastPathSegment();
        }else if(uriMatcher.match(uri) == GROUP_ONE_TIME_PRE_KEY_PAIR_ID){
            selection = DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_ID + "=" + uri.getLastPathSegment();
        }

        switch (uriMatcher.match(uri)){
            case CONTACTS:
                _cursor = database.query(DBOpenHelper.TABLE_CONTACT, DBOpenHelper.ALL_COLUMNS_CONTACT,
                        selection, null, null, null, DBOpenHelper.CONTACT_CREATED + " DESC");
                break;
            case CHATS:
                _cursor = database.query(DBOpenHelper.TABLE_CHAT, DBOpenHelper.ALL_COLUMNS_CHAT,
                        selection, null, null, null, DBOpenHelper.CHAT_CREATED + " DESC");
                break;
            case GROUP_CHATS:
                _cursor = database.query(DBOpenHelper.TABLE_GROUP_CHAT, DBOpenHelper.ALL_COLUMNS_GROUP_CHAT,
                        selection, null, null, null, DBOpenHelper.GROUP_CHAT_CREATED + " DESC");
                break;
            case GROUP_CHAT_MEMBERS:
                _cursor = database.query(DBOpenHelper.TABLE_GROUP_CHAT_MEMBER, DBOpenHelper.ALL_COLUMNS_GROUP_CHAT_MEMBER,
                        selection, null, null, null, null);
                break;
            case MESSAGES:
                _cursor = database.query(DBOpenHelper.TABLE_MESSAGE, DBOpenHelper.ALL_COLUMNS_MESSAGE,
                        selection, null, null, null, DBOpenHelper.MESSAGE_CREATED + " DESC");
                break;
            case MESSAGE_QUEUE_ITEMS:
                _cursor = database.query(DBOpenHelper.TABLE_MESSAGE_QUEUE, DBOpenHelper.ALL_COLUMNS_MESSAGE_QUEUE,
                        selection, null, null, null, null);
                break;
            case GROUP_MESSAGE_QUEUE_ITEMS:
                _cursor = database.query(DBOpenHelper.TABLE_GROUP_MESSAGE_QUEUE, DBOpenHelper.ALL_COLUMNS_GROUP_MESSAGE_QUEUE,
                        selection, null, null, null, null);
                break;
            case USERS:
                _cursor = database.query(DBOpenHelper.TABLE_USER, DBOpenHelper.ALL_COLUMNS_USER,
                        selection, null, null, null, null);
                break;
            case OFFLINE_CONTACT_RESPONSES:
                _cursor = database.query(DBOpenHelper.TABLE_OFFLINE_CONTACT_RESPONSE, DBOpenHelper.ALL_COLUMNS_OFFLINE_CONTACT_RESPONSE,
                        selection, null, null, null, null);
                break;
            case RETRIEVED_OFFLINE_MESSAGE_UUIDS:
                _cursor = database.query(DBOpenHelper.TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID, DBOpenHelper.ALL_COLUMNS_RETRIEVED_OFFLINE_MESSAGE_UUID,
                        selection, null, null, null, null);
                break;
            case RECEIVED_ONLINE_MESSAGES:
                _cursor = database.query(DBOpenHelper.TABLE_RECEIVED_ONLINE_MESSAGE, DBOpenHelper.ALL_COLUMNS_RECEIVED_ONLINE_MESSAGE,
                        selection, null, null, null, null);
                break;
            case RECEIVED_ONLINE_GROUP_MESSAGES:
                _cursor = database.query(DBOpenHelper.TABLE_RECEIVED_ONLINE_GROUP_MESSAGE, DBOpenHelper.ALL_COLUMNS_RECEIVED_ONLINE_GROUP_MESSAGE,
                        selection, null, null, null, null);
                break;
            case CREATOR_CONTACTS:
                _cursor = database.query(DBOpenHelper.TABLE_CREATOR_CONTACT, DBOpenHelper.ALL_COLUMNS_CREATOR_CONTACT,
                        selection, null, null, null, null);
                break;
            case CREATOR_POSTS_LIKEDS:
                _cursor = database.query(DBOpenHelper.TABLE_CREATOR_POSTS_LIKED, DBOpenHelper.ALL_COLUMNS_CREATOR_POSTS_LIKED,
                        selection, null, null, null, null);
                break;
            case USER_ONE_TIME_PRE_KEY_PAIRS:
                _cursor = database.query(DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR, DBOpenHelper.ALL_COLUMNS_USER_ONE_TIME_PRE_KEY_PAIR,
                        selection, null, null, null, null);
                break;
            case GROUP_ONE_TIME_PRE_KEY_PAIRS:
                _cursor = database.query(DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR, DBOpenHelper.ALL_COLUMNS_GROUP_ONE_TIME_PRE_KEY_PAIR,
                        selection, null, null, null, null);
                break;

            default: throw new SQLException("Failed to retrieve row from " + uri);
        }

        return _cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;

        switch (uriMatcher.match(uri)){
            case CONTACTS:
                long contact_id = database.insert(DBOpenHelper.TABLE_CONTACT, null, values);

                //---if added successfully---
                if (contact_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CONTACT, contact_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case CHATS:
                long chat_id = database.insert(DBOpenHelper.TABLE_CHAT, null, values);

                //---if added successfully---
                if (chat_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CHAT, chat_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case GROUP_CHATS:
                long group_chat_id = database.insert(DBOpenHelper.TABLE_GROUP_CHAT, null, values);

                //---if added successfully---
                if (group_chat_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_CHAT, group_chat_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case GROUP_CHAT_MEMBERS:
                long group_chat_member_id = database.insert(DBOpenHelper.TABLE_GROUP_CHAT_MEMBER, null, values);

                //---if added successfully---
                if (group_chat_member_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_CHAT_MEMBER, group_chat_member_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case MESSAGES:
                long message_id = database.insert(DBOpenHelper.TABLE_MESSAGE, null, values);

                //---if added successfully---
                if (message_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE, message_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case MESSAGE_QUEUE_ITEMS:
                long message_queue_item_id = database.insert(DBOpenHelper.TABLE_MESSAGE_QUEUE, null, values);

                //---if added successfully---
                if (message_queue_item_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE_QUEUE, message_queue_item_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case GROUP_MESSAGE_QUEUE_ITEMS:
                long group_message_queue_item_id = database.insert(DBOpenHelper.TABLE_GROUP_MESSAGE_QUEUE, null, values);

                //---if added successfully---
                if (group_message_queue_item_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_MESSAGE_QUEUE, group_message_queue_item_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case USERS:
                long u_id = database.insert(DBOpenHelper.TABLE_USER, null, values);

                //---if added successfully---
                if (u_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER, u_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case OFFLINE_CONTACT_RESPONSES:
                long off_id = database.insert(DBOpenHelper.TABLE_OFFLINE_CONTACT_RESPONSE, null, values);

                //---if added successfully---
                if (off_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_OFFLINE_CONTACT_RESPONSE, off_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case RETRIEVED_OFFLINE_MESSAGE_UUIDS:
                long r_off_id = database.insert(DBOpenHelper.TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID, null, values);

                //---if added successfully---
                if (r_off_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RETRIEVED_OFFLINE_MESSAGE_UUID, r_off_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case RECEIVED_ONLINE_MESSAGES:
                long r_on_id = database.insert(DBOpenHelper.TABLE_RECEIVED_ONLINE_MESSAGE, null, values);

                //---if added successfully---
                if (r_on_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RECEIVED_ONLINE_MESSAGE, r_on_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case RECEIVED_ONLINE_GROUP_MESSAGES:
                long r_ong_id = database.insert(DBOpenHelper.TABLE_RECEIVED_ONLINE_GROUP_MESSAGE, null, values);

                //---if added successfully---
                if (r_ong_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RECEIVED_ONLINE_GROUP_MESSAGE, r_ong_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case CREATOR_CONTACTS:
                long cc_id = database.insert(DBOpenHelper.TABLE_CREATOR_CONTACT, null, values);

                //---if added successfully---
                if (cc_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CREATOR_CONTACT, cc_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case CREATOR_POSTS_LIKEDS:
                long cpl_id = database.insert(DBOpenHelper.TABLE_CREATOR_POSTS_LIKED, null, values);

                //---if added successfully---
                if (cpl_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CREATOR_CONTACT, cpl_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case USER_ONE_TIME_PRE_KEY_PAIRS:
                long uotpkp_id = database.insert(DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR, null, values);

                //---if added successfully---
                if (uotpkp_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER_ONE_TIME_PRE_KEY_PAIR, uotpkp_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;
            case GROUP_ONE_TIME_PRE_KEY_PAIRS:
                long gotpkp_id = database.insert(DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR, null, values);

                //---if added successfully---
                if (gotpkp_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_ONE_TIME_PRE_KEY_PAIR, gotpkp_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                break;

            default: throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        Uri _uri = null;
        int result;

        switch (uriMatcher.match(uri)){
            case CONTACTS:
                long contact_id =  database.delete(DBOpenHelper.TABLE_CONTACT, selection, selectionArgs);

                //---if added successfully---
                if (contact_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CONTACT, contact_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)contact_id;
                break;
            case CHATS:
                long chat_id =  database.delete(DBOpenHelper.TABLE_CHAT, selection, selectionArgs);

                //---if added successfully---
                if (chat_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CHAT, chat_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)chat_id;
                break;
            case GROUP_CHATS:
                long group_chat_id = database.delete(DBOpenHelper.TABLE_GROUP_CHAT, selection, selectionArgs);

                //---if added successfully---
                if (group_chat_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_CHAT, group_chat_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)group_chat_id;
                break;
            case GROUP_CHAT_MEMBERS:
                long group_chat_member_id = database.delete(DBOpenHelper.TABLE_GROUP_CHAT_MEMBER, selection, selectionArgs);

                //---if added successfully---
                if (group_chat_member_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_CHAT_MEMBER, group_chat_member_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)group_chat_member_id;
                break;
            case MESSAGES:
                long message_id = database.delete(DBOpenHelper.TABLE_MESSAGE, selection, selectionArgs);

                //---if added successfully---
                if (message_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE, message_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)message_id;
                break;
            case MESSAGE_QUEUE_ITEMS:
                long message_queue_item_id = database.delete(DBOpenHelper.TABLE_MESSAGE_QUEUE, selection, selectionArgs);

                //---if added successfully---
                if (message_queue_item_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE_QUEUE, message_queue_item_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)message_queue_item_id;
                break;
            case GROUP_MESSAGE_QUEUE_ITEMS:
                long group_message_queue_item_id = database.delete(DBOpenHelper.TABLE_GROUP_MESSAGE_QUEUE, selection, selectionArgs);

                //---if added successfully---
                if (group_message_queue_item_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_MESSAGE_QUEUE, group_message_queue_item_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)group_message_queue_item_id;
                break;
            case USERS:
                long u_id = database.delete(DBOpenHelper.TABLE_USER, selection, selectionArgs);

                //---if added successfully---
                if (u_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER, u_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)u_id;
                break;
            case OFFLINE_CONTACT_RESPONSES:
                long off_id = database.delete(DBOpenHelper.TABLE_OFFLINE_CONTACT_RESPONSE, selection, selectionArgs);

                //---if added successfully---
                if (off_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_OFFLINE_CONTACT_RESPONSE, off_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)off_id;
                break;
            case RETRIEVED_OFFLINE_MESSAGE_UUIDS:
                long r_off_id = database.delete(DBOpenHelper.TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID, selection, selectionArgs);

                //---if added successfully---
                if (r_off_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RETRIEVED_OFFLINE_MESSAGE_UUID, r_off_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)r_off_id;
                break;
            case RECEIVED_ONLINE_MESSAGES:
                long r_on_id = database.delete(DBOpenHelper.TABLE_RECEIVED_ONLINE_MESSAGE, selection, selectionArgs);

                //---if added successfully---
                if (r_on_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RECEIVED_ONLINE_MESSAGE, r_on_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)r_on_id;
                break;
            case RECEIVED_ONLINE_GROUP_MESSAGES:
                long r_ong_id = database.delete(DBOpenHelper.TABLE_RECEIVED_ONLINE_GROUP_MESSAGE, selection, selectionArgs);

                //---if added successfully---
                if (r_ong_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RECEIVED_ONLINE_GROUP_MESSAGE, r_ong_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)r_ong_id;
                break;
            case CREATOR_CONTACTS:
                long cc_id = database.delete(DBOpenHelper.TABLE_CREATOR_CONTACT, selection, selectionArgs);

                //---if added successfully---
                if (cc_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CREATOR_CONTACT, cc_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)cc_id;
                break;
            case CREATOR_POSTS_LIKEDS:
                long cpl_id = database.delete(DBOpenHelper.TABLE_CREATOR_POSTS_LIKED, selection, selectionArgs);

                //---if added successfully---
                if (cpl_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CREATOR_POSTS_LIKED, cpl_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)cpl_id;
                break;
            case USER_ONE_TIME_PRE_KEY_PAIRS:
                long uotpkp_id = database.delete(DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR, selection, selectionArgs);

                //---if added successfully---
                if (uotpkp_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER_ONE_TIME_PRE_KEY_PAIR, uotpkp_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)uotpkp_id;
                break;
            case GROUP_ONE_TIME_PRE_KEY_PAIRS:
                long gotpkp_id = database.delete(DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR, selection, selectionArgs);

                //---if added successfully---
                if (gotpkp_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_ONE_TIME_PRE_KEY_PAIR, gotpkp_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)gotpkp_id;
                break;

            default: throw new SQLException("Failed to delete row in " + uri);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        Uri _uri = null;
        int result;

        switch (uriMatcher.match(uri)){
            case CONTACTS:
                long contact_id =  database.update(DBOpenHelper.TABLE_CONTACT, values, selection, selectionArgs);

                //---if added successfully---
                if (contact_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CONTACT, contact_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)contact_id;
                break;
            case CHATS:
                long chat_id =  database.update(DBOpenHelper.TABLE_CHAT, values, selection, selectionArgs);

                //---if added successfully---
                if (chat_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CHAT, chat_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)chat_id;
                break;
            case GROUP_CHATS:
                long group_chat_id = database.update(DBOpenHelper.TABLE_GROUP_CHAT, values, selection, selectionArgs);

                //---if added successfully---
                if (group_chat_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_CHAT, group_chat_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)group_chat_id;
                break;
            case GROUP_CHAT_MEMBERS:
                long group_chat_member_id = database.update(DBOpenHelper.TABLE_GROUP_CHAT_MEMBER, values, selection, selectionArgs);

                //---if added successfully---
                if (group_chat_member_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_CHAT_MEMBER, group_chat_member_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)group_chat_member_id;
                break;
            case MESSAGES:
                long message_id = database.update(DBOpenHelper.TABLE_MESSAGE, values, selection, selectionArgs);

                //---if added successfully---
                if (message_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE, message_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)message_id;
                break;
            case MESSAGE_QUEUE_ITEMS:
                long message_queue_item_id = database.update(DBOpenHelper.TABLE_MESSAGE_QUEUE, values, selection, selectionArgs);

                //---if added successfully---
                if (message_queue_item_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_MESSAGE_QUEUE, message_queue_item_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)message_queue_item_id;
                break;
            case GROUP_MESSAGE_QUEUE_ITEMS:
                long group_message_queue_item_id = database.update(DBOpenHelper.TABLE_GROUP_MESSAGE_QUEUE, values, selection, selectionArgs);

                //---if added successfully---
                if (group_message_queue_item_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_MESSAGE_QUEUE, group_message_queue_item_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)group_message_queue_item_id;
                break;
            case USERS:
                long u_id = database.update(DBOpenHelper.TABLE_USER, values, selection, selectionArgs);

                //---if added successfully---
                if (u_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER, u_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)u_id;
                break;
            case OFFLINE_CONTACT_RESPONSES:
                long off_id = database.update(DBOpenHelper.TABLE_OFFLINE_CONTACT_RESPONSE, values, selection, selectionArgs);

                //---if added successfully---
                if (off_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_OFFLINE_CONTACT_RESPONSE, off_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)off_id;
                break;
            case RETRIEVED_OFFLINE_MESSAGE_UUIDS:
                long r_off_id = database.update(DBOpenHelper.TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID, values, selection, selectionArgs);

                //---if added successfully---
                if (r_off_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RETRIEVED_OFFLINE_MESSAGE_UUID, r_off_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)r_off_id;
                break;
            case RECEIVED_ONLINE_MESSAGES:
                long r_on_id = database.update(DBOpenHelper.TABLE_RECEIVED_ONLINE_MESSAGE, values, selection, selectionArgs);

                //---if added successfully---
                if (r_on_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RECEIVED_ONLINE_MESSAGE, r_on_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)r_on_id;
                break;
            case RECEIVED_ONLINE_GROUP_MESSAGES:
                long r_ong_id = database.update(DBOpenHelper.TABLE_RECEIVED_ONLINE_GROUP_MESSAGE, values, selection, selectionArgs);

                //---if added successfully---
                if (r_ong_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_RECEIVED_ONLINE_GROUP_MESSAGE, r_ong_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)r_ong_id;
                break;
            case CREATOR_CONTACTS:
                long cc_id = database.update(DBOpenHelper.TABLE_CREATOR_CONTACT, values, selection, selectionArgs);

                //---if added successfully---
                if (cc_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CREATOR_CONTACT, cc_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)cc_id;
                break;
            case CREATOR_POSTS_LIKEDS:
                long cpl_id = database.update(DBOpenHelper.TABLE_CREATOR_POSTS_LIKED, values, selection, selectionArgs);

                //---if added successfully---
                if (cpl_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CREATOR_POSTS_LIKED, cpl_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }

                result = (int)cpl_id;
                break;
            case USER_ONE_TIME_PRE_KEY_PAIRS:
                long uotpkp_id = database.update(DBOpenHelper.TABLE_USER_ONE_TIME_PRE_KEY_PAIR, values, selection, selectionArgs);

                //---if added successfully---
                if (uotpkp_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER_ONE_TIME_PRE_KEY_PAIR, uotpkp_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)uotpkp_id;
                break;
            case GROUP_ONE_TIME_PRE_KEY_PAIRS:
                long gotpkp_id = database.update(DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR, values, selection, selectionArgs);

                //---if added successfully---
                if (gotpkp_id > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_GROUP_ONE_TIME_PRE_KEY_PAIR, gotpkp_id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                result = (int)gotpkp_id;
                break;

            default: throw new SQLException("Failed to update row in " + uri);
        }
        return result;
    }
}


