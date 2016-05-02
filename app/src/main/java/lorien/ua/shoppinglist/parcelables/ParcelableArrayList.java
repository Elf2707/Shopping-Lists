package lorien.ua.shoppinglist.parcelables;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import java.util.ArrayList;

/**
 * Created by Elf on 01.04.2016.
 */
public class ParcelableArrayList<T> extends ArrayList<T> implements Parcelable {
    public static Parcelable.Creator<ParcelableArrayList>
            CREATOR = new Parcelable.Creator<ParcelableArrayList>() {
        @Override
        public ParcelableArrayList createFromParcel(Parcel source) {
            return new ParcelableArrayList(source);
        }

        @Override
        public ParcelableArrayList[] newArray(int size) {
            return new ParcelableArrayList[size];
        }
    };

    public ParcelableArrayList() {

    }

    public ParcelableArrayList(Parcel source) {
        int size = source.readInt();

        for (int i = 0; i < size; i++) {
            add( (T) source.readValue(null));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());

        for(int i = 0; i < size(); i++){
            dest.writeValue( get(i));
        }
    }
}
