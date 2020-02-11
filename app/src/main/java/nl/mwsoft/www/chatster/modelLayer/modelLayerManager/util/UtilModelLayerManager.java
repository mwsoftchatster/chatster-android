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
package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util;



import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.internet.InternetConnectionUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.uuid.ChatsterUUIDUtil;

public class UtilModelLayerManager {

    private ChatsterUUIDUtil chatsterUUIDUtil;
    private InternetConnectionUtil internetConnectionUtil;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;


    // region Constructors

    public UtilModelLayerManager() {
    }

    public UtilModelLayerManager(ChatsterUUIDUtil chatsterUUIDUtil, InternetConnectionUtil internetConnectionUtil, ChatsterDateTimeUtil chatsterDateTimeUtil) {
        this.chatsterUUIDUtil = chatsterUUIDUtil;
        this.internetConnectionUtil = internetConnectionUtil;
        this.chatsterDateTimeUtil = chatsterDateTimeUtil;
    }

    public UtilModelLayerManager(ChatsterDateTimeUtil chatsterDateTimeUtil) {
        this.chatsterDateTimeUtil = chatsterDateTimeUtil;
    }

    public UtilModelLayerManager(ChatsterUUIDUtil chatsterUUIDUtil) {
        this.chatsterUUIDUtil = chatsterUUIDUtil;
    }

    public UtilModelLayerManager(InternetConnectionUtil internetConnectionUtil) {
        this.internetConnectionUtil = internetConnectionUtil;
    }

    public UtilModelLayerManager(ChatsterUUIDUtil chatsterUUIDUtil, InternetConnectionUtil internetConnectionUtil) {
        this.chatsterUUIDUtil = chatsterUUIDUtil;
        this.internetConnectionUtil = internetConnectionUtil;
    }

    // endregion

    // region UUID

    public String createUUID() {
        return chatsterUUIDUtil.createUUID();
    }

    // endregion

    // region DateTime

    public String getDateTime(){
        return chatsterDateTimeUtil.getDateTime();
    }
    public String convertFromUtcToLocal(String utc){
        return chatsterDateTimeUtil.convertFromUtcToLocal(utc);
    }

    // endregion

    // region Internet

    public boolean hasInternetConnection() {
        return internetConnectionUtil.hasInternetConnection();
    }

    // endregion


}
