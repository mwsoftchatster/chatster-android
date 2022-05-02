package nl.mwsoft.www.chatster.modelLayer.event.creators

import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact
import java.util.*

class SearchCreatorsEvent {
    var creatorContacts: ArrayList<CreatorContact>? = null

    constructor() {}

    constructor(creatorContacts: ArrayList<CreatorContact>?) {
        this.creatorContacts = creatorContacts
    }

}