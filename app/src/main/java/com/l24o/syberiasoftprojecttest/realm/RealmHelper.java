package com.l24o.syberiasoftprojecttest.realm;

import android.support.annotation.NonNull;

import com.l24o.syberiasoftprojecttest.model.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * @author Alexander Popov on 16.05.2016.
 */
public class RealmHelper {

    public static <T extends RealmObject> void save(@NonNull Realm realm, Class<T> clazz, InputStream is) {
        realm.beginTransaction();
        try {
            realm.createAllFromJson(clazz, is);
            realm.commitTransaction();
        } catch (IOException e) {
            realm.cancelTransaction();
            e.printStackTrace();
        }
    }

    public static long getCount(@NonNull Realm realm, Boolean isF) {
        if (!isF)
            return realm.where(Image.class).equalTo("favorite", true).count();
        else
            return realm.where(Image.class).count();
    }

    public static <T extends RealmObject> List<T> getAll(@NonNull Realm realm, Class<T> clazz) {
        return realm.where(clazz).findAll().sort("displayOrder");
    }

    public static List<Image> getAllF(@NonNull Realm realm) {
        return realm.where(Image.class).equalTo("favorite", true).findAll();
    }

    public static <T extends RealmObject> void save(@NonNull Realm realm, T object) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
    }

}
