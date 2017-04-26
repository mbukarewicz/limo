package com.mutunus.tutunus.structures;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Mmm on 14.03.2017.
 */
public class MTDateTest {

    @Test
    public void test1() throws Exception {
        final MTDate d1 = new MTDate(2000, 02, 01);
        final MTDate d2 = new MTDate(2001, 02, 01);

        Assert.assertTrue(d1.isLt(d2));
        Assert.assertTrue(d2.isGte(d1));
    }

}