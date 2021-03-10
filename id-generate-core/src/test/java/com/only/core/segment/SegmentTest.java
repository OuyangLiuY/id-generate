package com.only.core.segment;

import com.generate.core.segment.bean.Segment;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SegmentTest {

    private Segment segment;

    @Before
    public void init(){
        segment = new Segment();
        segment.setCurId(0);
        segment.setMinId(0);
        segment.setMaxId(100);
    }
    @Test
    public void singleTest(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 11; i++) {
            System.out.println(segment.getNextId(10));
        }
    }

    @Test
    public void multTest()  {
        ExecutorService service = Executors.newFixedThreadPool(11);
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 11; i++) {
            service.execute(()->{
                System.out.println(segment.getNextId(10));
            });
            //latch.countDown();
        }
        try {
           //latch.await();
            service.shutdown();
            service.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("完成");
    }
}
