package org.mardep.ssrs.dao.seafarer.impl.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:jpaDao-test.xml")
public class DnItemAtcTest {

    @Autowired
    IDemandNoteItemDao dnItemDao;

    @Test
    public void testGetOutstandingDn(){
        List<String> imoList = new ArrayList<>(); 
        imoList.add("9845726");
        imoList.add("9813060");
            
        List<Object[]> items = dnItemDao.getOutstandingDn(imoList);
        System.out.println(items.size());
    }
}