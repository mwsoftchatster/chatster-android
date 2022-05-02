package nl.mwsoft.www.chatster.modelLayer.event.creators

import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost
import java.util.*

class LoadMorePostsEvent {
    var creatorPosts: ArrayList<CreatorPost>? = null

    constructor() {}

    constructor(creatorPosts: ArrayList<CreatorPost>?) {
        this.creatorPosts = creatorPosts
    }

}