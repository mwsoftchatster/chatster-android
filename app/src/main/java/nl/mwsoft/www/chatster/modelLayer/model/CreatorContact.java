package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CreatorContact implements Parcelable {

    private String creatorId;
    private String statusMessage;
    private String profilePic;
    private long posts;
    private long creatorFollowers;
    private long creatorFollowing;
    private long creatorProfileViews;
    private long creatorTotalLikes;
    private String website;
    private int followingThisCreator;
    private ArrayList<CreatorPost> creatorPosts;
    public String response = "success";

    public CreatorContact() {
    }

    public CreatorContact(String response) {
        this.response = response;
    }

    public CreatorContact(String creatorId, String statusMessage, String profilePic, long posts, long creatorFollowers, long creatorFollowing, long creatorProfileViews, long creatorTotalLikes, String website, int followingThisCreator, ArrayList<CreatorPost> creatorPosts, String response) {
        this.creatorId = creatorId;
        this.statusMessage = statusMessage;
        this.profilePic = profilePic;
        this.posts = posts;
        this.creatorFollowers = creatorFollowers;
        this.creatorFollowing = creatorFollowing;
        this.creatorProfileViews = creatorProfileViews;
        this.creatorTotalLikes = creatorTotalLikes;
        this.website = website;
        this.followingThisCreator = followingThisCreator;
        this.creatorPosts = creatorPosts;
        this.response = response;
    }

    protected CreatorContact(Parcel in) {
        creatorId = in.readString();
        statusMessage = in.readString();
        profilePic = in.readString();
        posts = in.readLong();
        creatorFollowers = in.readLong();
        creatorFollowing = in.readLong();
        creatorProfileViews = in.readLong();
        creatorTotalLikes = in.readLong();
        website = in.readString();
        followingThisCreator = in.readInt();
        creatorPosts = in.createTypedArrayList(CreatorPost.CREATOR);
        response = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creatorId);
        dest.writeString(statusMessage);
        dest.writeString(profilePic);
        dest.writeLong(posts);
        dest.writeLong(creatorFollowers);
        dest.writeLong(creatorFollowing);
        dest.writeLong(creatorProfileViews);
        dest.writeLong(creatorTotalLikes);
        dest.writeString(website);
        dest.writeInt(followingThisCreator);
        dest.writeTypedList(creatorPosts);
        dest.writeString(response);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreatorContact> CREATOR = new Creator<CreatorContact>() {
        @Override
        public CreatorContact createFromParcel(Parcel in) {
            return new CreatorContact(in);
        }

        @Override
        public CreatorContact[] newArray(int size) {
            return new CreatorContact[size];
        }
    };

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public long getCreatorFollowers() {
        return creatorFollowers;
    }

    public void setCreatorFollowers(long creatorFollowers) {
        this.creatorFollowers = creatorFollowers;
    }

    public long getCreatorFollowing() {
        return creatorFollowing;
    }

    public void setCreatorFollowing(long creatorFollowing) {
        this.creatorFollowing = creatorFollowing;
    }

    public long getCreatorProfileViews() {
        return creatorProfileViews;
    }

    public void setCreatorProfileViews(long creatorProfileViews) {
        this.creatorProfileViews = creatorProfileViews;
    }

    public long getCreatorTotalLikes() {
        return creatorTotalLikes;
    }

    public void setCreatorTotalLikes(long creatorTotalLikes) {
        this.creatorTotalLikes = creatorTotalLikes;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getFollowingThisCreator() {
        return followingThisCreator;
    }

    public void setFollowingThisCreator(int followingThisCreator) {
        this.followingThisCreator = followingThisCreator;
    }

    public ArrayList<CreatorPost> getCreatorPosts() {
        return creatorPosts;
    }

    public void setCreatorPosts(ArrayList<CreatorPost> creatorPosts) {
        this.creatorPosts = creatorPosts;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
