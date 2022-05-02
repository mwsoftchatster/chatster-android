package nl.mwsoft.www.chatster.modelLayer.model

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry
import java.util.*

class RegisterUserResponse {

    private var _id: Long = 0
    var name: String? = null
    var profilePic: String? = null
    var statusMessage: String? = null
    var chatsterContacts: ArrayList<Long>? = null
    var isUserAlreadyExists = false
    @JvmField
    var status = ConstantRegistry.SUCCESS

    constructor() {}

    constructor(status: String) {
        this.status = status
    }

    constructor(_id: Long, name: String?, profilePic: String?, statusMessage: String?, chatsterContacts: ArrayList<Long>?, userAlreadyExists: Boolean) {
        this._id = _id
        this.name = name
        this.profilePic = profilePic
        this.statusMessage = statusMessage
        this.chatsterContacts = chatsterContacts
        isUserAlreadyExists = userAlreadyExists
    }

    fun get_id(): Long {
        return _id
    }

    fun set_id(_id: Long) {
        this._id = _id
    }

}