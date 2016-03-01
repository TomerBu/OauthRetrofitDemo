package tomerbu.edu.oauthretrofitdemo.api;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import tomerbu.edu.oauthretrofitdemo.api.models.AccessTokenResponse;

public class OKHttpClientGenerator {

    private static OKHttpClientGenerator sharedInstance;
    private AccessTokenResponse token = null;
    private final Call<AccessTokenResponse> accessTokenCall;

    private OKHttpClientGenerator(AccessTokenResponse token, Call<AccessTokenResponse> accessTokenCall) {
        this.token = token;
        this.accessTokenCall = accessTokenCall;
    }

    //getters
    public static synchronized OKHttpClientGenerator getSharedInstance(AccessTokenResponse token, Call<AccessTokenResponse> accessTokenCall) {
        if (sharedInstance == null)
            sharedInstance = new OKHttpClientGenerator(token, accessTokenCall);
        return sharedInstance;
    }

    //public API
    public OkHttpClient getClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(getHttpLoggingInterceptor())
                .addInterceptor(addTokenInterceptor())
                .authenticator(LoginAuthenticator())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * If a request was unauthorized (OKHttp client tests the headers)
     * The authenticator performs a login and updates the token
     * once it updates the token, the request is dispatched again with the token in the header
     * Also, the token is saved in the token field so in the next requests we don't hit the login api node again
     *
     * @return OKHttp Authenticator
     */
    private Authenticator LoginAuthenticator() {
        return new Authenticator() {
            public AccessTokenResponse token;

            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                // give up after 3 failures. do this exponentially slower is better.
                // we are in a background thread and we can block it as we like...
                if (responseCount(response) >= 3) {
                    return null; // If we've failed 3 times, give up.
                }

                //Make a blocking call to get a token, this is done on a background thread already.


                retrofit2.Response<AccessTokenResponse> accessTokenResponse = accessTokenCall.clone().execute();
                this.token = accessTokenResponse.body();


//                AccessTokenResponse accessToken = api.getTokenBlocking();
//                token = accessToken.getToken();


                //run the original request with the token from the authorization node.
                return response.request();//.newBuilder().header("Authorization", token.getAccessToken()).build();
            }
        };
    }

    /**
     * A simple method that counts response.priorResponses.
     * //each time a request is retried, the response.priorResponse stack grows.
     * here we count the respnse.priorResponses
     */
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    /**
     * A request interceptor that adds the token to every request.
     *
     * @return an OKHttp3 Interceptor to be used by the httpClient
     */
    private Interceptor addTokenInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (token == null)
                    return chain.proceed(chain.request());
//                Request original = chain.request();

//
//                // Request URL customization: add url parameters:
//                HttpUrl modifiedUrl = original.url().newBuilder()
//                        //Provide your custom parameter here
//                        //.addQueryParameter("token", token)
//                        .build();
//
//                // Request customization: change the request method:
//                String method = original.method();
//
//                // Request customization: add request headers:
//                Request.Builder requestBuilder = original.newBuilder()
//                        .header("Accept", "application/json")
//                        .header("Content-Type", "application/json")
//                        .header("Authorization", token)  // this is the important line
//                        .url(modifiedUrl)
//                        .method(method, original.body());
//
//                Request request = requestBuilder.build();
//                // Response customization: Get a new token:
//                Response response = chain.proceed(request);
//
//                if (response.code() == 401) {
//                    logThisIssue();
//                }
//                return response;
                //add the token to the request parameters
                if (chain.request().method().equals("GET")) {
                    Request original = chain.request();

                    // Request URL customization: add url parameters:
                    HttpUrl modifiedUrl = original.url().newBuilder()
                            //Provide your custom parameter here
                            .addQueryParameter("access_token", token.getAccessToken())
                            .build();

                    return chain.proceed(original.newBuilder().url(modifiedUrl).build());

                    //return chain.proceed(chain.request().url().newBuilder().addQueryParameter("token", token.getAccessToken()).build())
                }
                return chain.proceed(chain.request().newBuilder().header("Authorization", token.getAccessToken()).build());
            }
        };
    }

    /**
     * inValuable debugging aid for OKHttp3 work
     *
     * @return an HttpLoggingInterceptor to log all the requests
     */
    @NonNull
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}
