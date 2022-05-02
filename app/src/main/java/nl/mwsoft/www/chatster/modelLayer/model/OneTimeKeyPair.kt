package nl.mwsoft.www.chatster.modelLayer.model

import android.os.Parcel
import android.os.Parcelable

class OneTimeKeyPair : Parcelable {

    var userId: Long = 0
    var oneTimePublicKey: ByteArray? = null
    var oneTimePrivateKey: ByteArray? = null
    var uuid: String? = null
    var status: String? = "success"

    constructor() {}

    constructor(status: String) {
        this.status = status
    }

    constructor(userId: Long, oneTimePublicKey: ByteArray, oneTimePrivateKey: ByteArray, uuid: String?) {
        this.userId = userId
        this.oneTimePublicKey = oneTimePublicKey
        this.oneTimePrivateKey = oneTimePrivateKey
        this.uuid = uuid
    }

    protected constructor(`in`: Parcel) {
        userId = `in`.readLong()
        oneTimePublicKey = `in`.createByteArray()
        oneTimePrivateKey = `in`.createByteArray()
        uuid = `in`.readString()
        status = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(userId)
        dest.writeByteArray(oneTimePublicKey)
        dest.writeByteArray(oneTimePrivateKey)
        dest.writeString(uuid)
        dest.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OneTimeKeyPair> {
        override fun createFromParcel(parcel: Parcel): OneTimeKeyPair {
            return OneTimeKeyPair(parcel)
        }

        override fun newArray(size: Int): Array<OneTimeKeyPair?> {
            return arrayOfNulls(size)
        }
    }
}