package com.pinyougou.search.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
           Long[] goodIds = (Long[]) objectMessage.getObject();
           itemSearchService.deleteByGoodIds(Arrays.asList(goodIds));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
