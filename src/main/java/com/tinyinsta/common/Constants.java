package com.tinyinsta.common;

import java.util.Map;

/**
 * Contains the client IDs and scopes for allowed clients consuming the helloworld API.
 */
public class Constants {
  public static final String WEB_CLIENT_ID =  "238318403831-upps8pc0cjj0n1nt33r7tl7kfqr3uko1.apps.googleusercontent.com";
  public static final String CLOUD_STORAGE_PROJECT_ID =  "TinyGram-2022-2023";
  public static final String CLOUD_STORAGE_BUCKET_NAME =  "tinygram-2022-2023.appspot.com";

  public static final int MAX_BUCKETS_NUMBER = 5;
  public static final int MAX_BATCH_SIZE = 50; //39_000;
  public static final int PAGINATION_SIZE = 5;
  public static final int TIMELINE_BUCKETS = 5; // from 0 to 4 so 5 buckets

  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
  public static final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
}
