package nl.mwsoft.www.chatster.modelLayer.model

class SearchResultUser {

    var name: String? = null
    var profilePicUrl: String? = null
    var statusMessage: String? = null

    constructor() {}

    constructor(name: String?, profilePicUrl: String?, statusMessage: String?) {
        this.name = name
        this.profilePicUrl = profilePicUrl
        this.statusMessage = statusMessage
    }

}