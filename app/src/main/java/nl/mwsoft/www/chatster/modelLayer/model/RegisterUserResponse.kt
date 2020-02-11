/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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