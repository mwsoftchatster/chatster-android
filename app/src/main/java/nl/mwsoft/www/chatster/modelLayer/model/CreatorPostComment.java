package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

public class CreatorPostComment implements Parcelable{

    private String _id;
    private String postUUID;
    private String creatorsName;
    private String userProfilePicUrl;
    private String comment;
    private String commentCreated;

    public CreatorPostComment() {
    }

    public CreatorPostComment(String _id, String postUUID, String creatorsName, String userProfilePicUrl, String comment, String commentCreated) {
        this._id = _id;
        this.postUUID = postUUID;
        this.creatorsName = creatorsName;
        this.userProfilePicUrl = userProfilePicUrl;
        this.comment = comment;
        this.commentCreated = commentCreated;
    }

    protected CreatorPostComment(Parcel in) {
        _id = in.readString();
        postUUID = in.readString();
        creatorsName = in.readString();
        userProfilePicUrl = in.readString();
        comment = in.readString();
        commentCreated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(postUUID);
        dest.writeString(creatorsName);
        dest.writeString(userProfilePicUrl);
        dest.writeString(comment);
        dest.writeString(commentCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreatorPostComment> CREATOR = new Creator<CreatorPostComment>() {
        @Override
        public CreatorPostComment createFromParcel(Parcel in) {
            return new CreatorPostComment(in);
        }

        @Override
        public CreatorPostComment[] newArray(int size) {
            return new CreatorPostComment[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPostUUID() {
        return postUUID;
    }

    public void setPostUUID(String postUUID) {
        this.postUUID = postUUID;
    }

    public String getCreatorsName() {
        return creatorsName;
    }

    public void setCreatorsName(String creatorsName) {
        this.creatorsName = creatorsName;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentCreated() {
        return commentCreated;
    }

    public void setCommentCreated(String commentCreated) {
        this.commentCreated = commentCreated;
    }
}
