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