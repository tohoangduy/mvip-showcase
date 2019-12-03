package com.mq.myvtg.model.cache;

import com.mq.myvtg.model.ModelPromotion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen.dang.tho on 7/7/2017.
 */

public class ModelOffersCache extends ModelCacheExt {
    public List<ModelPromotion> listPromotion = new ArrayList<>();
    public int currentPage;
    public boolean needLoadMore;
}
