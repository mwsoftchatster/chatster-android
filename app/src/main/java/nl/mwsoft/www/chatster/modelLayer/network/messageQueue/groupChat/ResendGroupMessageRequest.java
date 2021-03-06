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
package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.groupChat;


import nl.mwsoft.www.chatster.modelLayer.model.ResendGroupMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ResendGroupMessageRequest {

    @POST("/resendGroupChatMessage")
    Call<ResendGroupMessageResponse> resendGroupChatMessage(
            @Query("messages") String messages);

    @Multipart
    @POST("/resendGroupChatMessage")
    Call<ResendGroupMessageResponse> resendGroupChatImageMessage(
            @Part MultipartBody.Part image,
            @Query("senderId") long senderId,
            @Query("uuid") String uuid,
            @Query("groupChatId") String groupChatId,
            @Query("contentType") String contentType);
}
