package se.lundakarnevalen.extern.map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import se.lundakarnevalen.extern.util.KarnevalistServer;

public class LocationTracker extends Service {
    private static final String LOG_TAG = LocationTracker.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final int UPDATE_DELAY_MILLIS = 60000;
    public static final int INITAL_DELAY_MILLIS = 100;

    private final Context mContext;
    private final KarnevalistServer mConnection;
    private final Timer mTimer;

    private List<LocationJSONListener> mListeners;
    private LocationJSONResult mLatestResult;

    public void invalidateMe(LocationJSONListener listener) {
        if(mLatestResult != null) {
            listener.onNewLocationFromKarnevalist(mLatestResult);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface LocationJSONListener {
        void onNewLocationFromKarnevalist(LocationJSONResult result);
    }


    public LocationTracker(Context context) {
        this.mContext = context;
        this.mConnection = new KarnevalistServer(context);
        this.mListeners = new ArrayList<>(2);
        this.mTimer = new Timer();
        init();
    }

    private void init() {
        this.mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateLocation();
            }
        }, INITAL_DELAY_MILLIS, UPDATE_DELAY_MILLIS);
    }

    public void updateLocation() {
        try {
            mConnection.requestServerForText("api/train_positions", "", KarnevalistServer.RequestType.GET, new KarnevalistServer.TextResultListener() {
                @Override
                public void onResult(String json) {
                    Log.d(LOG_TAG, "Reponse from server: "+json);
                    Gson gson = new Gson();
                    LocationJSONResult jsonResult = mLatestResult = gson.fromJson(json, LocationJSONResult.class);
                    for (LocationJSONListener listener : mListeners) {
                        listener.onNewLocationFromKarnevalist(jsonResult);
                    }
                }
            });
        } catch (Throwable e) {
            Log.wtf(LOG_TAG, "Failed to acquire locations", e);
            e.printStackTrace();
        }
    }

    public static class LocationJSONResult {
        public List<LatLng> train_positions;
        public boolean success;

        public LocationJSONResult() {}

        public static class LatLng {
            public float lat = 0.0f;
            public float lng =  0.0f;
            public int id = 0;

            public LatLng() {}
        }
    }

    public void stopUpdates() {
        mTimer.cancel();
    }

    public void addListener(LocationJSONListener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(LocationJSONListener listener) {
        this.mListeners.remove(listener);
    }
}