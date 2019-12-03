package com.mq.myvtg.model.cache;

import com.mq.myvtg.model.ModelDataPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen.dang.tho on 7/10/2017.
 */

public class ModelListDataPackageCache extends ModelCacheExt {
    public List<ModelDataPackage> listPackage = new ArrayList<>();
    public List<ModelDataPackage> getListPackage(String currentLang) {
        if (language.equalsIgnoreCase(currentLang)) {
            return listPackage;
        } else {
            return null;
        }
    }

    public void setListPackage(List<ModelDataPackage> data, String currentLang) {
        language = currentLang;
        listPackage.addAll(data);
    }
}
