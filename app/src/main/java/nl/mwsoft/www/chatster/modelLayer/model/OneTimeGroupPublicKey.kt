package nl.mwsoft.www.chatster.modelLayer.model

import android.os.Parcel
import android.os.Parcelable

class OneTimeGroupPublicKey : Parcelable {

    var userId: Long = 0
    var groupChatId: String? = null
    var oneTimeGroupPublicKey: ByteArray? = null
    var uuid: String? = null
    var status: String? = "success"

    constructor() {}

    constructor(status: String) {
        this.status = status
    }

    constructor(userId: Long, groupChatId: String?, oneTimeGroupPublicKey: ByteArray, uuid: String?) {
        this.userId = userId
        this.groupChatId = groupChatId
        this.oneTimeGroupPublicKey = oneTimeGroupPublicKey
        this.uuid = uuid
    }

    protected constructor(`in`: Parcel) {
        userId = `in`.readLong()
        groupChatId = `in`.readString()
        oneTimeGroupPublicKey = `in`.createByteArray()
        uuid = `in`.readString()
        status = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(userId)
        dest.writeString(groupChatId)
        dest.writeByteArray(oneTimeGroupPublicKey)
        dest.writeString(uuid)
        dest.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OneTimeGroupPublicKey> {
        override fun createFromParcel(parcel: Parcel): OneTimeGroupPublicKey {
            return OneTimeGroupPublicKey(parcel)
        }

        override fun newArray(size: Int): Array<OneTimeGroupPublicKey?> {
            return arrayOfNulls(size)
        }
    }
}