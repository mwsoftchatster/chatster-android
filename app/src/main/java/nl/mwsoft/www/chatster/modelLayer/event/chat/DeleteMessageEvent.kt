package nl.mwsoft.www.chatster.modelLayer.event.chat


import java.util.ArrayList

import nl.mwsoft.www.chatster.modelLayer.model.Message

class DeleteMessageEvent {

    var messages = ArrayList<Message>()

    constructor() {}

    constructor(messages: ArrayList<Message>) {
        this.messages = messages
    }
}
