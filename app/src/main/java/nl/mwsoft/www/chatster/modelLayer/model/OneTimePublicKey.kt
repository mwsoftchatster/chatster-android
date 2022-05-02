package nl.mwsoft.www.chatster.modelLayer.model

import android.os.Parcel
import android.os.Parcelable

class OneTimePublicKey : Parcelable {

    var userId: Long = 0
    var oneTimePublicKey: ByteArray? = null
    var uuid: String? = null
    var status: String? = "success"

    constructor(status: String) {
        this.status = status
    }

    constructor() {}

    constructor(userId: Long, oneTimePublicKey: ByteArray, uuid: String?, status: String) {
        this.userId = userId
        this.oneTimePublicKey = oneTimePublicKey
        this.uuid = uuid
        this.status = status
    }

    protected constructor(`in`: Parcel) {
        userId = `in`.readLong()
        oneTimePublicKey = `in`.createByteArray()
        uuid = `in`.readString()
        status = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(userId)
        dest.writeByteArray(oneTimePublicKey)
        dest.writeString(uuid)
        dest.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OneTimePublicKey> {
        override fun createFromParcel(parcel: Parcel): OneTimePublicKey {
            return OneTimePublicKey(parcel)
        }

        override fun newArray(size: Int): Array<OneTimePublicKey?> {
            return arrayOfNulls(size)
        }
    }
}