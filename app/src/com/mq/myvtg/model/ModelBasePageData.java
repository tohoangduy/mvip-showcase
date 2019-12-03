package com.mq.myvtg.model;

public class ModelBasePageData {
    public boolean isLoaded=false;
    public boolean isBaseClass() {
        return getClass().getSimpleName().equals(ModelBasePageData.class.getSimpleName());
    }
}
