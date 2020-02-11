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
package nl.mwsoft.www.chatster.modelLayer.helper.util.uuid;


import java.util.UUID;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;

public class ChatsterUUIDUtil {

    public ChatsterUUIDUtil() {
    }

    public String createUUID() {
        return UUID.randomUUID().toString().replace(ConstantRegistry.CHATSTER_DASH,ConstantRegistry.CHATSTER_EMPTY_STRING);
    }
}
