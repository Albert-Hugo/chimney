package com.oppein.chimney.core;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Ido
 * @date 2020/11/17 13:29
 */
public class MockDataExtractor extends DataExtractor {

    @Override
    public Page getResultPage(int pageNo) {
        Page page = new MockPage();
        if (pageNo > 100) {
            ((MockPage) page).setRecordList(Collections.emptyList());
            return page;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((MockPage) page).setRecordList(Arrays.asList("sf", "fs"));
        return page;
    }
}
