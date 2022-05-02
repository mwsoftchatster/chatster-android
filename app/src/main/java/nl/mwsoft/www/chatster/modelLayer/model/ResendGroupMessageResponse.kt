package nl.mwsoft.www.chatster.modelLayer.model

class ResendGroupMessageResponse {

    var uuid: String? = null
    var response: String? = null
    var groupChatId: String? = null

    constructor() {}

    constructor(response: String?) {
        this.response = response
    }

    constructor(uuid: String?, response: String?, groupChatId: String?) {
        this.uuid = uuid
        this.response = response
        this.groupChatId = groupChatId
    }

}