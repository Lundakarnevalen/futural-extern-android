package se.lundakarnevalen.extern.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import se.lundakarnevalen.extern.android.R;

public class DataElement implements Parcelable {
    public String timeFriday = "00:00-00:00";
    public String timeSaturday = "00:00-00:00";
    public String timeSunday = "00:00-00:00";
    public int card = 1;
    public int cash = 1;
    public int title;
    public int place;
    public float lat;
    public float lng;
    public int headerPicture;
    public int picture;
    public int picture_list;
    public int question;
    public int info;
    public DataType type;
    public ArrayList<Integer> menu;
    public ArrayList<String> menuPrice;

    public DataElement() {}

    public DataElement(int place, int title, int info, float lat, float lng, int headerPicture, int picture, int picture_list, int question, String timeFriday, String timeSaturday, String timeSunday, DataType type) {
        this.place = place;
        this.title = title;
        this.info = info;
        this.lat = lat;
        this.lng = lng;
        this.headerPicture = headerPicture;
        this.picture = picture;
        this.question = question;
        this.type = type;
        this.timeFriday = timeFriday;
        this.timeSaturday = timeSaturday;
        this.timeSunday = timeSunday;
        this.picture_list = picture_list;
    }


    public DataElement(int place, int title, int info, float lat, float lng, int headerPicture, int picture, int picture_list, int question, String timeFriday, String timeSaturday, String timeSunday, DataType type, ArrayList<Integer> menu, ArrayList<String> menuPrice) {
        this.place = place;
        this.title = title;
        this.info = info;
        this.lat = lat;
        this.lng = lng;
        this.headerPicture = headerPicture;
        this.picture = picture;
        this.question = question;
        this.type = type;
        this.timeFriday = timeFriday;
        this.timeSaturday = timeSaturday;
        this.timeSunday = timeSunday;
        this.menu = menu;
        this.menuPrice = menuPrice;
        this.picture_list = picture_list;
    }



    public DataElement(int title, int picture, int picture_list ,DataType type) {
        this.title = title;
        this.picture = picture;
        this.type = type;
        this.picture_list = picture_list;
    }

    public DataElement(int place, int title, float lat, float lng, int picture, DataType type) {
        this.place = place;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.picture = picture;
        this.type = type;
    }

    public DataElement(int title, int place, int question, int info ,float lat, float lng, int picture, int picture_list, DataType type) {
        this.title = title;
        this.place = place;
        this.lat = lat;
        this.lng = lng;
        this.picture = picture;
        this.type = type;
        this.question = question;
        this.info = info;
        this.picture_list = picture_list;
    }

    public DataElement(int place, int title, int info, int question, float lat, float lng, int headerPicture, int picture, int picture_list, DataType type) {
        this.place = place;
        this.title = title;
        this.info = info;
        this.lat = lat;
        this.lng = lng;
        this.headerPicture = headerPicture;
        this.picture = picture;
        this.question = question;
        this.picture_list = picture_list;
        this.type = type;

    }

    public DataElement(int title, int question, int info ,float lat, float lng, int headerPicture, int picture, DataType type) {
        this.title = title;
        this.place = R.string.empty;
        this.info = info;
        this.lat = lat;
        this.lng = lng;
        this.headerPicture = headerPicture;
        this.picture_list = picture;
        this.question = question;
        this.type = type;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(lat);
        parcel.writeFloat(lng);
        parcel.writeInt(headerPicture);
        parcel.writeInt(picture);
        parcel.writeInt(picture_list);
        parcel.writeSerializable(type);
        parcel.writeInt(info);
        parcel.writeInt(question);
        parcel.writeInt(title);
        parcel.writeInt(place);
        parcel.writeInt(cash);
        parcel.writeInt(card);
        parcel.writeString(timeFriday);
        parcel.writeString(timeSaturday);
        parcel.writeString(timeSunday);
        parcel.writeSerializable(menu);
        parcel.writeSerializable(menuPrice);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataElement createFromParcel(Parcel in) {
            DataElement e = new DataElement();
            e.lat = in.readFloat();
            e.lng = in.readFloat();
            e.headerPicture = in.readInt();
            e.picture = in.readInt();
            e.picture_list = in.readInt();
            e.type = (DataType) in.readSerializable();
            e.info = in.readInt();
            e.question = in.readInt();
            e.title = in.readInt();
            e.place = in.readInt();
            e.cash = in.readInt();
            e.card = in.readInt();
            e.timeFriday = in.readString();
            e.timeSaturday = in.readString();
            e.timeSunday = in.readString();
            e.menu = (ArrayList<Integer>) in.readSerializable(); // Should be right
            e.menuPrice =(ArrayList<String>) in.readSerializable(); // Don't care
            return e;
        }

        public DataElement[] newArray(int size) {
            return new DataElement[size];
        }
    };

    public boolean hasLandingPage() {
        return info > 0;
    }

    public boolean isRadio() {
        return type == DataType.RADIO;
    }

}
