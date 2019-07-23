package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /*
     * 根据关键字查找
     * */
    public Map<String,Object> search(Map searchMap);


    public void importList(List list);


    public void deleteByGoodIds(List goodIds);
}
