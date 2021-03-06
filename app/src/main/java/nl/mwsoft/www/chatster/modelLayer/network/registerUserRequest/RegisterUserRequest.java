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
package nl.mwsoft.www.chatster.modelLayer.network.registerUserRequest;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.RegisterUserResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterUserRequest {
    @POST("/createUser")
    Call<RegisterUserResponse> createUser(
            @Query("userId") long userId,
            @Query("userName") String userName,
            @Query("statusMessage") String statusMessage,
            @Query("messagingToken") String messagingToken,
            @Query("contacts[]") ArrayList<Long> contacts);
}
