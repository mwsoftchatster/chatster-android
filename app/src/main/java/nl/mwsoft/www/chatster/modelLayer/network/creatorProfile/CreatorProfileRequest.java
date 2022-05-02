package nl.mwsoft.www.chatster.modelLayer.network.creatorProfile;



import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CreatorProfileRequest {
    @POST("/creatorContactProfile")
    Call<CreatorContact> getCreatorContactProfile(@Query("creatorName") String creatorName,
                                                  @Query("userId") long userId);
}
