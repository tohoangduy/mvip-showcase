package com.mq.myvtg.model.cache;

import com.mq.myvtg.model.ModelNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen.dang.tho on 7/7/2017.
 */

public class ModelNewsCache extends ModelCacheExt {
    public List<ModelNews> listNews = new ArrayList<>();
    public int currentPage;
    public boolean needLoadMore;
}
