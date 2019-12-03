package com.mq.myvtg.model;

import com.mq.myvtg.model.cache.ModelCacheExt;
import com.mq.myvtg.util.Const;

import java.util.List;

/**
 * Created by LuatNC on 7/11/2017.
 */

public class ModelAppConfig extends ModelCacheExt {
    public List<FunctItem> home;
    public List<GroupFunct> utilities;
    public List<FunctItem> more;
    public List<EmptyPage> emptyPages;

    public String getEmptyPageLink(String pageName, String language) {
        if (emptyPages == null || emptyPages.isEmpty()) {
            return "";
        }

        for (EmptyPage page : emptyPages) {
            if (page.name != null && page.name.equalsIgnoreCase(pageName))
                if (language.equalsIgnoreCase(Const.LANG_EN)) {
                    return page.enLink;
                } else {
                    return page.localLink;
                }
        }

        return "";
    }

    public class GroupFunct {
        public String enName;
        public String localName;
        public List<FunctItem> items;
    }

    public class FunctItem {
        public Boolean isEnable;
        public int isActive;//0 - disable, 1 - chỉ enable cho thuê bao trả trước, 2 - chỉ enable cho thuê bao trả sau, 3 - enable cho cả trả sau và trả trước.
        public String localMsg;
        public String enMsg;
        public String enTitle;
        public String localTitle;
        public String selectedIconUrl;
        public String normalIconUrl;
        public int functType; // 0: Ultilities, 1: VAS
        public FunctionUri uri; // function's name or VAS service code

        public String getTitile(String language) {
            if (language.startsWith("e")) {
                return enTitle;
            } else {
                return localTitle;
            }
        }
    }

    public class FunctionUri {
        public String enTitle;
        public String localTitle;
        public String serviceCode;
        public String functionName;

        public String getTitile(String language) {
            if (language.equalsIgnoreCase(Const.LANG_EN)) {
                return enTitle;
            } else {
                return localTitle;
            }
        }
    }

    public class EmptyPage {
        public String name;
        public String enLink;
        public String localLink;
    }
}
