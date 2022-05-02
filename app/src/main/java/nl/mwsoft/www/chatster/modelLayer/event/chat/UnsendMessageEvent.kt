package nl.mwsoft.www.chatster.modelLayer.event.chat

class UnsendMessageEvent {
    var messageText: String? = null
    var messagePosition = 0

    constructor() {}

    constructor(messageText: String?, messagePosition: Int) {
        this.messageText = messageText
        this.messagePosition = messagePosition
    }
}