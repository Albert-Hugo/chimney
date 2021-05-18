package com.oppein.chimney.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ido
 * @date 2020/11/23 11:21
 */
public class MockPage  implements Page {
    public MockPage() {
    }
    private List result = new ArrayList();

    public void setRecordList(List list){
        result = list;
    }

    @Override
    public List getRecords() {
        return result;
    }
}
