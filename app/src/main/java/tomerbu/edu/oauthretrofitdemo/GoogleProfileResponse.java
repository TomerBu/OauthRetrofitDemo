package tomerbu.edu.oauthretrofitdemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleProfileResponse {

    @SerializedName("family_name")
    @Expose
    private String familyName;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("given_name")
    @Expose
    private String givenName;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("picture")
    @Expose
    private String picture;

    /**
     * @return The familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName The family_name
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return The givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName The given_name
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * @param picture The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

}