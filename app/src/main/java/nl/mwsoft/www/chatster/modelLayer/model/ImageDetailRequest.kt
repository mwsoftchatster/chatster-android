/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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