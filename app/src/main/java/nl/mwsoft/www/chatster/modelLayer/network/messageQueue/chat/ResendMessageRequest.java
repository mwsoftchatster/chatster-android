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
package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.chat;



import nl.mwsoft.www.chatster.modelLayer.model.ResendMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ResendMessageRequest {
    @POST("/resendChatMessage")
    Call<ResendMessageResponse> resendMessage(
            @Query("message") String message,
            @Query("senderId") long senderId,
            @Query("senderName") String senderName,
            @Query("receiverId") long receiverId,
            @Query("chatName") String chatName,
            @Query("uuid") String uuid,
            @Query("contentType") String contentType);

    @Multipart
    @POST("/resendChatMessage")
    Call<ResendMessageResponse> resendImageMessage(
            @Part MultipartBody.Part image,
            @Query("senderId") long senderId,
            @Query("senderName") String senderName,
            @Query("receiverId") long receiverId,
            @Query("chatName") String chatName,
            @Query("uuid") String uuid,
            @Query("contentType") String contentType);
}
