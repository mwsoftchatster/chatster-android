package nl.mwsoft.www.chatster.modelLayer.constantRegistry;



public class ConstantRegistry {
    public static final String CONTACT_REQUEST = "CONTACT_REQUEST";
    public static final String CONTACTS_LIST = "CONTACTS_LIST";
    public static final String CHATS_LIST = "CHATS_LIST";
    public static final String CHATS_SETTINGS = "CHATS_SETTINGS";
    public static final String IMAGE_DETAIL = "IMAGE_DETAIL";
    public static final String IMAGE_DETAIL_REQUEST = "IMAGE_DETAIL_REQUEST";
    public static final String CHAT_REQUEST = "CHAT_REQUEST";
    public static final String GROUP_CHATS_LIST_REQUEST = "GROUP_CHATS_LIST_REQUEST";
    public static final String GROUP_CHATS_LIST = "GROUP_CHATS_LIST";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String TEXT = "text";
    public static final String JOINED_GROUP_CHAT = "joined_group_chat";
    public static final String LOADING = "LOADING";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String DONE = "done";
    public static final String GROUP_IMAGE = "group image";
    public static final String LEFT_GROUP = "leftGroup";
    public static final String DEFAULT = "default";
    public static final String GROUP_CHAT_ID = "groupChatId";
    public static final String AVAILABLE = "available";
    public static final String UNAVAILABLE = "unavailable";
    public static final String YES = "yes";
    public static final String NO = "no";


    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1001;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1002;


    public static final String BASE_SERVER_URL = "https://chatster-net-lbc-ec3426b7dcd55e97.elb.us-west-2.amazonaws.com";
    public static final String IMAGE_URL_PREFIX = "https:";

    public static final String CHATSTER_API_USER_PORT = ":3000";
    public static final String CHATSTER_API_USER_Q_PORT = ":3100";
    public static final String CHATSTER_E2E_PORT = ":4000";
    public static final String CHATSTER_E2E_Q_PORT = ":4100";
    public static final String CHATSTER_CHAT_PORT = ":5000";
    public static final String CHATSTER_CHAT_Q_PORT = ":5100";
    public static final String CHATSTER_GROUP_E2E_PORT = ":6000";
    public static final String CHATSTER_GROUP_E2E_Q_PORT = ":6100";
    public static final String CHATSTER_GROUP_CHAT_PORT = ":7000";
    public static final String CHATSTER_GROUP_CHAT_Q_PORT = ":7100";
    public static final String CHATSTER_CREATORS_PORT = ":8000";
    public static final String CHATSTER_CREATORS_Q_PORT = ":8100";


    public static final String CHATSTER_GROUP_CHAT_MESSAGE = "groupchat";
    public static final String CHATSTER_ONE_TO_ONE_CHAT_MESSAGE = "chat";
    public static final String CHATSTER_SPY_CHAT_MESSAGE = "spyChat";
    public static final String CHATSTER_SPY_CHAT_CONNECTION = "connectionToSpyChat";
    public static final String CHATSTER_OPEN_GROUP_CHAT_MESSAGE = "opengroupchat";
    public static final String CHATSTER_OPEN_CHAT_MESSAGE = "openchat";
    public static final String CHATSTER_HANDLE_CHAT_MESSAGE = "message";
    public static final String CHATSTER_HANDLE_GROUP_CHAT_MESSAGE = "groupchatmessage";
    public static final String CHATSTER_HANDLE_CHAT_ONLINE_STATUS = "contactOnline";
    public static final String CHATSTER_MESSAGE_RECEIVED = "messageReceived";
    public static final String CHATSTER_GROUP_MESSAGE_RECEIVED = "groupMessageReceived";
    public static final String CHATSTER_SAVE_CREATORS_POST = "saveCreatorPost";
    public static final String CHATSTER_SAVE_CREATORS_TEXT_POST = "saveCreatorTextPost";
    public static final String CHATSTER_CONNECT_WITH_CREATOR = "connectWithCreator";
    public static final String CHATSTER_DISCONNECT_WITH_CREATOR = "disconnectWithCreator";
    public static final String CHATSTER_LIKE_CREATORS_POST = "likeCreatorPost";
    public static final String CHATSTER_UNLIKE_CREATORS_POST = "unlikeCreatorPost";
    public static final String CHATSTER_DELETE_CREATORS_POST = "deleteCreatorPost";
    public static final String CHATSTER_SEARCH_FOR_CREATOR = "searchForCreator";
    public static final String CHATSTER_POST_COMMENT_FOR_CREATOR_POST = "postCommentForCreatorPost";

