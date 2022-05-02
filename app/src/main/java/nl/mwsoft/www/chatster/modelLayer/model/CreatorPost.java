package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CreatorPost implements Parcelable{

    private String uuid;
    private String creatorProfilePicUrl;
    private String creatorsName;
    @Nullable
    private ArrayList<String> postUrls;
    private String postCaption;
    private String postType;
    @Nullable
    private boolean hasBeenLiked = false;
    private long likes;
    private long comments;
    private String postCreated;
    private int followingThisCreator;
    @Nullable
    private String postText;

    public CreatorPost() {
    }

    public CreatorPost(String uuid, String creatorProfilePicUrl, String creatorsName, @Nullable ArrayList<String> postUrls, String postCaption, String postType, @Nullable boolean hasBeenLiked, long likes, long comments, String postCreated, int followingThisCreator, @Nullable String postText) {
        this.uuid = uuid;
        this.creatorProfilePicUrl = creatorProfilePicUrl;
        this.creatorsName = creatorsName;
        this.postUrls = postUrls;
        this.postCaption = postCaption;
        this.postType = postType;
        this.hasBeenLiked = hasBeenLiked;
        this.likes = likes;
        this.comments = comments;
        this.postCreated = postCreated;
        this.followingThisCreator = followingThisCreator;
        this.postText = postText;
    }

    protected CreatorPost(Parcel in) {
        uuid = in.readString();
        creatorProfilePicUrl = in.readString();
        creatorsName = in.readString();
        postUrls = in.createStringArrayList();
        postCaption = in.readString();
        postType = in.readString();
        hasBeenLiked = in.readByte() != 0;
        likes = in.readLong();
        comments = in.readLong();
        postCreated = in.readString();
        followingThisCreator = in.readInt();
        postText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(creatorProfilePicUrl);
        dest.writeString(creatorsName);
        dest.writeStringList(postUrls);
        dest.writeString(postCaption);
        dest.writeString(postType);
        dest.writeByte((byte) (hasBeenLiked ? 1 : 0));
        dest.writeLong(likes);
        dest.writeLong(comments);
        dest.writeString(postCreated);
        dest.writeInt(followingThisCreator);
        dest.writeString(postText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreatorPost> CREATOR = new Creator<CreatorPost>() {
        @Override
        public CreatorPost createFromParcel(Parcel in) {
            return new CreatorPost(in);
        }

        @Override
        public CreatorPost[] newArray(int size) {
            return new CreatorPost[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreatorProfilePicUrl() {
        return creatorProfilePicUrl;
    }

    public void setCreatorProfilePicUrl(String creatorProfilePicUrl) {
        this.creatorProfilePicUrl = creatorProfilePicUrl;
    }

    public String getCreatorsName() {
        return creatorsName;
    }

    public void setCreatorsName(String creatorsName) {
        this.creatorsName = creatorsName;
    }

    @Nullable
    public ArrayList<String> getPostUrls() {
        return postUrls;
    }

    public void setPostUrls(@Nullable ArrayList<String> postUrls) {
        this.postUrls = postUrls;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    @Nullable
    public boolean isHasBeenLiked() {
        return hasBeenLiked;
    }

    public void setHasBeenLiked(@Nullable boolean hasBeenLiked) {
        this.hasBeenLiked = hasBeenLiked;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public String getPostCreated() {
        return postCreated;
    }

    public void setPostCreated(String postCreated) {
        this.postCreated = postCreated;
    }

    public int getFollowingThisCreator() {
        return followingThisCreator;
    }

    public void setFollowingThisCreator(int followingThisCreator) {
        this.followingThisCreator = followingThisCreator;
    }

    @Nullable
    public String getPostText() {
        return postText;
    }

    public void setPostText(@Nullable String postText) {
        this.postText = postText;
    }
}
