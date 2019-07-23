package com.pinyougou.page.service;

import java.io.IOException;

public interface ItemPageService {
    /*
    * 商品详细页
    *
    * */

    public boolean genItemHtml(Long goodsId);


    public boolean deleteItemHtml(Long[]goodsIds);
}
