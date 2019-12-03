package com.mq.myvtg.model.cache;

import com.mq.myvtg.model.ModelServiceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen.dang.tho on 7/6/2017.
 */

public class ModelServicesSubscribedCache extends ModelCacheExt {
    private List<ModelServiceItem> listServicesSubscribe = new ArrayList<>();

    public List<ModelServiceItem> getListPackage(String currentLang) {
        if (language.equalsIgnoreCase(currentLang)) {
            return listServicesSubscribe;
        } else {
            return null;
        }
    }

    public void setListPackage(List<ModelServiceItem> data, String currentLang) {
        language = currentLang;
        listServicesSubscribe.addAll(data);
    }
}
