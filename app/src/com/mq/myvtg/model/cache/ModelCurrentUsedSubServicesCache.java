package com.mq.myvtg.model.cache;

import com.mq.myvtg.model.ModelServiceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen.dang.tho on 7/27/2017.
 */

public class ModelCurrentUsedSubServicesCache extends ModelCacheExt {
    private List<ModelServiceItem> listServices = new ArrayList<>();

    public List<ModelServiceItem> getListPackage(String currentLang) {
        if (language.equalsIgnoreCase(currentLang)) {
            return listServices;
        } else {
            return null;
        }
    }

    public void setListPackage(List<ModelServiceItem> data, String currentLang) {
        language = currentLang;
        listServices.addAll(data);
    }
}
