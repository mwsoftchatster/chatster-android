package nl.mwsoft.www.chatster.modelLayer.model

class ExitGroupChatReq {

    var groupChatId: String? = null
    var userId: Long = 0

    constructor() {}

    constructor(groupChatId: String?, userId: Long) {
        this.groupChatId = groupChatId
        this.userId = userId
    }

}