    public static final String CHATSTER_IMAGE_MESSAGE = "Sent you an image";

    public static final String CHATSTER_EMPTY_STRING = "";
    public static final String CHATSTER_SPACE_STRING = " ";
    public static final String CHATSTER_EQUALS = "=";
    public static final String CHATSTER_OPEN_SQUARE_BRACKETS = "[";
    public static final String CHATSTER_CLOSE_SQUARE_BRACKETS = "]";
    public static final String CHATSTER_OPEN_ROUND_BRACKETS = "(";
    public static final String CHATSTER_CLOSE_ROUND_BRACKETS = ")";
    public static final String CHATSTER_FORWARD_SLASH = "/";
    public static final String CHATSTER_LETTER_N = "N";
    public static final String CHATSTER_COMMA = ",";
    public static final String CHATSTER_POINT = ".";
    public static final String CHATSTER_STAR = "*";
    public static final String CHATSTER_SEMICOLON = ";";
    public static final String CHATSTER_HASH_TAG = "#";
    public static final String CHATSTER_MINUS = "-";
    public static final String CHATSTER_DASH = "-";
    public static final String CHATSTER_AT = "@";
    public static final String CHATSTER_QUESTION_MARK = "?";

    public static final String CHATSTER_PLUS = "+";
    public static final String CHATSTER_ZERO = "0";
    public static final String CHATSTER_DOUBLE_ZERO = "00";
    public static final String CHATSTER_HI_MY_NAME_IS = "Hi, my name is ";
    public static final String CHATSTER_GROUP_CHAT_INVITATION_MSG = "groupChatInvitation";
    public static final String CHATSTER_CHAT_MSG = "chatMsg";
    public static final String CHATSTER_MESSAGE_TYPE = "msgType";
    public static final String CHATSTER_GROUP_CHAT_MSG = "groupChatMsg";
    public static final String CHATSTER_SELECT_PICTURE = "Select Picture";
    public static final String CHATSTER_SELECT_VIDEO = "Select Video";
    public static final String CHATSTER_UPDATE_USER = "updateUser";
    public static final String CHATSTER_UPDATED_USER = "updatedUser";
    public static final String CHATSTER_UPDATE_GROUP = "updateGroup";
    public static final String CHATSTER_UPDATED_GROUP = "updatedGroup";
    public static final String CHATSTER_UPDATED_UNSEND = "updatedUnsend";
    public static final String CHATSTER_UNSEND_ALLOW = "unsendAllow";
    public static final String CHATSTER_UNSEND_FORBID = "unsendForbid";
    public static final String CHATSTER_UNSEND_MESSAGE = "unsendMessage";
    public static final String CHATSTER_UNSEND_MESSAGE_GROUP = "unsendMessageGroup";
    public static final String CHATSTER_NOT_ALLOWED_UNSEND_MESSAGE = "notAllowedToUnsend";
    public static final String CHATSTER_MESSAGE_DELIVERY_STATUS = "messageDeliveryStatus";
    public static final String CHATSTER_USERNAME_AVAILABILITY = "checkUserNameAvailability";


    public static final String CHATSTER_PHONE_TO_VERIFY = "phoneToVerify";
    public static final String CHATSTER_VERIFY_AGE = "minimumAgeDialog";

    public static final String CHATSTER_SPY_MODE_QUESTION = "spyModeQuestion";
    public static final String CHATSTER_CONTACT_NOT_REGISTERED = "notRegistered";
    public static final String CHATSTER_CONTACT_NAME = "conatctName";


    public static final String CHATSTER_PRIVACY_POLICY_URL = "http://www.mwsoft.nl/chatster/privacy-policy.html";
    public static final String CHATSTER_TERMS_AND_POLICIES_URL = "http://www.mwsoft.nl/chatster/terms-policies.html";
    public static final String CHATSTER_GDPR_URL = "http://www.mwsoft.nl/chatster/gdpr-rights.html";
    public static final CharSequence CHATSTER_PASTE = "PASTE";

    public static final int MAX_HEIGHT = 1024;
    public static final int MAX_WIDTH = 1024;

    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int PICK_VIDEO_REQUEST = 3;


