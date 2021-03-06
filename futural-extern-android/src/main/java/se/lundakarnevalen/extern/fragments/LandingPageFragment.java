package se.lundakarnevalen.extern.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import se.lundakarnevalen.extern.activities.ContentActivity;
import se.lundakarnevalen.extern.android.R;
import se.lundakarnevalen.extern.data.DataElement;
import se.lundakarnevalen.extern.data.DataType;

public class LandingPageFragment extends LKFragment{

    private float lat;
    private float lng;
    public LandingPageFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final DataElement element = bundle.getParcelable("element");
        final View rootView;

        if(element.type == DataType.FOODSTOCK) {
            rootView = inflater.inflate(R.layout.fragment_landing_page_foodstock, container, false);
        } else if(element.type == DataType.DEVELOPER) {
            rootView = inflater.inflate(R.layout.fragment_markus_filip_fredrik, container, false);

            ImageView header = get(rootView, R.id.header_background, ImageView.class);
            final ImageView header1 = get(rootView, R.id.markus_pic, ImageView.class);
            final ImageView header2 = get(rootView, R.id.filip_pic, ImageView.class);
            final ImageView header3 = get(rootView, R.id.fredrik_pic, ImageView.class);

            header.setOnTouchListener(new View.OnTouchListener() {

                Animation a1;
                Animation a2;
                Animation a3;


                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    float width = view.getWidth();
                    if(a1!=null) {
                    }
                        switch (motionEvent.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                                if(motionEvent.getX() < width/3) {
                                    startAnimation(1);
                                } else if(motionEvent.getX() > width-width/3) {
                                    startAnimation(3);
                                } else {

                                    startAnimation(2);
                                }

                                //startX = motionEvent.getX();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if(motionEvent.getX() < width/3) {
                                    startAnimation(1);
                                } else if(motionEvent.getX() > width-width/3) {
                                    startAnimation(3);
                                } else {

                                    startAnimation(2);
                                }
                                break;
                            case MotionEvent.ACTION_UP:



                                break;
                        }




                    return true;
                }
                private synchronized void startAnimation(int nbr) {
                    switch (nbr) {
                        case 1:
                            if(a1 == null || (!a1.hasStarted() || a1.hasEnded())) {
                                Log.d("run this","run!!");
                                a1 = new TranslateAnimation(0, 0, 0, -header1.getHeight());
                                a1.setRepeatMode(Animation.REVERSE);
                                a1.setRepeatCount(1);
                                a1.setDuration(500);
                                header1.startAnimation(a1);
                            }
                            break;
                        case 2:
                            if(a2 == null || (!a2.hasStarted() || a2.hasEnded())) {
                                a2 = new TranslateAnimation(0, 0, 0, -header2.getHeight());
                                a2.setRepeatMode(Animation.REVERSE);
                                a2.setRepeatCount(1);
                                a2.setDuration(500);

                                header2.startAnimation(a2);
                            }
                            break;
                        case 3:
                            if(a3 == null || (!a3.hasStarted() ||a3.hasEnded())) {
                                a3 = new TranslateAnimation(0, 0, 0, -header3.getHeight());
                                a3.setRepeatMode(Animation.REVERSE);
                                a3.setRepeatCount(1);
                                a3.setDuration(500);
                                header3.startAnimation(a3);
                            }

                            break;
                    }


                }

            });


            ImageView mapView = get(rootView, R.id.map_picture_1, ImageView.class);
            mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentActivity.class.cast(getActivity()).showMapAndPanDeveloper(lat, lng, 1);
                    ContentActivity.class.cast(getActivity()).ensureSelectedFilters(new DataType[]{element.type});
                }
            });

            mapView = get(rootView, R.id.map_picture_2, ImageView.class);
            mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentActivity.class.cast(getActivity()).showMapAndPanDeveloper(lat, lng,2);
                    ContentActivity.class.cast(getActivity()).ensureSelectedFilters(new DataType[]{element.type});
                }
            });

            mapView = get(rootView, R.id.map_picture_3, ImageView.class);
            mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentActivity.class.cast(getActivity()).showMapAndPanDeveloper(lat, lng,3);
                    ContentActivity.class.cast(getActivity()).ensureSelectedFilters(new DataType[]{element.type});
                }
            });

            return rootView;
        } else{
            rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);
        }

        get(rootView,R.id.name,TextView.class).setText(element.title);
        get(rootView,R.id.place,TextView.class).setText(element.place);

        Calendar c = Calendar.getInstance();

        switch (c.DAY_OF_MONTH) {
            case 23:
                get(rootView,R.id.open_info,TextView.class).setText(element.timeFriday);
                break;
            case 24:
                if(c.HOUR_OF_DAY < 6) {
                    get(rootView,R.id.open_info,TextView.class).setText(element.timeFriday);
                }
                get(rootView,R.id.open_info,TextView.class).setText(element.timeSaturday);
                break;
            case 25:
                if(c.HOUR_OF_DAY < 6) {
                    get(rootView,R.id.open_info,TextView.class).setText(element.timeSaturday);
                }
                get(rootView,R.id.open_info,TextView.class).setText(element.timeSunday);
                break;
            default:
                if(c.DAY_OF_MONTH > 25) {
                    get(rootView, R.id.open_info, TextView.class).setText(element.timeSunday);
                } else {
                    get(rootView,R.id.open_info,TextView.class).setText(element.timeFriday);
                }
                    break;
        }

        lat = element.lat;
        lng = element.lng;
        final DataType type = element.type;

        get(rootView,R.id.picture,ImageView.class).setImageResource(element.picture_list);
        get(rootView,R.id.header_background,ImageView.class).setImageResource(element.headerPicture);

        ImageView mapView = get(rootView, R.id.map_picture, ImageView.class);
        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<DataType> multiContainers = new HashSet<>();
                multiContainers.add(DataType.TENT_FUN);
                multiContainers.add(DataType.TOMBOLAN);
                multiContainers.add(DataType.MUSIC);
                multiContainers.add(DataType.FOODSTOCK);
                multiContainers.add(DataType.SNACKS);
                multiContainers.add(DataType.TOILETS);
                multiContainers.add(DataType.SECURITY);
                multiContainers.add(DataType.CARE);
                multiContainers.add(DataType.TRASHCAN);
                multiContainers.add(DataType.ENTRANCE);

                if(element.type == DataType.TRAIN) {
                    ContentActivity.class.cast(getActivity()).loadFragmentAddingBS(TrainMapFragment.create());
                } else if (multiContainers.contains(element.type)) {
                    ContentActivity.class.cast(getActivity()).showMapAndPanTo(lat, lng);
                    DataType[] types;
                    switch (element.type) {
                        case FOODSTOCK: case SNACKS:
                            types = new DataType[]{DataType.FOOD};
                            break;
                        case SECURITY: case CARE:
                            types = new DataType[]{DataType.SECURITY, DataType.CARE};
                            break;
                        default:
                            types = new DataType[]{element.type};
                            break;
                    }
                    ContentActivity.class.cast(getActivity()).ensureSelectedFilters(types);
                } else {
                    ContentActivity.class.cast(getActivity()).showMapAndPanTo(lat, lng);
                    ContentActivity.class.cast(getActivity()).ensureSelectedFilters(new DataType[]{element.type});
                }
            }
        });


        if(type== DataType.FUN) {
            get(rootView, R.id.question, TextView.class).setText(element.question);
            get(rootView, R.id.text, TextView.class).setText(Html.fromHtml(getString(element.info)));

        } else if(type == DataType.FOOD || type == DataType.FOODSTOCK) {
            get(rootView, R.id.question, TextView.class).setText(element.question);
            get(rootView, R.id.text, TextView.class).setText(element.info);
            get(rootView, R.id.middleLayout, RelativeLayout.class).setBackgroundColor(getResources().getColor(R.color.green_background));

            if (element.menu != null) {
                get(rootView,R.id.menu,RelativeLayout.class).setVisibility(View.VISIBLE);
                LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.menu_food_list);

                for(int i = 0;i< element.menu.size();i++){
                    View view = inflater.inflate(R.layout.menu_food_element, ll, false);
                    ((TextView)view.findViewById(R.id.name)).setText((i+1)+". "+getString(element.menu.get(i)));
                    ((TextView)view.findViewById(R.id.price)).setText(element.menuPrice.get(i));
                    ll.addView(view);
                }
            }
        } else if(type == DataType.BILJETTERIET || type == DataType.SHOPPEN|| type == DataType.TRASHCAN|| type == DataType.ENTRANCE) {
            get(rootView, R.id.question, TextView.class).setText(element.question);
            get(rootView, R.id.text, TextView.class).setText(Html.fromHtml(getString(element.info)));
            get(rootView, R.id.middleLayout, RelativeLayout.class).setBackgroundColor(getResources().getColor(R.color.blue_dark));
            get(rootView, R.id.middleView, View.class).setVisibility(View.INVISIBLE);

        } else if(type == DataType.TENT_FUN || type == DataType.SMALL_FUN || type == DataType.TOMBOLAN ||type == DataType.MUSIC ||type == DataType.SCENE) {
            get(rootView, R.id.question, TextView.class).setText(element.question);
            get(rootView, R.id.text, TextView.class).setText(element.info);
            get(rootView, R.id.middleView, View.class).setVisibility(View.INVISIBLE);

        } else if(type == DataType.SNACKS) {
            get(rootView, R.id.question, TextView.class).setVisibility(View.GONE);
            get(rootView, R.id.text, TextView.class).setText(element.info);
            get(rootView, R.id.middleLayout, RelativeLayout.class).setBackgroundColor(getResources().getColor(R.color.green_background));
            get(rootView, R.id.middleView, View.class).setVisibility(View.INVISIBLE);
        }else if(type == DataType.TOILETS || type == DataType.SECURITY || type == DataType.CARE) {
            get(rootView, R.id.question, TextView.class).setVisibility(View.GONE);
            get(rootView, R.id.text, TextView.class).setText(element.info);
            get(rootView, R.id.middleLayout, RelativeLayout.class).setBackgroundColor(getResources().getColor(R.color.blue_dark));
            get(rootView, R.id.middleView, View.class).setVisibility(View.INVISIBLE);
        }else if(type == DataType.TRAIN) {
            get(rootView, R.id.question, TextView.class).setText(element.question);
            get(rootView, R.id.text, TextView.class).setText(element.info);
            get(rootView, R.id.middleLayout, RelativeLayout.class).setBackgroundColor(getResources().getColor(R.color.blue_dark));
            get(rootView,R.id.card_box,RelativeLayout.class).setVisibility(View.INVISIBLE);
            get(rootView,R.id.cash_box,RelativeLayout.class).setVisibility(View.INVISIBLE);
            get(rootView,R.id.map_info,TextView.class).setText(R.string.to_traint);
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        ContentActivity.class.cast(getActivity()).allBottomsUnfocus();
    }

    public static LandingPageFragment create(DataElement element) {
        LandingPageFragment fragment = new LandingPageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("element",element);

        fragment.setArguments(bundle);
        return fragment;
    }
}


