package nl.mwsoft.www.chatster.modelLayer.model

class GroupMessageQueueItem {

    private var _id = 0
    var messageUUID: String? = null
    var groupChatId: String? = null

    constructor() {}

    constructor(_id: Int, messageUUID: String?, groupChatId: String?) {
        this._id = _id
        this.messageUUID = messageUUID
        this.groupChatId = groupChatId
    }

    constructor(messageUUID: String?, groupChatId: String?) {
        this.messageUUID = messageUUID
        this.groupChatId = groupChatId
    }

    fun get_id(): Int {
        return _id
    }

    fun set_id(_id: Int) {
        this._id = _id
    }

}