package com.oppein.chimney.core;

import java.util.List;

/**
 * @author Ido
 * @date 2020/11/17 13:32
 */
public class MockTransformer extends Transformer {
    @Override
    public void transformData(List toBeTransformedData) {
        try {
            System.out.println("transforming data ");
            Thread.sleep(1000);
            System.out.println("transform data finish ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
