package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

public class CreatorsConnectSuggestion implements Parcelable{

    private String creatorName;
    private String creatorProfilePicUrl;
    private String creatorLastPostUrl;

    public CreatorsConnectSuggestion() {
    }

    public CreatorsConnectSuggestion(String creatorName, String creatorProfilePicUrl, String creatorLastPostUrl) {
        this.creatorName = creatorName;
        this.creatorProfilePicUrl = creatorProfilePicUrl;
        this.creatorLastPostUrl = creatorLastPostUrl;
    }

    protected CreatorsConnectSuggestion(Parcel in) {
        creatorName = in.readString();
        creatorProfilePicUrl = in.readString();
        creatorLastPostUrl = in.readString();
    }

    public static final Creator<CreatorsConnectSuggestion> CREATOR = new Creator<CreatorsConnectSuggestion>() {
        @Override
        public CreatorsConnectSuggestion createFromParcel(Parcel in) {
            return new CreatorsConnectSuggestion(in);
        }

        @Override
        public CreatorsConnectSuggestion[] newArray(int size) {
            return new CreatorsConnectSuggestion[size];
        }
    };

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorProfilePicUrl() {
        return creatorProfilePicUrl;
    }

    public void setCreatorProfilePicUrl(String creatorProfilePicUrl) {
        this.creatorProfilePicUrl = creatorProfilePicUrl;
    }

    public String getCreatorLastPostUrl() {
        return creatorLastPostUrl;
    }

    public void setCreatorLastPostUrl(String creatorLastPostUrl) {
        this.creatorLastPostUrl = creatorLastPostUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creatorName);
        dest.writeString(creatorProfilePicUrl);
        dest.writeString(creatorLastPostUrl);
    }
}
