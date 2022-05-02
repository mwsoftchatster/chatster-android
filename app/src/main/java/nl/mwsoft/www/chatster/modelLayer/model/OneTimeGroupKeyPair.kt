package nl.mwsoft.www.chatster.modelLayer.model

import android.os.Parcel
import android.os.Parcelable

class OneTimeGroupKeyPair : Parcelable {

    var userId: Long = 0
    var groupChatId: String? = null
    var oneTimeGroupPublicKey: ByteArray? = null
    var oneTimeGroupPrivateKey: ByteArray? = null
    var uuid: String? = null
    var status: String? = "success"

    constructor() {}

    constructor(status: String) {
        this.status = status
    }

    constructor(userId: Long, groupChatId: String?, oneTimeGroupPublicKey: ByteArray, oneTimeGroupPrivateKey: ByteArray, uuid: String?) {
        this.userId = userId
        this.groupChatId = groupChatId
        this.oneTimeGroupPublicKey = oneTimeGroupPublicKey
        this.oneTimeGroupPrivateKey = oneTimeGroupPrivateKey
        this.uuid = uuid
    }

    protected constructor(`in`: Parcel) {
        userId = `in`.readLong()
        groupChatId = `in`.readString()
        oneTimeGroupPublicKey = `in`.createByteArray()
        oneTimeGroupPrivateKey = `in`.createByteArray()
        uuid = `in`.readString()
        status = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(userId)
        dest.writeString(groupChatId)
        dest.writeByteArray(oneTimeGroupPublicKey)
        dest.writeByteArray(oneTimeGroupPrivateKey)
        dest.writeString(uuid)
        dest.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OneTimeGroupKeyPair> {
        override fun createFromParcel(parcel: Parcel): OneTimeGroupKeyPair {
            return OneTimeGroupKeyPair(parcel)
        }

        override fun newArray(size: Int): Array<OneTimeGroupKeyPair?> {
            return arrayOfNulls(size)
        }
    }
}