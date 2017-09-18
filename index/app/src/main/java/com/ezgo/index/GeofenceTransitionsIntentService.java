package com.ezgo.index;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GTIntentService";
    public static int WHICHAREA;

    public GeofenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve the Geofencing intent
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if ( event.hasError() ) {
            return;
        }

        int transition = event.getGeofenceTransition(); // Get the transition type.
        // Get the geofence that were triggered
        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        Geofence geofence = triggeringGeofences.get(0);
        final String requestId = geofence.getRequestId();

        // Check if the transition type
        if ( transition == Geofence.GEOFENCE_TRANSITION_ENTER ) {   //進入範圍
            //Log.e(TAG,"Entering geofence -"+requestId);

            WHICHAREA=Integer.parseInt(requestId);  //進入哪個範圍

            //-----------------傳送訊息至arActivity
            Intent lbcIntent = new Intent("googlegeofence"); //Send to any reciever listening for this
            lbcIntent.putExtra("from","enter");
            LocalBroadcastManager.getInstance(GeofenceTransitionsIntentService.this).sendBroadcast(lbcIntent);  //Send the intent


        }else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT ){ //離開範圍
            //Log.e(TAG,"Exiting geofence -"+requestId);

            Intent lbcIntent = new Intent("googlegeofence"); //Send to any reciever listening for this
            lbcIntent.putExtra("from","exit");
            LocalBroadcastManager.getInstance(GeofenceTransitionsIntentService.this).sendBroadcast(lbcIntent);  //Send the intent
        }else{
            //Log.e(TAG, getString(transition));
        }

    }
}
