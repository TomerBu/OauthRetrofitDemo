package tomerbu.edu.oauthretrofitdemo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginService {
    //The order of the annotations matters!!!

    /**
     * POST /o/oauth2/token HTTP/1.1
     * Host: www.googleapis.com
     * Content-Type: application/x-www-form-urlencoded
     * <p/>
     * code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7&
     * client_id=8819981768.apps.googleusercontent.com&
     * client_secret={client_secret}&
     * redirect_uri=https://oauth2-login-demo.appspot.com/code&
     * grant_type=authorization_code
     **/
    @POST("token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(@Field("client_id") String clientId,
                                     @Field("client_secret") String clientSecret,
                                     @Field("code") String code,
                                     @Field("grant_type") String grantType,
                                     @Field("redirect_uri") String redirectUri);


    /**
     * POST /o/oauth2/token HTTP/1.1
     * Host: accounts.google.com
     * Content-Type: application/x-www-form-urlencoded
     * <p/>
     * client_id=21302922996.apps.googleusercontent.com&
     * client_secret=XTHhXh1SlUNgvyWGwDk1EjXB&
     * refresh_token=1/HKSmLFXzqP0leUihZp2xUt3-5wkU7Gmu2Os_eBnzw74
     * grant_type=refresh_token
     **/

    @POST("token")
    @FormUrlEncoded
    Call<AccessToken> refreshAccessToken(@Field("client_id") String clientId,
                                         @Field("client_secret") String clientSecret,
                                         @Field("refresh_token") String refreshToken,
                                         @Field("grant_type") String grantType);

    /**
     https://www.googleapis.com/oauth2/v1/userinfo?alt=json
     usage: get https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=<your token>
     **/
    @GET("https://www.googleapis.com/oauth2/v1/userinfo")
    Call<GoogleProfileResponse> getUserProfile(@Query("access_token") String token);



}