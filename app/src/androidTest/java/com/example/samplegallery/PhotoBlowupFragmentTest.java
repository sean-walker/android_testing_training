package com.example.samplegallery;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.example.samplegallery.Utilities.VolleyRequestQueue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Set;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.not;

/**
 * Created by Sean Walker on 1/29/17.
 */


// test photo blowup synchronization
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PhotoBlowupFragmentTest {
    // disable the volley queue before the activity is launched to make sure that we may test
    // the behaviour of the test both before and after the data has been pulled.
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                    VolleyRequestQueue
                            .getInstance(InstrumentationRegistry.getTargetContext().getApplicationContext())
                            .getRequestQueue()
                            .stop();
                }
            };
    @Test
    public void photoBlowupTest() throws Exception {
        // register our volley request queue as an idling resource and start it.
        VolleyQueueIdlingResource vqidr = new VolleyQueueIdlingResource("Volley Queue");
        VolleyRequestQueue.getInstance(null).getRequestQueue().start();
        Espresso.registerIdlingResources(vqidr);

        // navigate to an album
        onData(anything())
                .inAdapterView(withId(R.id.albums_list_view))
                .atPosition(0)
                .perform(click());

        // attempt to click on a photo to blow it up
        onData(anything())
                .inAdapterView(withId(R.id.gridview))
                .atPosition(0)
                .perform(click());

        // the description, title, and photo itself should all be visible together
        onView(withId(R.id.photo_blowup))
                .check(matches(isDisplayed()));
        onView(withResourceName("photo_description"))
                .check(matches(isDisplayed()));
        onView(withResourceName("photo_title"))
                .check(matches(isDisplayed()));
        // and the progress bar should be hidden
        onView(withId(R.id.progress_bar_blowup))
                .check(matches(not(isDisplayed())));
    }

    private class VolleyQueueIdlingResource implements IdlingResource {
        private Field mCurrentRequests;
        private String resName;
        private volatile ResourceCallback resourceCallback;

        VolleyQueueIdlingResource(String resourceName) throws NoSuchFieldException {
            resName = resourceName;
            mCurrentRequests = RequestQueue.class.getDeclaredField("mCurrentRequests");
            mCurrentRequests.setAccessible(true);
        }

        @Override
        public String getName() {
            return resName;
        }

        @Override
        public boolean isIdleNow() {
            try {
                synchronized (mCurrentRequests.get(
                        VolleyRequestQueue.getInstance(null).getRequestQueue()
                )) {
                    @SuppressWarnings("unchecked")
                    Set<Request> requests = (Set<Request>) mCurrentRequests.get(
                            VolleyRequestQueue.getInstance(null).getRequestQueue()
                    );

                    if (requests.size() == 0) {
                        if(resourceCallback != null) {
                            resourceCallback.onTransitionToIdle();
                        }

                        return true;
                    }

                    return false;
                }
            } catch (IllegalAccessException e) {
                Log.e("AlbumsFragmentTest", "Cannot access request queue variable!");
                e.printStackTrace();
                System.exit(-1);
            }

            return false;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            resourceCallback = callback;
        }
    }
}
