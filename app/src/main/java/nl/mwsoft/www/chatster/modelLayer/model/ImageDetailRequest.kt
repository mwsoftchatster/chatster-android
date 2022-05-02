package nl.mwsoft.www.chatster.modelLayer.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class ImageDetailRequest : Parcelable {

    var isGroupChat: Boolean = false
    var chatId: Int = 0
    var groupChatId: String? = null
    var imageUri: Uri? = null
    var senderName: String? = null

    constructor() {}

    constructor(isGroupChat: Boolean, chatId: Int, groupChatId: String?, imageUri: Uri?, senderName: String?) {
        this.isGroupChat = isGroupChat
        this.chatId = chatId
        this.groupChatId = groupChatId
        this.imageUri = imageUri
        this.senderName = senderName
    }

    protected constructor(`in`: Parcel) {
        isGroupChat = `in`.readByte().toInt() != 0
        chatId = `in`.readInt()
        groupChatId = `in`.readString()
        imageUri = `in`.readParcelable(Uri::class.java.classLoader)
        senderName = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte((if (isGroupChat) 1 else 0).toByte())
        dest.writeInt(chatId)
        dest.writeString(groupChatId)
        dest.writeParcelable(imageUri, flags)
        dest.writeString(senderName)
    }

    companion object CREATOR : Parcelable.Creator<ImageDetailRequest> {
        override fun createFromParcel(parcel: Parcel): ImageDetailRequest {
            return ImageDetailRequest(parcel)
        }

        override fun newArray(size: Int): Array<ImageDetailRequest?> {
            return arrayOfNulls(size)
        }
    }
}