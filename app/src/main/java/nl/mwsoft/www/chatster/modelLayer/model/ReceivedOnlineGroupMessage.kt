package nl.mwsoft.www.chatster.modelLayer.model

class ReceivedOnlineGroupMessage {

    private var _id = 0
    var uuid: String? = null

    constructor() {}

    constructor(uuid: String?) {
        this.uuid = uuid
    }

    constructor(_id: Int, uuid: String?) {
        this._id = _id
        this.uuid = uuid
    }

    fun get_id(): Int {
        return _id
    }

    fun set_id(_id: Int) {
        this._id = _id
    }

}