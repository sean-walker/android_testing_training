package com.example.samplegallery.Utilities;

import android.content.Context;
import android.util.DisplayMetrics;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by Sean Walker on 1/28/17.
 */

public class LruBitmapCacheTest {

    @Mock
    Context mMockContext;

    @Test
    public void getCacheSizeTest() {

        // mock display metrics for the test
        DisplayMetrics mockMetrics = new DisplayMetrics();
        mockMetrics.widthPixels = 3;
        mockMetrics.heightPixels = 3;

        // mock chained events
        mMockContext = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
        when(mMockContext.getResources().getDisplayMetrics()).thenReturn(mockMetrics);

        // unit test with an assertion
        // bytes = width * height * 4 * 3
        assertThat(LruBitmapCache.getCacheSize(mMockContext), is(3 * 3 * 4 * 3));
    }

}
