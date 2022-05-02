package nl.mwsoft.www.chatster.modelLayer.model

import java.util.*

class GroupChatOfflineMessageRequest {

    var groupChatIds: ArrayList<Int>? = null
    var userId: Long = 0

    constructor() {}

    constructor(groupChatIds: ArrayList<Int>?, userId: Long) {
        this.groupChatIds = groupChatIds
        this.userId = userId
    }

}