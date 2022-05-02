package nl.mwsoft.www.chatster.modelLayer.event.groupChat

import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage
import java.util.*

class TextGroupChatMessageEvent {
    var groupChatMessages = ArrayList<GroupChatMessage>()

    constructor() {}

    constructor(groupChatMessages: ArrayList<GroupChatMessage>) {
        this.groupChatMessages = groupChatMessages
    }

}