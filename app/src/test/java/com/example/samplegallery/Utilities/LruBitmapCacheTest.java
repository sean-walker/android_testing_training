package com.example.samplegallery.Utilities;

import android.content.Context;
import android.util.DisplayMetrics;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Sean Walker on 1/28/17.
 */

public class LruBitmapCacheTest {

    @Mock
    Context mMockContext;

    @Test
    public void getCacheSizeTest() throws Exception {

        // mock display metrics for the test
        DisplayMetrics mockMetrics = new DisplayMetrics();
        mockMetrics.widthPixels = 3;
        mockMetrics.heightPixels = 3;

        // mock chained events
        mMockContext = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
        when(mMockContext.getResources().getDisplayMetrics()).thenReturn(mockMetrics);

        // getCacheSize() is now private, we must access it with reflection
        Method method = LruBitmapCache.class.getDeclaredMethod("getCacheSize", Context.class);
        method.setAccessible(true);

        // unit test with an assertion
        // bytes = width * height * 4 * 3
        assertThat(method.invoke(LruBitmapCache.class, mMockContext), CoreMatchers.<Object>is(3 * 3 * 4 * 3));
    }

}