    public static final String ACCEPT_GROUP_INVITATION_INTENT = "ACCEPT_GROUP_INVITATION_INTENT";
    public static final String ACCEPT_GROUP_INVITATION_REQUEST = "ACCEPT_GROUP_INVITATION_REQUEST";
    public static final String DECLINE_GROUP_INVITATION_INTENT = "DECLINE_GROUP_INVITATION_INTENT";
    public static final String DECLINE_GROUP_INVITATION_REQUEST = "DECLINE_GROUP_INVITATION_REQUEST";

    public static final String READ_GROUP_CHAT_MESSAGE_INTENT = "READ_GROUP_CHAT_MESSAGE_INTENT";
    public static final String READ_GROUP_CHAT_MESSAGE_REQUEST = "READ_GROUP_CHAT_MESSAGE_REQUEST";

    public static final String READ_MESSAGE_INTENT = "READ_MESSAGE_INTENT";
    public static final String READ_MESSAGE_REQUEST = "READ_MESSAGE_REQUEST";

    public static final String ACCEPT_MESSAGE_FROM_UNKNOWN_INTENT = "ACCEPT_MESSAGE_FROM_UNKNOWN_INTENT";
    public static final String ACCEPT_MESSAGE_FROM_UNKNOWN_REQUEST = "ACCEPT_MESSAGE_FROM_UNKNOWN_REQUEST";
    public static final String DECLINE_MESSAGE_FROM_UNKNOWN_INTENT = "DECLINE_MESSAGE_FROM_UNKNOWN_INTENT";
    public static final String DECLINE_MESSAGE_FROM_UNKNOWN_REQUEST = "DECLINE_MESSAGE_FROM_UNKNOWN_REQUEST";

    public static final int REQUEST_CODE = 10;

    public static final int READ_CONTACTS = 0;
    public static final int MAKE_MANAGE_CALLS = 1;
    public static final int ACCESS_FILES = 2;
    public static final int PERMISSION_DENIED = 3;
    public static final int ALL_PERMISSIONS_GRANTED = 4;

    public static final String NULL = "NULL";

    public static final String CHATSTER_FILE_PROVIDER = "nl.mwsoft.www.chatster.fileprovider";

    public static final int LOCAL_NOTIFICATION_JOB_SERVICE = 1002;
    public static final int CONTACT_LATEST_NOTIFICATION_JOB_SERVICE = 1004;
    public static final int RESEND_MESSAGE_JOB_SERVICE = 1005;


    public static final int ACCEPT_PENDING_INTENT_REQUEST = 1002;
    public static final int DECLINE_PENDING_INTENT_REQUEST = 1001;
    public static final int REPLY_MESSAGE_PENDING_INTENT_REQUEST = 1000;

    public static final String FRAGMENT_TAG = "fragment_tag";


    public static final String CHATSTER_DOCUMENT_TYPE_IMAGE = "image/*";
    public static final String CHATSTER_DOCUMENT_TYPE_VIDEO = "video/*";


    public static final String CHATSTER_SENDER_ID = "senderId";
    public static final String CHATSTER_GROUP_CHAT_ID = "groupChatId";
    public static final String CHATSTER_MESSAGE_TEXT = "messageText";
    public static final String CHATSTER_MESSAGE_CREATED = "messageCreated";
    public static final String CHATSTER_MESSAGE_UUID = "uuid";
    public static final String CHATSTER_MESSAGE_PBK_UUID = "groupMemberPBKUUID";
    public static final String CHATSTER_MESSAGE_CONTACT_PK_UUID = "contactPublicKeyUUID";
    public static final String CHATSTER_MESSAGE_STATUS = "status";
    public static final String CHATSTER_MESSAGE_CHAT_NAME = "chatname";
    public static final String CHATSTER_SPY_CHAT_ACTION = "action";
    public static final String CHATSTER_SPY_CHAT_ACTION_JOIN = "join";
    public static final String CHATSTER_SPY_CHAT_ACTION_DISCONNECT = "disconnect";
    public static final String CHATSTER_SPY_CHAT_ACTION_JOINED = "joined";
    public static final String CHATSTER_SPY_CHAT_ACTION_REJECTED = "rejected";
    public static final String CHATSTER_SPY_CHAT_ACTION_SPY_IS_OFFLINE = "spyIsOffline";

    public static final String CHATSTER_CREATORS_POST_UUID = "uuid";
    public static final String CHATSTER_CREATORS_POST_STATUS = "status";
    public static final String CHATSTER_CREATORS_POST_UPDATED_LIKES = "updatedLikes";
    public static final String CHATSTER_CREATORS_NAME = "creatorsName";


