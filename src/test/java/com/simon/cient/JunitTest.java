package com.simon.cient;

import org.junit.Test;

/**
 * Created by simon on 2016/10/9.
 */
public class JunitTest {
    @Test
    public void hello(){
        long now = System.currentTimeMillis();
        System.out.println(now);
        System.out.println(1475991000000l);
        long hello = now -1475991000000l;
        System.out.println(hello);
    }
}
