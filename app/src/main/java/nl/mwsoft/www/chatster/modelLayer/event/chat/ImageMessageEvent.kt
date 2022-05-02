package nl.mwsoft.www.chatster.modelLayer.event.chat

import nl.mwsoft.www.chatster.modelLayer.model.Message
import java.util.*

class ImageMessageEvent {
    var messages = ArrayList<Message>()

    constructor() {}
    constructor(messages: ArrayList<Message>) {
        this.messages = messages
    }

}