package com.example.util;

import android.os.StrictMode;
import android.util.Log;

import com.example.util.Interfaces.MyStatListener;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ValidateEmail {

  /**
   *  Tag to add in and log messages.
   */
  private static final String TAG = "ValidateEmail";

  /**
   * The error messages in the class are stored here so that the parent class can have access to these through interface.
   */
  private String errMessgae;


  /**
   * The access key for the MaiboxLayer api. NEEDS TO BE CHANGED FOR NEW KEY IF UPDATED.
   * The credentials for the username = healthcare.umflint@gmail.com; password = androidDevelopmentHc
   */
  public static final String AccessKey = "f1d34ebbb7ff35da5ef47502cecea7de";

  public static final String BaseUrl = "https://apilayer.net/api/";

  public static final String ApiUrl = BaseUrl + "check?access_key=" + AccessKey;


  /**
   * Check with regular expressions to validate email format.
   * @param email - Email id of the user signing up.
   * @return returns true of false
   */
  static boolean isValid(String email) {
    String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    return email.matches(regex);
  }

  /**
   * Constructor that handle normal validation and validation through api.
   * @param email- Email id of the user signing up.
   * @param myStatListener An interface to handle the callbacks.
   */
  public ValidateEmail(String email, MyStatListener myStatListener) {
    errMessgae = null;
    if(!isValid(email)) {
      errMessgae = "Email invalid format, please check your email.";
      myStatListener.onFailure(errMessgae);
      return;
    }

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    StrictMode.setThreadPolicy(policy);

    if(!checkEmailExists(email)) {
      myStatListener.onFailure(errMessgae);
      return;
    }
    myStatListener.status(true, null);
    return;
  }

  /**
   * Validation email and checking if that actually exists using mailboxlayer api.
   * MailboxLayer api can validate upto 200 mails per month for free, any more than that a service needs to be bought.
   * IT IS MENTIONED IN THE HOME PAGE, "FOR PERSONAL USE" NEED TO READ THE TERMS AND CONDITIONS BEFORE DEPLOYMENT.
   * @param email Email id of the user signing up.
   */
  private boolean checkEmailExists(String email) {
    boolean valid = false;
    try {
      String url = ApiUrl + "&email="+ email + "&smtp=1&format=1";
      URL urlObj = new URL(url);

      HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", "Chrome/17.0");

      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

      String inputLine;

      StringBuffer response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JSONObject jsonResponse = new JSONObject(response.toString());

      if (!Boolean.parseBoolean(jsonResponse.get("format_valid").toString()) || !Boolean.parseBoolean(jsonResponse.get("mx_found").toString()) || !Boolean.parseBoolean(jsonResponse.get("smtp_check").toString())) {
        errMessgae = "Invalid Mail id";
      } else valid = true;

      Log.d(TAG, "checkEmailExists: response" + response.toString());
    } catch (Exception ex) {
      ex.printStackTrace();
      errMessgae = ex.getMessage();
      Log.d(TAG, "checkEmailExists: " + ex.getMessage());
    } finally {
      return valid;
    }
  }
}
