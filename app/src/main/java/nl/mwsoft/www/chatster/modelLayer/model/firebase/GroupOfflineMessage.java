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
package nl.mwsoft.www.chatster.modelLayer.model.firebase;


import java.util.List;

public class GroupOfflineMessage {

    private List<String> receiver_ids;

    public GroupOfflineMessage() {
    }

    public GroupOfflineMessage(List<String> receiver_ids) {
        this.receiver_ids = receiver_ids;
    }

    public List<String> getReceiver_ids() {
        return receiver_ids;
    }

    public void setReceiver_ids(List<String> receiver_ids) {
        this.receiver_ids = receiver_ids;
    }
}
