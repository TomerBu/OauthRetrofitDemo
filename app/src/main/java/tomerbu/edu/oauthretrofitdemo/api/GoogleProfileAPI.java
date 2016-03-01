package tomerbu.edu.oauthretrofitdemo.api;

import android.net.Uri;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tomerbu.edu.oauthretrofitdemo.api.models.AccessTokenResponse;
import tomerbu.edu.oauthretrofitdemo.api.models.GoogleProfileResponse;

/**
 * Created by TomerBu.
 */
public class GoogleProfileAPI {
    // you should either define client id and secret as constants or in string resources
    private final String clientId = "387414573620-a5dbc02u4af3be0mrv5ll5ki94iaq317.apps.googleusercontent.com";
    private final String clientSecret = "l3FHjuvppIAsdNN6FCZ-0fiw";
    private final String redirectUri = "http://localhost";
    private final String scope = "https://www.googleapis.com/auth/plus.me";
    private final String grantType = "authorization_code";

    public static final String BASE_URL = "https://accounts.google.com/o/oauth2/";
    private AccessTokenResponse token;
    private LoginService mGoogleProfileService;


    /**
     * Make a call to get a token using the codeUri.
     * Run the call to get an access token.
     * Prepare another call to refresh the token using our credentials.
     * Send the Prepared call and token to be used by the interceptors of OKHttp Client
     * The prepared Call will be used in case the token needs to be refreshed. (OKHttp Authenticator)
     * The token will be sent with all our outgoing api calls using the interceptors.
     * Notify the listeners that the API is ready for use.
     *
     * @param codeUri  the uri from the web authentication intent
     * @param listener get's notified when the api is ready for calls.
     */
    public GoogleProfileAPI(Uri codeUri, final OnAPIReady listener) {
        //get the token from WebBrowser
        if (codeUri != null && codeUri.toString().startsWith(redirectUri)) {
            String code = codeUri.getQueryParameter("code");
            if (code != null) {
                //Make a simple call (no interceptors / Authenticator)
                //get token call (only done once)
                final LoginService loginService = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build().create(LoginService.class);
                Call<AccessTokenResponse> accessTokenCall = loginService.
                        getAccessToken(clientId, clientSecret, code, grantType, redirectUri);

                //run the getToken call
                accessTokenCall.enqueue(new Callback<AccessTokenResponse>() {
                    @Override
                    public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                        if (response != null) {
                            GoogleProfileAPI.this.token = response.body();
                            //Prepare a refresh Token call for the authenticator to use.
                            Call<AccessTokenResponse> accessTokenCall = loginService.refreshAccessToken(clientId, clientSecret, token.getRefreshToken(), grantType, redirectUri);
                            //pass the refresh Token call to be used by OKHttpClient from now on.
                            OkHttpClient client = OKHttpClientGenerator.getSharedInstance(token, accessTokenCall).getClient();
                            // Create an instance of our API interface.
                            mGoogleProfileService = new Retrofit.Builder().
                                    baseUrl(BASE_URL).
                                    addConverterFactory(GsonConverterFactory.create()).
                                    client(client).
                                    build().create(LoginService.class);

                            //add a listener to let them know the api is ready, once the interceptors are in place.
                            listener.onApiReady(token);
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                        //TODO: Handle this error
                        t.printStackTrace();
                    }
                });
            } else {
                //TODO: Handle this error
                String error = codeUri.getQueryParameter("error");
                if (error != null)
                    System.out.println(error);
            }
        }

    }


    /**
     * Make a call to get a token using the codeUri.
     * Run the call to get an access token.
     * Prepare another call to refresh the token using our credentials.
     * Send the Prepared call and token to be used by the interceptors of OKHttp Client
     * The prepared Call will be used in case the token needs to be refreshed. (OKHttp Authenticator)
     * The token will be sent with all our outgoing api calls using the interceptors.
     * Notify the listeners that the API is ready for use.
     *
     * @param token    the token saved from earlier usage
     * @param listener get's notified when the api is ready for calls.
     */
    public GoogleProfileAPI(final AccessTokenResponse token, final OnAPIReady listener) {
        //get the token from the user
        //Make a simple call (no interceptors / Authenticator)
        //get token call (only done once)
        final LoginService loginService = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build().create(LoginService.class);
        Call<AccessTokenResponse> accessTokenCall = loginService.refreshAccessToken(clientId, clientSecret, token.getRefreshToken(), grantType, redirectUri);

        //run the getToken call
        accessTokenCall.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response != null) {
                    GoogleProfileAPI.this.token = response.body();
                    //Prepare a refresh Token call for the authenticator to use.
                    Call<AccessTokenResponse> accessTokenCall = loginService.refreshAccessToken(clientId, clientSecret, token.getRefreshToken(), grantType, redirectUri);
                    //pass the refresh Token call to be used by OKHttpClient from now on.
                    OkHttpClient client = OKHttpClientGenerator.getSharedInstance(token, accessTokenCall).getClient();
                    // Create an instance of our API interface.
                    mGoogleProfileService = new Retrofit.Builder().
                            baseUrl(BASE_URL).
                            addConverterFactory(GsonConverterFactory.create()).
                            client(client).
                            build().create(LoginService.class);

                    //add a listener to let them know the api is ready, once the interceptors are in place.
                    listener.onApiReady(token);
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                //TODO: Handle this error
                t.printStackTrace();
            }
        });
    }

    public interface OnAPIReady {
        void onApiReady(AccessTokenResponse accessTokenResponse);
    }

    public void getProfile() {
        Call<GoogleProfileResponse> userProfile = mGoogleProfileService.getUserProfile();
        userProfile.enqueue(new Callback<GoogleProfileResponse>() {
            @Override
            public void onResponse(Call<GoogleProfileResponse> call, Response<GoogleProfileResponse> response) {
                if (response != null)
                    Log.d("TomerBu", response.body().toString());
            }

            @Override
            public void onFailure(Call<GoogleProfileResponse> call, Throwable t) {
                t.printStackTrace();
                //TODO: Handle this error
            }
        });
    }

    public interface LoginService {

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
        //The order of the annotations matters!!!
        @POST("token")
        @FormUrlEncoded
        Call<AccessTokenResponse> getAccessToken(@Field("client_id") String clientId,
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
        Call<AccessTokenResponse> refreshAccessToken(@Field("client_id") String clientId,
                                                     @Field("client_secret") String clientSecret,
                                                     @Field("refresh_token") String refreshToken,
                                                     @Field("grant_type") String grantType,
                                                     @Field("redirect_uri") String redirectUri);

        /**
         * https://www.googleapis.com/oauth2/v1/userinfo?alt=json
         * usage: get https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=<your token>
         **/
        @GET("https://www.googleapis.com/oauth2/v1/userinfo")
        Call<GoogleProfileResponse> getUserProfile(@Query("access_token") String token);


        /**
         * https://www.googleapis.com/oauth2/v1/userinfo?alt=json
         * usage: get https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=<your token>
         **/
        @GET("https://www.googleapis.com/oauth2/v1/userinfo")
        Call<GoogleProfileResponse> getUserProfile(/*rely on interceptors*/);


    }
}
