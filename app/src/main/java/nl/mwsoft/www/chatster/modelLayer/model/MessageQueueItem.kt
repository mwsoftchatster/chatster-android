package nl.mwsoft.www.chatster.modelLayer.model

class MessageQueueItem {

    private var _id = 0
    var messageUUID: String? = null
    var receiverId: Long = 0
    var contactPublicKeyUUID: String? = null
    var userPublicKeyUUID: String? = null

    constructor() {}

    constructor(_id: Int, messageUUID: String?) {
        this._id = _id
        this.messageUUID = messageUUID
    }

    constructor(_id: Int, messageUUID: String?, receiverId: Long) {
        this._id = _id
        this.messageUUID = messageUUID
        this.receiverId = receiverId
    }

    constructor(_id: Int, messageUUID: String?, receiverId: Long, contactPublicKeyUUID: String?, userPublicKeyUUID: String?) {
        this._id = _id
        this.messageUUID = messageUUID
        this.receiverId = receiverId
        this.contactPublicKeyUUID = contactPublicKeyUUID
        this.userPublicKeyUUID = userPublicKeyUUID
    }

    fun get_id(): Int {
        return _id
    }

    fun set_id(_id: Int) {
        this._id = _id
    }

}