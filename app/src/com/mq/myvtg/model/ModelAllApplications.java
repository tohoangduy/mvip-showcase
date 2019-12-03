package com.mq.myvtg.model;

import com.mq.myvtg.model.cache.ModelCacheExt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuatNC on 7/1/2017.
 */
public class ModelAllApplications extends ModelCacheExt {

    public List<AdsBanner> adBanner = new ArrayList<>();
    public List<App> apps = new ArrayList<>();

    public class AdsBanner {
        public String adImgUrl;
        public String sourceLink;
    }

    public class App extends ModelBase {
        public String id;

        public String shortDes;

        public String iconUrl;

        public String iosLink;

        public String androidLink;

        public String fullDes;


    }
}
