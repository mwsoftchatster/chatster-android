package nl.mwsoft.www.chatster.modelLayer.event.groupChat

class UnsendGroupChatMessageEvent {
    var messageText: String? = null
    var messagePosition = 0

    constructor() {}

    constructor(messageText: String?, messagePosition: Int) {
        this.messageText = messageText
        this.messagePosition = messagePosition
    }
}