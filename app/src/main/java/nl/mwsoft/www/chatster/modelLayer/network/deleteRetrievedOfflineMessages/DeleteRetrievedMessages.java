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
package nl.mwsoft.www.chatster.modelLayer.network.deleteRetrievedOfflineMessages;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;



public interface DeleteRetrievedMessages {
    @POST("/deleteRetrievedMessages")
    Call<ArrayList<DeleteRetrievedMessagesResponse>> deleteRetrievedMessages(@Query("uuids") String[] uuids,
                                                                             @Query("dstId") long userId);

    @POST("/deleteRetrievedGroupMessages")
    Call<ArrayList<DeleteRetrievedMessagesResponse>> deleteRetrievedGroupMessages(@Query("uuids") String[] uuids,
                                                                             @Query("dstId") long userId);
}