    public static final String OFFLINE_MESSAGE_NOTIFICATION = "{data_type=offline_message}";
    public static final String GROUP_OFFLINE_MESSAGE_NOTIFICATION = "{data_type=group_offline_message}";
    public static final String GROUP_INVITATION_NOTIFICATION = "{data_type=group_invitation_message}";
    public static final String CREATOR_POST_NOTIFICATION = "{data_type=creator_post_message}";
    public static final String CREATOR_FOLLOW_NOTIFICATION = "{data_type=creator_follow_message}";
    public static final String CREATOR_UNFOLLOW_NOTIFICATION = "{data_type=creator_unfollow_message}";
    public static final String CREATOR_POST_COMMENT_NOTIFICATION = "{data_type=creator_post_comment_message}";
    public static final String CREATOR_POST_LIKE_NOTIFICATION = "{data_type=creator_post_like_message}";
    public static final String CREATOR_POST_UNLIKE_NOTIFICATION = "{data_type=creator_post_unlike_message}";


    public static final String NEW_GROUP_MEMBERS_ADDED = "New members added to the group.";

    public static final String CHATSTER_DATE_TIME_FORMAT = "yyyyMMdd_HHmmss";
    public static final String CHATSTER_JPG_EXTENSION = ".jpg";
    public static final String CHATSTER_MP4_EXTENSION = ".mp4";
    public static final String CHATSTER_IMAGE_NAME_PART1 = "JPEG_";
    public static final String CHATSTER_VIDEO_NAME_PART1 = "MP4_";
    public static final String CHATSTER_IMAGE_NAME_PART2 = "_";
    public static final String CHATSTER_VIDEO_NAME_PART2 = "_";
    public static final String CHATSTER_PROJECTION_ORIENTATION = "orientation";
    public static final String CHATSTER_PROJECTION_DATE_ADDED = "date_added";
    public static final String CHATSTER_SORT_ORDER_DATE_ADDED_DESC = "date_added desc";
    public static final String CHATSTER_TEMP_IMAGE_NAME= "temporary_file.jpg";


    public static final int SPEECH_INPUT_REQUEST = 100;


    public static final int INTRO_VIEW_PAGER_FIRST_PAGE = 0;
    public static final int INTRO_VIEW_PAGER_LAST_PAGE = 5;
    public static final int INTRO_VIEW_PAGER_ONE_BEFORE_LAST_PAGE = 4;
    public static final int INTRO_VIEW_PAGER_TOTAL_AMOUNT_PAGES = 6;

    public static final int ALLOWED_TO_UNSEND = 1;
    public static final int NOT_ALLOWED_TO_UNSEND = 0;

    public static final String CREATORS_POST_TYPE = "postType";
    public static final String CREATORS_POST_TEXT = "postText";
    public static final String CREATORS_URI = "uri";
    public static final String CREATORS_PHOTO_URI = "photoUri";
    public static final String CREATORS_VIDEO_URI = "videoUri";
    public static final String CREATORS_POST = "creatorPost";
    public static final String CREATORS_PROFILE = "creatorProfile";

    public static final String CREATORS_NOTIFICATION_TYPE_POST = "post";
    public static final String CREATORS_NOTIFICATION_TYPE_POST_LIKE = "postLike";
    public static final String CREATORS_NOTIFICATION_TYPE_POST_UNLIKE = "postUnlike";
    public static final String CREATORS_NOTIFICATION_TYPE_POST_COMMENT = "postComment";

    public static final String READ_HISTORY_ITEM_INTENT = "READ_HISTORY_ITEM_INTENT";
    public static final String READ_HISTORY_ITEM_REQUEST = "READ_HISTORY_ITEM_REQUEST";

    public static final int READ_HISTORY_ITEM_PENDING_INTENT_REQUEST = 1005;

    public static final String USER_NAME = "userName";
    public static final String INVITEE_NAME = "inviteeName";

    public static final int AMOUNT_OF_ONE_TIME_KEY_PAIRS_AT_REGISTRATION = 20;
    public static final int AMOUNT_OF_ONE_TIME_KEY_PAIRS_AT_REPLENISHMENT = 20;

    public static final int AMOUNT_OF_GROUP_ONE_TIME_KEY_PAIRS_AT_CREATION = 20;
    public static final int AMOUNT_OF_GROUP_ONE_TIME_KEY_PAIRS_AT_REPLENISHMENT = 20;
}
