package nl.mwsoft.www.chatster.modelLayer.model

class ReceivedOnlineMessage {

    private var _id = 0
    var uuid: String? = null
    var chatName: String? = null

    constructor() {}

    constructor(uuid: String?, chatName: String?) {
        this.uuid = uuid
        this.chatName = chatName
    }

    constructor(_id: Int, uuid: String?, chatName: String?) {
        this._id = _id
        this.uuid = uuid
        this.chatName = chatName
    }

    fun get_id(): Int {
        return _id
    }

    fun set_id(_id: Int) {
        this._id = _id
    }

}