package se.lundakarnevalen.extern.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import se.lundakarnevalen.extern.android.R;
import se.lundakarnevalen.extern.map.Marker;
import se.lundakarnevalen.extern.map.MarkerType;
import se.lundakarnevalen.extern.map.Markers;
import se.lundakarnevalen.extern.util.BitmapUtil;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class MapFragment extends LKFragment implements View.OnTouchListener {

    private static final int TIME_INTERVAL = 1800000; // get gps location every 30 min
    private static final int GPS_DISTANCE = 0; // set the distance value in meter
    // States onTouchEvent
    private final int NONE = 0;
    private int mode = NONE;
    private final int DRAG = 1;
    private final int ZOOM = 2;
    private Handler handler;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private Matrix matrix;
    private Matrix savedMatrix = new Matrix();
    private boolean isActive;
    // Save current dots
    private Bitmap bmOverlay;
    private ImageView img;
    private int imageWidth;
    private int imageHeight;
    // Variables for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float newDist = 1f;
    // Control scale 1 = full size
    private float scale = 1f;
    private boolean firstTime = true;
    // Context
    private Context context;
    // For gps and network
    private LocationManager locMan;
    private float myLat;
    private float myLng;

    // Information about the map
/*
//55.7037889,(float)13.194647222222223
   // (float)55.7054111,(float)13.195491666666667
    // only fake, for next map...
    private float startLonMap = (float) 13.190449839578941;
    private float startLatMap = (float) 55.69015099913018;
    private float endLonMap = (float) 13.200917368875816;
    private float endLatMap = (float) 55.72300194685981;
*/
    private float startLonMap = (float) 12.445449839578941;
    private float startLatMap = (float) 55.33715099913018;
    private float endLonMap = (float) 14.580917368875816;
    private float diffLon = endLonMap - startLonMap;
    private float endLatMap = (float) 56.52300194685981;
    private float diffLat = endLatMap - startLatMap;

    private HashMap<Integer, Boolean> active = new HashMap<Integer, Boolean>();

    // Every time you switch to this fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, null);

        if (active.size() == 0) {
            active.put(MarkerType.FOOD, true);
            active.put(MarkerType.FUN, true);
            active.put(MarkerType.HELP, true);
            active.put(MarkerType.WC, true);
        }

        context = getContext();


        if (markers.size() == 0) {
            Markers.addMarkers(markers);
        }

        if (imageWidth == 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            imageWidth = metrics.widthPixels;
            imageHeight = metrics.heightPixels;
        }
        img = ((ImageView) rootView.findViewById(R.id.map_id));


        if (matrix != null) {
            if (bmOverlay != null) {
                img.setImageBitmap(bmOverlay);
            }
            if (!firstTime) { //test
                img.setScaleType(ImageView.ScaleType.MATRIX);
                img.setImageMatrix(matrix);
            }
        } else {

            matrix = new Matrix();
            firstTime = true;
            isActive = true;
            //TODO fix bug with switch to another fragment and the back again to map...
            PositionTask positionTask = new PositionTask();
            positionTask.execute();

        }

        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (isActive) {
                        getPosition();
                        updatePositions();
                        handler.postDelayed(this, 10000);
                    } else {
                        handler.postDelayed(this, 10000);
                    }
                }
            }, 0);
        }

        Bundle bundle = getArguments();

        if(bundle != null) {
            if(bundle.getBoolean("zoom")) {
                Log.d("here!","yes");
                zoomInto(bundle.getFloat("lat"),bundle.getFloat("lng"));
            }
        }

        img.setOnTouchListener(this);


        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        isActive = false;
        super.onPause();
    }


    @Override
    public void onResume() {
        isActive = true;
        super.onResume();
    }

    public void updatePositions() {
        Bitmap mapBitmap = BitmapUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.test_map, imageWidth, imageHeight);

        // Create an overlay bitmap
        bmOverlay = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());

        final Canvas canvas = new Canvas();
        canvas.setBitmap(bmOverlay);
        canvas.drawBitmap(mapBitmap, new Matrix(), null);

        final Paint paintRed = getColoredPaint(R.color.red);
        final Paint paintGray = getColoredPaint(R.color.blue_purple);

        float lat = (myLat - startLatMap) / diffLat;
        float lon = (myLng - startLonMap) / diffLon;
        float x = lon * mapBitmap.getWidth();
        float y = mapBitmap.getHeight() - lat * mapBitmap.getHeight();

        canvas.drawCircle(x,y, 20, paintRed);

        for (Marker m : markers) {

            if (active.get(m.type) != null && active.get(m.type)) {

                if (m.x == -1) {
                    lat = (m.lat - startLatMap) / diffLat;
                    lon = (m.lng - startLonMap) / diffLon;
                    x = lon * mapBitmap.getWidth();
                    y = mapBitmap.getHeight() - lat * mapBitmap.getHeight();
                    // draw canvas..
                    m.x = x;
                    m.y = y;
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), m.picture);

                canvas.drawBitmap(bitmap, m.x - bitmap.getWidth() / 2, m.y - bitmap.getHeight() / 2, null);
            }
            //canvas.dra(x, y, 10, paintRed);
        }

        img.setImageBitmap(bmOverlay);


    }

    private Paint getColoredPaint(int colorId) {
        final Paint paintRed = new Paint();
        paintRed.setColor(getResources().getColor(colorId));
        return paintRed;
    }

    public void getPosition() {
        if (locMan == null) {
            locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        // Only turn off get position with GPS. Ok with network...
        if (locMan.isProviderEnabled(GPS_PROVIDER)) {
            locMan.requestLocationUpdates(GPS_PROVIDER, TIME_INTERVAL, GPS_DISTANCE, PositionListener);
            Log.d("Updateing GPS!", "Update");
        } else {
            Log.d("GPS off", "Avst�ngd GPS");
            if (locMan.isProviderEnabled(NETWORK_PROVIDER)) {
                locMan.requestLocationUpdates(NETWORK_PROVIDER, TIME_INTERVAL, GPS_DISTANCE, PositionListener);
                Log.d("Updateing Position with network!", "Update");
            }
        }

        Location location = locMan.getLastKnownLocation(GPS_PROVIDER);
        if (location != null) {
            myLat = (float) location.getLatitude();
            myLng = (float) location.getLongitude();
            Log.d("Find GPS_position", myLat + " " + myLng);
        } else {
            location = locMan.getLastKnownLocation(NETWORK_PROVIDER);
            if (location != null) {
                myLng = (float) location.getLongitude();
                myLat = (float) location.getLatitude();
                Log.d("Find Network_position", myLat + " " + myLng);
            } else {
                Log.d("No GPS or Network position", "FAIL1");
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        matrix.set(view.getImageMatrix());
        view.setScaleType(ImageView.ScaleType.MATRIX);
        firstTime = false;
        float scale;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted
                if (mode == ZOOM) {
                    this.scale = this.scale * newDist / oldDist;
                    // generateDots(this.scale);
                } else {

                    if ((start.x - event.getX()) * (start.x - event.getX()) + (start.y - event.getY()) * (start.y - event.getY()) < 10) {

                        float[] values = new float[9];
                        matrix.getValues(values);
                        // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                        // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.
                        float relativeX = (event.getX() - values[2]) / values[0];
                        float relativeY = (event.getY() - values[5]) / values[4];
                        Log.d("rel x and y", "x: " + relativeX + " y: " + relativeY);
                        // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
                        checkClick(relativeX, relativeY);
                    } else {

                    }
                }
                mode = NONE;
                // Uppdatera mapen...

                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                newDist = oldDist;
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist2 = spacing(event);
                    // Lock zoom out
                    if (this.scale * newDist2 / oldDist >= 1) {
                        //newDist = newDist2;
                        newDist = newDist2;
                        if (newDist > 5f) {
                            matrix.set(savedMatrix);
                            scale = newDist / oldDist;
                            // setting the scaling of the
                            // matrix...if scale > 1 means
                            // zoom in...if scale < 1 means
                            // zoom out
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }
                    }
                }
                break;
        }
        view.setImageMatrix(matrix); // display the transformation on screen
        // v.getLeft() = 0; v.getRight() = 480
        /*
        int []location = new int[10];
        view.getLocationInWindow(location);
        for(int i = 0;i<location.length;i++) {
            Log.d("loc"+i,"get: "+location[i]+"");
        }
        */
        return true; // indicate event was handled
    }

    private LocationListener PositionListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // update location
            locMan.removeUpdates(PositionListener); // remove this listener
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * Calculate the space between the two fingers on touch
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * @param relativeX
     * @param relativeY
     */
    private void checkClick(float relativeX, float relativeY) {
        if (bmOverlay != null) {
            for (Marker m : markers) {
                if (m.isClose(relativeX, relativeY)) {

                    return;
                }
            }
        }
    }

    /**
     * Calculates the midpoint between the two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void changeActive(int i, boolean activated) {
        active.put(i, activated);
    }

    private class PositionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            /*
            final Handler handler;
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.d("isinactive","new position");
                        if(isActive) {
                            Log.d("isactive","new position");
                            getPosition();
                            updatePositions();

                        }
                        handler.postDelayed(this, 10000);
                    }
              }, 0);

        */

        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }
    }

    public void zoomInto(float lat, float lng) {
     //   scale = 2;
        Bitmap mapBitmap = BitmapUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.test_map, imageWidth, imageHeight);

        float lat2 = (lat - startLatMap) / diffLat;
        float lon2 = (lng - startLonMap) / diffLon;
        float x = lon2 * mapBitmap.getWidth();
        float y = mapBitmap.getHeight() - lat2 * mapBitmap.getHeight();
        img.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.set(img.getImageMatrix());
        float values[] = new float[10];
        matrix.getValues(values);
        // values[2] and values[5] are the x,y coordinates of the top left corner of the drawable image, regardless of the zoom factor.
        // values[0] and values[4] are the zoom factors for the image's width and height respectively. If you zoom at the same factor, these should both be the same value.
        float relativeX = (imageWidth/2 - values[2]) / values[0];
        float relativeY = (imageHeight/2 - values[5]) / values[4];
        //matrix.postTranslate(mapBitmap.getWidth()/2-x,mapBitmap.getHeight()/2-y);
       // matrix.postTranslate(mapBitmap.getWidth()/2-x,mapBitmap.getHeight()/2-y);
        // draw canvas..
        Log.d("get","x= "+x+" y= "+y);
        Log.d("get","widthx= "+mapBitmap.getWidth()+" widthy= "+mapBitmap.getHeight());
        Log.d("change:","x: "+(mapBitmap.getWidth()/2-x)+" y: "+(mapBitmap.getHeight()/2-y));
        Log.d("scale"," x: "+values[0]+" y: "+values[4]);
        matrix.postTranslate(relativeX-x,relativeY-y);

        img.setImageMatrix(matrix);
        scale = 2;

    }

    public static MapFragment create(boolean zoom, float lat, float lng) {
        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();

        bundle.putFloat("lat", lat);
        bundle.putFloat("lng", lng);
        bundle.putBoolean("zoom", zoom);

        fragment.setArguments(bundle);
        // Add arguments
        return fragment;
    }


}