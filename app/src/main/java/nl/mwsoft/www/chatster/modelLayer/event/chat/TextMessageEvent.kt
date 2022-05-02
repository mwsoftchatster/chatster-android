package nl.mwsoft.www.chatster.modelLayer.event.chat

import nl.mwsoft.www.chatster.modelLayer.model.Message
import java.util.*

class TextMessageEvent {
    var messages: ArrayList<Message> = ArrayList()

    constructor() {}

    constructor(messages: ArrayList<Message>) {
        this.messages = messages
    }

}