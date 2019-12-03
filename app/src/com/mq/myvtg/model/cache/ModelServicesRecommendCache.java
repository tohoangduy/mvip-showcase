package com.mq.myvtg.model.cache;

import com.mq.myvtg.model.ModelServiceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen.dang.tho on 7/6/2017.
 */

public class ModelServicesRecommendCache extends ModelCacheExt {
    public List<ModelServiceGroup> listServicesRecommend = new ArrayList<>();

    public List<ModelServiceGroup> getListPackage(String currentLang) {
        if (language.equalsIgnoreCase(currentLang)) {
            return listServicesRecommend;
        } else {
            return null;
        }
    }

    public void setListPackage(List<ModelServiceGroup> data, String currentLang) {
        language = currentLang;
        listServicesRecommend.addAll(data);
    }
}
