package com.oppein.chimney.core;

/**
 * @author Ido
 * @date 2020/11/23 13:27
 */
public class AppTest {
    public static void main(String[] args) {
        SyncDataTask syncDatatask2 = new SyncDataTask(new MockTransformer(), new MockDataExtractor(), "test",5);
        syncDatatask2.startSync();

    }
}
