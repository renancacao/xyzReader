package com.example.xyzreader.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;

import com.example.xyzreader.remote.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.xyzreader.intent.action.STATE_CHANGE";

    public static final String EXTRA_STATUS
            = "com.example.xyzreader.intent.extra.STATUS";

    public static final int START=0;
    public static final int NOTHING=1;
    public static final int NEW_CONTET=2;
    public static final int ERROR=3;
    public static final int RELOAD = 4;

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Time time = new Time();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            sendStickyBroadcast(
                    new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_STATUS, ERROR));
            return;
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_STATUS, START));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildDirUri();

        boolean newItens = false;

        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray();
            if (array == null || array.length()==0 ) {
                throw new JSONException("Invalid parsed item array" );
            }

            String id;
            Bundle args = new Bundle();


            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);
                id=object.getString("id");
                args = getContentResolver().call(dirUri,ItemsProvider.CALL_EXIST,id,null);

                if (args != null && !args.getBoolean(ItemsProvider.CALL_EXIST_RESPONSE)) {

                    sendStickyBroadcast(
                            new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_STATUS, NEW_CONTET));

                    newItens= true;
                    ContentValues values = new ContentValues();
                    values.put(ItemsContract.Items.SERVER_ID, object.getString("id"));
                    values.put(ItemsContract.Items.AUTHOR, object.getString("author"));
                    values.put(ItemsContract.Items.TITLE, object.getString("title"));
                    values.put(ItemsContract.Items.BODY, Html.fromHtml(object.getString("body")).toString());
                    values.put(ItemsContract.Items.THUMB_URL, object.getString("thumb"));
                    values.put(ItemsContract.Items.PHOTO_URL, object.getString("photo"));
                    values.put(ItemsContract.Items.ASPECT_RATIO, object.getString("aspect_ratio"));
                    values.put(ItemsContract.Items.PUBLISHED_DATE, object.getString("published_date"));
                    cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());


                }
            }

            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

            if (newItens){
                sendStickyBroadcast(
                        new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_STATUS, RELOAD));
            }
            else{
                sendStickyBroadcast(
                        new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_STATUS, NOTHING));
            }

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
            sendStickyBroadcast(
                    new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_STATUS, ERROR));
        }


    }
}
