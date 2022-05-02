package nl.mwsoft.www.chatster.modelLayer.model

class ResendMessageResponse {

    var uuid: String? = null
    var response: String? = null
    var chatId: String? = null

    constructor() {}

    constructor(response: String?) {
        this.response = response
    }

    constructor(uuid: String?, response: String?, chatId: String?) {
        this.uuid = uuid
        this.response = response
        this.chatId = chatId
    }

}