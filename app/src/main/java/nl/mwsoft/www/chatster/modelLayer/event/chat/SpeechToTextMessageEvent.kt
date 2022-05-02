package nl.mwsoft.www.chatster.modelLayer.event.chat

class SpeechToTextMessageEvent {
    var message: String? = null

    constructor() {}

    constructor(message: String?) {
        this.message = message
    }
}