package net.a40two.pext.services;

import net.a40two.pext.Constants;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PastebinPasteService {

    public static void buildPasteUrl(String body, String title, String expires, String privacy, String syntax, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String postTitle = title;
        String postExpires = expires;
        String postPrivacy = privacy;
        String postSyntax = syntax;
        String userApiKey = "";

        if (postExpires.equals("")) {
            postExpires = "N";
        }
        if (postPrivacy.equals("")) {
            postPrivacy = "0";
        }
        if (postSyntax.equals("")) {
            postSyntax = "text";
        }
        if (Constants.CURRENT_USER == null || userApiKey.contains("invalid")) {
            userApiKey = "";
            //no idea if this will work
        } else {
            userApiKey = Constants.CURRENT_USER.getUserApiKey();
        }

        RequestBody formBody = new FormBody.Builder()
                .add(Constants.DEV_API_KEY_PARAM, Constants.DEV_API_KEY)
                .add(Constants.API_OPTION, "paste")
                .add(Constants.PASTE_BODY_PARAM, body)
                .add(Constants.PASTE_NAME_PARAM, postTitle)
                .add(Constants.PASTE_FORMAT_PARAM, postSyntax)
                .add(Constants.PASTE_EXPIRE_PARAM, postExpires)
                .add(Constants.USER_API_KEY_PARAM, userApiKey)
                .build();
        Request request = new Request.Builder()
                .url(Constants.BASE_URL)
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String processResult(Response response) {

        String respBody = "";
        try {
            respBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return respBody;
    }
}
