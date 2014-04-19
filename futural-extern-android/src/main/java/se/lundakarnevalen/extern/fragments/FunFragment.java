package se.lundakarnevalen.extern.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import se.lundakarnevalen.extern.android.ContentActivity;
import se.lundakarnevalen.extern.android.R;

/**
 * Created by Markus on 2014-04-16.
 */
public class FunFragment extends LKFragment{

    private ArrayList<Fun> fun = new ArrayList<Fun>();

    private static class FunItem {
        String text1;
        String text2;

        private FunItem(String text1, String text2) {
            this.text1 = text1;
            this.text2 = text2;
        }
    }

    // Every time you switch to this fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fun, null);

        ListView lv = (ListView) rootView.findViewById(R.id.fragment_fun_list);
        if(fun.isEmpty()) {
            addAllFun();
        }


        lv.setAdapter(new SimpleAdapter(getContext(),
                new ArrayList<Map<String, String>>() {{
                    for(final Fun f: fun) {
                        add(new HashMap<String, String>() {{
                            put("title", f.title);
                            put("time", "17:00 - 19:00");
                        }});
                    }
                }}, R.layout.element_listitem,
                new String[]{"title", "time"},
                new int[]{android.R.id.text1, android.R.id.text2}
        ));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContentActivity.class
                        .cast(getActivity())
                        .loadFragment(LandingPageFragment.create(fun.get(position).title,fun.get(position).place,true,true,true,fun.get(position).lat,fun.get(position).lng,fun.get(position).picture,fun.get(position).headerPicture,fun.get(position).question,fun.get(position).info), true);
            }
        });
        return rootView;
    }

    private void addAllFun() {
        fun.add(new Fun(
                getString(R.string.kabare_place),
                getString(R.string.kabare_title),
                getString(R.string.kabare_info),
                55.7042667f,13.193833333333334f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.kabare_question)));
        fun.add(new Fun(
                getString(R.string.filmen_place),
                getString(R.string.filmen_title),
                getString(R.string.filmen_info),
                55.7059389f, 13.194805555555556f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.filmen_question)));
        fun.add(new Fun(
                getString(R.string.barneval_place),
                getString(R.string.barneval_title),
                getString(R.string.barneval_info),
                55.7037889f, 13.194647222222223f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.barneval_question)));
        fun.add(new Fun(
                getString(R.string.cirkus_place),
                getString(R.string.cirkus_title),
                getString(R.string.cirkus_info),
                55.7048333f, 13.195352777777778f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.cirkus_question)));
        fun.add(new Fun(
                getString(R.string.spexet_place),
                getString(R.string.spexet_title),
                getString(R.string.spexet_info),
                55.7054111f, 13.195491666666667f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.spexet_question)));
        fun.add(new Fun(
                getString(R.string.showen_place),
                getString(R.string.show_title),
                getString(R.string.show_info),
                55.7055444f, 13.195588888888889f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.showen_question)));
        fun.add(new Fun(
                getString(R.string.revy_place),
                getString(R.string.revy_title),
                getString(R.string.revy_info),
                55.705775f, 13.193555555555555f,
                R.drawable.header_kabare,
                R.drawable.header_kabare,
                getString(R.string.revy_question)));





        // add all fun here...

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class Fun {
        String title;
        String place;
        float lat;
        float lng;
        int headerPicture;
        int picture;
        String question;
        String info;

        private Fun(String place, String title, String info, float lat, float lng, int headerPicture, int picture, String question) {
            this.place = place;
            this.title = title;
            this.info = info;
            this.lat = lat;
            this.lng = lng;
            this.headerPicture = headerPicture;
            this.picture = picture;
            this.question = question;
        }


    }



}