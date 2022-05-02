package nl.mwsoft.www.chatster.modelLayer.model

import android.os.Parcel
import android.os.Parcelable

class HistoryItem : Parcelable {

    var userProfilePic: String? = null
    var userName: String? = null
    var type: String? = null
    var description: String? = null
    var created: String? = null
    var postUUID: String? = null
    var postUrl: String? = null

    constructor() {}

    constructor(userProfilePic: String?, userName: String?, type: String?, description: String?, created: String?, postUUID: String?, postUrl: String?) {
        this.userProfilePic = userProfilePic
        this.userName = userName
        this.type = type
        this.description = description
        this.created = created
        this.postUUID = postUUID
        this.postUrl = postUrl
    }

    protected constructor(`in`: Parcel) {
        userProfilePic = `in`.readString()
        userName = `in`.readString()
        type = `in`.readString()
        description = `in`.readString()
        created = `in`.readString()
        postUUID = `in`.readString()
        postUrl = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(userProfilePic)
        dest.writeString(userName)
        dest.writeString(type)
        dest.writeString(description)
        dest.writeString(created)
        dest.writeString(postUUID)
        dest.writeString(postUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HistoryItem> {
        override fun createFromParcel(parcel: Parcel): HistoryItem {
            return HistoryItem(parcel)
        }

        override fun newArray(size: Int): Array<HistoryItem?> {
            return arrayOfNulls(size)
        }
    }
}