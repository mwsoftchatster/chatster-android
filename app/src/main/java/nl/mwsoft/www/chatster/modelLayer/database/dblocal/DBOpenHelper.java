package nl.mwsoft.www.chatster.modelLayer.database.dblocal;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chatster.db";
    private static final int DATABASE_VERSION = 25;

    // region Contacts

    public static final String TABLE_CONTACT = "contact";
    public static final String CONTACT_ID = "_id";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_STATUS_MESSAGE = "contact_status_message";
    public static final String CONTACT_PROFILE_PIC = "contact_profile_pic";
    public static final String CONTACT_TYPE = "contact_type";
    public static final String CONTACT_CREATED = "contact_created";


    public static final String[] ALL_COLUMNS_CONTACT = {CONTACT_ID, CONTACT_NAME, CONTACT_STATUS_MESSAGE,
            CONTACT_PROFILE_PIC, CONTACT_TYPE, CONTACT_CREATED};


    // SQL to create table contact
    private static final String TABLE_CREATE_CONTACT =
            "CREATE TABLE " + TABLE_CONTACT + " (" +
                    CONTACT_ID + " INTEGER PRIMARY KEY, " +
                    CONTACT_NAME + " TEXT NOT NULL," +
                    CONTACT_STATUS_MESSAGE + " TEXT," +
                    CONTACT_PROFILE_PIC + " TEXT," +
                    CONTACT_TYPE + " INTEGER default 0," +
                    CONTACT_CREATED + " TEXT default CURRENT_TIMESTAMP " +
                    ")";

    // endregion

    // region User

    public static final String TABLE_USER = "user";
    public static final String U_ID = "_id";
    public static final String USER_NAME = "username";
    public static final String USER_PROFILE_PIC_URI = "profile_pic_uri";
    public static final String USER_PROFILE_PIC_URL = "profile_pic_url";
    public static final String STATUS_MESSAGE = "status_message";
    public static final String PHONE_CONFIRM_MESSAGE = "phone_confirm_message";


    public static final String[] ALL_COLUMNS_USER = {U_ID, USER_NAME, USER_PROFILE_PIC_URI, USER_PROFILE_PIC_URL,
            STATUS_MESSAGE, PHONE_CONFIRM_MESSAGE};

    // SQL to create table user
    private static final String TABLE_CREATE_USER =
            "CREATE TABLE " + TABLE_USER + " (" +
                    U_ID + " INTEGER NOT NULL, " +
                    USER_NAME + " TEXT NOT NULL," +
                    USER_PROFILE_PIC_URI + " TEXT," +
                    USER_PROFILE_PIC_URL + " TEXT," +
                    STATUS_MESSAGE + " TEXT, " +
                    PHONE_CONFIRM_MESSAGE + " INTEGER " +
                    ")";

    // insert default user
    private static final String INSERT_DEFAULT_USER = "INSERT INTO " + TABLE_USER +
            " VALUES ( 0, 'default', 'localUri', 's3Url', '', 0)";

    // endregion

    // region Chat

    public static final String TABLE_CHAT = "chat";
    public static final String CHAT_ID = "_id";
    public static final String CHAT_NAME = "chat_name";
    public static final String CHAT_CONTACT_ID = "chat_contact_id";
    public static final String CHAT_LAST_MESSAGE_ID = "last_message_id";
    public static final String CHAT_IS_ALLOWED_UNSEND = "chat_is_allowed_unsend";
    public static final String CHAT_IS_IN_SPY_MODE = "chat_is_in_spy_mode";
    public static final String CHAT_CREATED = "chat_created";


    public static final String[] ALL_COLUMNS_CHAT = {CHAT_ID, CHAT_NAME, CHAT_CONTACT_ID,
            CHAT_LAST_MESSAGE_ID, CHAT_IS_ALLOWED_UNSEND, CHAT_IS_IN_SPY_MODE, CHAT_CREATED};

    // SQL to create table chat
    private static final String TABLE_CREATE_CHAT =
            "CREATE TABLE " + TABLE_CHAT + " (" +
                    CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CHAT_NAME + " TEXT," +
                    CHAT_CONTACT_ID + " INTEGER NOT NULL," +
                    CHAT_LAST_MESSAGE_ID + " INTEGER," +
                    CHAT_IS_ALLOWED_UNSEND + " INTEGER NOT NULL DEFAULT 0," +
                    CHAT_IS_IN_SPY_MODE + " INTEGER NOT NULL DEFAULT 0," +
                    CHAT_CREATED + " TEXT default CURRENT_TIMESTAMP , " +
                    " FOREIGN KEY(" + CHAT_CONTACT_ID + ") REFERENCES " + TABLE_CONTACT + "(" + CONTACT_ID + ")" +
                    ")";

    // endregion

    // region Group Chat

    public static final String TABLE_GROUP_CHAT = "group_chat";
    public static final String GROUP_CHAT_ID = "_id";
    public static final String GROUP_CHAT_ADMIN_ID = "admin_id";
    public static final String GROUP_CHAT_NAME = "group_chat_name";
    public static final String GROUP_CHAT_STATUS_MESSAGE = "group_chat_status_message";
    public static final String GROUP_CHAT_PROFILE_PIC = "group_chat_profile_pic";
    public static final String GROUP_CHAT_LAST_MESSAGE_ID = "group_chat_last_message";
    public static final String GROUP_CHAT_CREATED = "group_chat_created";


    public static final String[] ALL_COLUMNS_GROUP_CHAT = {GROUP_CHAT_ID, GROUP_CHAT_ADMIN_ID, GROUP_CHAT_NAME,
            GROUP_CHAT_STATUS_MESSAGE, GROUP_CHAT_PROFILE_PIC, GROUP_CHAT_LAST_MESSAGE_ID, GROUP_CHAT_CREATED};

    // SQL to create table group chat
    private static final String TABLE_CREATE_GROUP_CHAT =
            "CREATE TABLE " + TABLE_GROUP_CHAT + " (" +
                    GROUP_CHAT_ID + " TEXT PRIMARY KEY, " +
                    GROUP_CHAT_ADMIN_ID + " INTEGER NOT NULL, " +
                    GROUP_CHAT_NAME + " TEXT NOT NULL," +
                    GROUP_CHAT_STATUS_MESSAGE + " TEXT ," +
                    GROUP_CHAT_PROFILE_PIC + " TEXT ," +
                    GROUP_CHAT_LAST_MESSAGE_ID + " INTEGER ," +
                    GROUP_CHAT_CREATED + " TEXT default CURRENT_TIMESTAMP, " +
                    " FOREIGN KEY(" + GROUP_CHAT_ADMIN_ID + ") REFERENCES " + TABLE_CONTACT + "(" + CONTACT_ID + ")" +
                    ")";

    // endregion

    // region Group Chat Member

    public static final String TABLE_GROUP_CHAT_MEMBER = "group_chat_member";
    public static final String GROUP_CHAT_MEMBER_GROUP_CHAT_ID = "group_chat_id";
    public static final String GROUP_CHAT_MEMBER_ID = "group_chat_member_id";


    public static final String[] ALL_COLUMNS_GROUP_CHAT_MEMBER = {GROUP_CHAT_MEMBER_GROUP_CHAT_ID, GROUP_CHAT_MEMBER_ID};

    // SQL to create table chat
    private static final String TABLE_CREATE_GROUP_CHAT_MEMBER =
            "CREATE TABLE " + TABLE_GROUP_CHAT_MEMBER + " (" +
                    GROUP_CHAT_MEMBER_GROUP_CHAT_ID + " TEXT NOT NULL, " +
                    GROUP_CHAT_MEMBER_ID + " INTEGER NOT NULL," +
                    " FOREIGN KEY(" + GROUP_CHAT_MEMBER_GROUP_CHAT_ID + ") REFERENCES " + TABLE_GROUP_CHAT + "(" + GROUP_CHAT_ID + "), " +
                    " FOREIGN KEY(" + GROUP_CHAT_MEMBER_ID + ") REFERENCES " + TABLE_CONTACT + "(" + CONTACT_ID + ")" +
                    ")";

    // endregion

    // region Message

    public static final String TABLE_MESSAGE = "message";
    public static final String MESSAGE_ID = "_id";
    public static final String TYPE_MESSAGE = "type_message";
    public static final String MESSAGE_CHAT_ID = "message_chat_id";
    public static final String MESSAGE_GROUP_CHAT_ID = "message_group_chat_id";
    public static final String MESSAGE_SENDER_ID = "message_sender_id";
    public static final String TEXT_MESSAGE = "text_message";
    public static final String BINARY_MESSAGE_FILE_PATH = "file_path";
    public static final String MESSAGE_HAS_BEEN_READ = "message_has_been_read";
    public static final String MESSAGE_UUID = "message_uuid";
    public static final String MESSAGE_CREATED = "text_message_created";


    public static final String[] ALL_COLUMNS_MESSAGE = {MESSAGE_ID, TYPE_MESSAGE, MESSAGE_CHAT_ID, MESSAGE_GROUP_CHAT_ID,
            MESSAGE_SENDER_ID, TEXT_MESSAGE, BINARY_MESSAGE_FILE_PATH, MESSAGE_HAS_BEEN_READ, MESSAGE_UUID, MESSAGE_CREATED};

    // SQL to create table message
    private static final String TABLE_CREATE_MESSAGE =
            "CREATE TABLE " + TABLE_MESSAGE + " (" +
                    MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TYPE_MESSAGE + " TEXT," +
                    MESSAGE_CHAT_ID + " INTEGER," +
                    MESSAGE_GROUP_CHAT_ID + " TEXT," +
                    MESSAGE_SENDER_ID + " INTEGER NOT NULL," +
                    TEXT_MESSAGE + " TEXT," +
                    BINARY_MESSAGE_FILE_PATH + " TEXT," +
                    MESSAGE_HAS_BEEN_READ + " INTEGER default 0," +
                    MESSAGE_UUID + " TEXT NOT NULL UNIQUE," +
                    MESSAGE_CREATED + " TEXT default CURRENT_TIMESTAMP , " +
                    " FOREIGN KEY(" + MESSAGE_CHAT_ID + ") REFERENCES " + TABLE_CHAT + "(" + CHAT_ID + "), " +
                    " FOREIGN KEY(" + MESSAGE_GROUP_CHAT_ID + ") REFERENCES " + TABLE_GROUP_CHAT + "(" + GROUP_CHAT_ID + "), " +
                    " FOREIGN KEY(" + MESSAGE_SENDER_ID + ") REFERENCES " + TABLE_CONTACT + "(" + CONTACT_ID + ")" +
                    ")";

    // endregion

    // region Message Queue

    public static final String TABLE_MESSAGE_QUEUE = "message_queue";
    public static final String MESSAGE_QUEUE_ITEM_ID = "_id";
    public static final String MESSAGE_QUEUE_MESSAGE_UUID = "message_queue_message_uuid";
    public static final String MESSAGE_QUEUE_MESSAGE_RECEIVER_ID = "message_queue_message_receiver_id";
    public static final String MESSAGE_QUEUE_MESSAGE_CONTACT_PK_UUID = "message_queue_message_contact_pk_uuid";
    public static final String MESSAGE_QUEUE_MESSAGE_USER_PK_UUID = "message_queue_message_user_pk_uuid";


    public static final String[] ALL_COLUMNS_MESSAGE_QUEUE = {MESSAGE_QUEUE_ITEM_ID, MESSAGE_QUEUE_MESSAGE_UUID,
            MESSAGE_QUEUE_MESSAGE_RECEIVER_ID, MESSAGE_QUEUE_MESSAGE_CONTACT_PK_UUID, MESSAGE_QUEUE_MESSAGE_USER_PK_UUID};

    // SQL to create table text_message
    private static final String TABLE_CREATE_MESSAGE_QUEUE =
            "CREATE TABLE " + TABLE_MESSAGE_QUEUE + " (" +
                    MESSAGE_QUEUE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MESSAGE_QUEUE_MESSAGE_UUID + " TEXT NOT NULL UNIQUE," +
                    MESSAGE_QUEUE_MESSAGE_CONTACT_PK_UUID + " TEXT NOT NULL UNIQUE," +
                    MESSAGE_QUEUE_MESSAGE_USER_PK_UUID + " TEXT NOT NULL UNIQUE," +
                    MESSAGE_QUEUE_MESSAGE_RECEIVER_ID + " INTEGER NOT NULL," +
                    " FOREIGN KEY(" + MESSAGE_QUEUE_MESSAGE_UUID + ") REFERENCES " + TABLE_MESSAGE + "(" + MESSAGE_UUID + "), " +
                    " FOREIGN KEY(" + MESSAGE_QUEUE_MESSAGE_RECEIVER_ID + ") REFERENCES " + TABLE_CONTACT + "(" + CONTACT_ID + ")" +
                    ")";

    // endregion

    // region Group Message Queue

    public static final String TABLE_GROUP_MESSAGE_QUEUE = "group_message_queue";
    public static final String GROUP_MESSAGE_QUEUE_ITEM_ID = "_id";
    public static final String GROUP_MESSAGE_QUEUE_MESSAGE_UUID = "group_message_queue_message_uuid";


    public static final String[] ALL_COLUMNS_GROUP_MESSAGE_QUEUE = {GROUP_MESSAGE_QUEUE_ITEM_ID, GROUP_MESSAGE_QUEUE_MESSAGE_UUID};

    // SQL to create table text_message
    private static final String TABLE_CREATE_GROUP_MESSAGE_QUEUE =
            "CREATE TABLE " + TABLE_GROUP_MESSAGE_QUEUE + " (" +
                    GROUP_MESSAGE_QUEUE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GROUP_MESSAGE_QUEUE_MESSAGE_UUID + " TEXT NOT NULL UNIQUE," +
                    " FOREIGN KEY(" + GROUP_MESSAGE_QUEUE_MESSAGE_UUID + ") REFERENCES " + TABLE_MESSAGE + "(" + MESSAGE_UUID + ")" +
                    ")";

    // endregion

    // region Retrieved Offline Message UUID

    public static final String TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID = "retrieved_offline_message_uuid";
    public static final String RETRIEVED_OFFLINE_MESSAGE_UUID_ID = "_id";
    public static final String RETRIEVED_OFFLINE_MESSAGE_UUID = "retrieved_offline_message_uuid_uuid";


    public static final String[] ALL_COLUMNS_RETRIEVED_OFFLINE_MESSAGE_UUID = {RETRIEVED_OFFLINE_MESSAGE_UUID_ID,
            RETRIEVED_OFFLINE_MESSAGE_UUID};

    // SQL to create table retrieved offline message
    private static final String TABLE_CREATE_RETRIEVED_OFFLINE_MESSAGE_UUID =
            "CREATE TABLE " + TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID + " (" +
                    RETRIEVED_OFFLINE_MESSAGE_UUID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RETRIEVED_OFFLINE_MESSAGE_UUID + " TEXT NOT NULL UNIQUE," +
                    " FOREIGN KEY(" + RETRIEVED_OFFLINE_MESSAGE_UUID + ") REFERENCES " + TABLE_MESSAGE + "(" + MESSAGE_UUID + ")" +
                    ")";

    // endregion

    // region Received Online Message

    public static final String TABLE_RECEIVED_ONLINE_MESSAGE = "received_online_message";
    public static final String RECEIVED_ONLINE_MESSAGE_ID = "_id";
    public static final String RECEIVED_ONLINE_MESSAGE_UUID = "received_online_message_uuid";


    public static final String[] ALL_COLUMNS_RECEIVED_ONLINE_MESSAGE = {RECEIVED_ONLINE_MESSAGE_ID,
            RECEIVED_ONLINE_MESSAGE_UUID};

    // SQL to create table text_message
    private static final String TABLE_CREATE_RECEIVED_ONLINE_MESSAGE =
            "CREATE TABLE " + TABLE_RECEIVED_ONLINE_MESSAGE + " (" +
                    RECEIVED_ONLINE_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RECEIVED_ONLINE_MESSAGE_UUID + " TEXT NOT NULL UNIQUE," +
                    " FOREIGN KEY(" + RECEIVED_ONLINE_MESSAGE_UUID + ") REFERENCES " + TABLE_MESSAGE + "(" + MESSAGE_UUID + ")" +
                    ")";

    // endregion

    // region Received Online Group Message

    public static final String TABLE_RECEIVED_ONLINE_GROUP_MESSAGE = "received_online_group_message";
    public static final String RECEIVED_ONLINE_GROUP_MESSAGE_ID = "_id";
    public static final String RECEIVED_ONLINE_GROUP_MESSAGE_UUID = "received_online_group_message_uuid";


    public static final String[] ALL_COLUMNS_RECEIVED_ONLINE_GROUP_MESSAGE = {RECEIVED_ONLINE_GROUP_MESSAGE_ID,
            RECEIVED_ONLINE_GROUP_MESSAGE_UUID};

    // SQL to create table text_message
    private static final String TABLE_CREATE_RECEIVED_ONLINE_GROUP_MESSAGE =
            "CREATE TABLE " + TABLE_RECEIVED_ONLINE_GROUP_MESSAGE + " (" +
                    RECEIVED_ONLINE_GROUP_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RECEIVED_ONLINE_GROUP_MESSAGE_UUID + " TEXT NOT NULL UNIQUE," +
                    " FOREIGN KEY(" + RECEIVED_ONLINE_GROUP_MESSAGE_UUID + ") REFERENCES " + TABLE_MESSAGE + "(" + MESSAGE_UUID + ")" +
                    ")";

    // endregion

    // region Offline Contact Response

    public static final String TABLE_OFFLINE_CONTACT_RESPONSE = "offline_contact_response";
    public static final String OFFLINE_CONTACT_RESPONSE_ID = "_id";
    public static final String OFFLINE_CONTACT_RESPONSE_TYPE_MESSAGE = "message_type";
    public static final String OFFLINE_CONTACT_RESPONSE_USER_ID = "contact_response_user_id";
    public static final String OFFLINE_CONTACT_RESPONSE_USER_NAME = "contact_response_user_name";
    public static final String OFFLINE_CONTACT_RESPONSE_STATUS_MESSAGE = "contact_response_status_message";
    public static final String OFFLINE_CONTACT_RESPONSE_REQUEST_MESSAGE = "contact_response_request_message";
    public static final String OFFLINE_CONTACT_RESPONSE_CONTACT_REQUEST_ID = "contact_response_contact_request_id";
    public static final String OFFLINE_CONTACT_RESPONSE_REQUEST_ID = "contact_response_request_id";
    public static final String OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID = "contact_response_group_chat_invitation_chat_id";
    public static final String OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_SENDER_ID = "contact_response_group_chat_invitation_sender_id";
    public static final String OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_CHAT_NAME = "contact_response_group_chat_invitation_chat_name";
    public static final String OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_PROFILE_IMAGE = "contact_response_group_chat_invitation_profile_image";
    public static final String OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_MEMBERS = "contact_response_group_chat_members";

    public static final String[] ALL_COLUMNS_OFFLINE_CONTACT_RESPONSE = {OFFLINE_CONTACT_RESPONSE_ID,
            OFFLINE_CONTACT_RESPONSE_TYPE_MESSAGE, OFFLINE_CONTACT_RESPONSE_USER_ID, OFFLINE_CONTACT_RESPONSE_USER_NAME,
            OFFLINE_CONTACT_RESPONSE_STATUS_MESSAGE, OFFLINE_CONTACT_RESPONSE_REQUEST_MESSAGE,
            OFFLINE_CONTACT_RESPONSE_CONTACT_REQUEST_ID, OFFLINE_CONTACT_RESPONSE_REQUEST_ID,
            OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID, OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_SENDER_ID,
            OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_CHAT_NAME, OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_PROFILE_IMAGE,
            OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_MEMBERS
            };

    // SQL to create table text_message
    private static final String TABLE_CREATE_OFFLINE_CONTACT_RESPONSE =
            "CREATE TABLE " + TABLE_OFFLINE_CONTACT_RESPONSE + " (" +
                    OFFLINE_CONTACT_RESPONSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    OFFLINE_CONTACT_RESPONSE_TYPE_MESSAGE + " TEXT NOT NULL," +
                    OFFLINE_CONTACT_RESPONSE_USER_ID + " INTEGER," +
                    OFFLINE_CONTACT_RESPONSE_USER_NAME + " TEXT," +
                    OFFLINE_CONTACT_RESPONSE_STATUS_MESSAGE + " TEXT," +
                    OFFLINE_CONTACT_RESPONSE_REQUEST_MESSAGE + " TEXT," +
                    OFFLINE_CONTACT_RESPONSE_CONTACT_REQUEST_ID + " INTEGER," +
                    OFFLINE_CONTACT_RESPONSE_REQUEST_ID + " INTEGER," +
                    OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID + " TEXT," +
                    OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_SENDER_ID + " INTEGER," +
                    OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_CHAT_NAME + " TEXT , " +
                    OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_PROFILE_IMAGE + " TEXT , " +
                    OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_MEMBERS + " TEXT  " +
                    ")";

    // endregion

    // region Creator Contact

    public static final String TABLE_CREATOR_CONTACT = "creator_contact";
    public static final String CREATOR_CONTACT_ID = "_id";
    public static final String CREATOR_CONTACT_STATUS_MESSAGE = "creator_contact_status_message";
    public static final String CREATOR_CONTACT_PROFILE_PIC = "creator_contact_profile_pic";
    public static final String CREATOR_CONTACT_TYPE = "creator_contact_type";
    public static final String CREATOR_CONTACT_CREATED = "creator_contact_created";


    public static final String[] ALL_COLUMNS_CREATOR_CONTACT = {CREATOR_CONTACT_ID, CREATOR_CONTACT_STATUS_MESSAGE,
            CREATOR_CONTACT_PROFILE_PIC, CREATOR_CONTACT_TYPE, CREATOR_CONTACT_CREATED};


    // SQL to create table creator contact
    private static final String TABLE_CREATE_CREATOR_CONTACT =
            "CREATE TABLE " + TABLE_CREATOR_CONTACT + " (" +
                    CREATOR_CONTACT_ID + " TEXT PRIMARY KEY, " +
                    CREATOR_CONTACT_STATUS_MESSAGE + " TEXT," +
                    CREATOR_CONTACT_PROFILE_PIC + " TEXT," +
                    CREATOR_CONTACT_TYPE + " INTEGER default 0," +
                    CREATOR_CONTACT_CREATED + " TEXT default CURRENT_TIMESTAMP " +
                    ")";

    // endregion

    // region Creator Posts Liked

    public static final String TABLE_CREATOR_POSTS_LIKED = "creator_posts_liked";
    public static final String CREATOR_POSTS_LIKED_ID = "_id";
    public static final String CREATOR_POST_UUID = "creator_post_uuid";
    public static final String CREATOR_POST_LIKES = "creator_post_likes";
    public static final String CREATOR_POST_IS_LIKED = "creator_post_is_liked";


    public static final String[] ALL_COLUMNS_CREATOR_POSTS_LIKED = {CREATOR_POSTS_LIKED_ID, CREATOR_POST_UUID,
            CREATOR_POST_LIKES, CREATOR_POST_IS_LIKED};


    // SQL to create table posts liked
    private static final String TABLE_CREATE_POSTS_LIKED =
            "CREATE TABLE " + TABLE_CREATOR_POSTS_LIKED + " (" +
                    CREATOR_POSTS_LIKED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CREATOR_POST_UUID + " TEXT NOT NULL," +
                    CREATOR_POST_LIKES + " INTEGER default 0," +
                    CREATOR_POST_IS_LIKED + " INTEGER default 0 " +
                    ")";

    // endregion

    // region Chat One Time Keys

    public static final String TABLE_USER_ONE_TIME_PRE_KEY_PAIR = "user_one_time_pre_key_pair";
    public static final String USER_ONE_TIME_PRE_KEY_PAIR_ID = "_id";
    public static final String USER_ONE_TIME_PRE_KEY_PAIR_UUID = "user_one_time_pre_key_pair_uuid";
    public static final String USER_ONE_TIME_PRE_KEY_PAIR_USER_ID = "user_id";
    public static final String USER_ONE_TIME_PRE_KEY_PAIR_PRK = "user_one_time_pre_key_pair_prk";
    public static final String USER_ONE_TIME_PRE_KEY_PAIR_PBK = "user_one_time_pre_key_pair_pbk";

    public static final String[] ALL_COLUMNS_USER_ONE_TIME_PRE_KEY_PAIR = {
            USER_ONE_TIME_PRE_KEY_PAIR_ID, USER_ONE_TIME_PRE_KEY_PAIR_UUID,
            USER_ONE_TIME_PRE_KEY_PAIR_USER_ID, USER_ONE_TIME_PRE_KEY_PAIR_PRK, USER_ONE_TIME_PRE_KEY_PAIR_PBK};


    // SQL to create table user_one_time_pre_key_pair
    private static final String TABLE_CREATE_USER_ONE_TIME_PRE_KEY_PAIR =
            "CREATE TABLE " + TABLE_USER_ONE_TIME_PRE_KEY_PAIR + " (" +
                    USER_ONE_TIME_PRE_KEY_PAIR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_ONE_TIME_PRE_KEY_PAIR_UUID + " TEXT NOT NULL," +
                    USER_ONE_TIME_PRE_KEY_PAIR_USER_ID + " INTEGER NOT NULL," +
                    USER_ONE_TIME_PRE_KEY_PAIR_PRK + " BLOB," +
                    USER_ONE_TIME_PRE_KEY_PAIR_PBK + " BLOB" +
                    ")";

    // endregion

    // region Group Chat One Time Keys

    public static final String TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR = "group_one_time_pre_key_pair";
    public static final String GROUP_ONE_TIME_PRE_KEY_PAIR_ID = "_id";
    public static final String GROUP_ONE_TIME_PRE_KEY_PAIR_UUID = "group_one_time_pre_key_pair_uuid";
    public static final String GROUP_ONE_TIME_PRE_KEY_PAIR_GROUP_ID = "group_id";
    public static final String GROUP_ONE_TIME_PRE_KEY_PAIR_PRK = "group_one_time_pre_key_pair_prk";
    public static final String GROUP_ONE_TIME_PRE_KEY_PAIR_PBK = "group_one_time_pre_key_pair_pbk";

    public static final String[] ALL_COLUMNS_GROUP_ONE_TIME_PRE_KEY_PAIR = {
            GROUP_ONE_TIME_PRE_KEY_PAIR_ID, GROUP_ONE_TIME_PRE_KEY_PAIR_UUID,
            GROUP_ONE_TIME_PRE_KEY_PAIR_GROUP_ID, GROUP_ONE_TIME_PRE_KEY_PAIR_PRK, GROUP_ONE_TIME_PRE_KEY_PAIR_PBK};


    // SQL to create table group_one_time_pre_key_pair
    private static final String TABLE_CREATE_GROUP_ONE_TIME_PRE_KEY_PAIR =
            "CREATE TABLE " + TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR + " (" +
                    GROUP_ONE_TIME_PRE_KEY_PAIR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GROUP_ONE_TIME_PRE_KEY_PAIR_UUID + " TEXT NOT NULL," +
                    GROUP_ONE_TIME_PRE_KEY_PAIR_GROUP_ID + " TEXT NOT NULL," +
                    GROUP_ONE_TIME_PRE_KEY_PAIR_PRK + " BLOB," +
                    GROUP_ONE_TIME_PRE_KEY_PAIR_PBK + " BLOB" +
                    ")";

    // endregion


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_CONTACT);
        db.execSQL(TABLE_CREATE_CHAT);
        db.execSQL(TABLE_CREATE_GROUP_CHAT);
        db.execSQL(TABLE_CREATE_GROUP_CHAT_MEMBER);
        db.execSQL(TABLE_CREATE_MESSAGE);
        db.execSQL(TABLE_CREATE_MESSAGE_QUEUE);
        db.execSQL(TABLE_CREATE_GROUP_MESSAGE_QUEUE);
        db.execSQL(TABLE_CREATE_USER);
        db.execSQL(TABLE_CREATE_OFFLINE_CONTACT_RESPONSE);
        db.execSQL(TABLE_CREATE_RETRIEVED_OFFLINE_MESSAGE_UUID);
        db.execSQL(TABLE_CREATE_RECEIVED_ONLINE_MESSAGE);
        db.execSQL(TABLE_CREATE_RECEIVED_ONLINE_GROUP_MESSAGE);
        db.execSQL(TABLE_CREATE_CREATOR_CONTACT);
        db.execSQL(TABLE_CREATE_POSTS_LIKED);
        db.execSQL(TABLE_CREATE_USER_ONE_TIME_PRE_KEY_PAIR);
        db.execSQL(TABLE_CREATE_GROUP_ONE_TIME_PRE_KEY_PAIR);
        db.execSQL(INSERT_DEFAULT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_CHAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_CHAT_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_QUEUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_MESSAGE_QUEUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_CONTACT_RESPONSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIVED_ONLINE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEIVED_ONLINE_GROUP_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATOR_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATOR_POSTS_LIKED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ONE_TIME_PRE_KEY_PAIR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR);
        onCreate(db);
    }
}

