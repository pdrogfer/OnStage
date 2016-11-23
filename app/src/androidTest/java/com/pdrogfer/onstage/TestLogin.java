package com.pdrogfer.onstage;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.pdrogfer.onstage.ui.PresentationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.pdrogfer.onstage.R.string.menu_logout;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestLogin {

    // start all tests by opening PresentationActivity
    @Rule
    public ActivityTestRule<PresentationActivity> activityTestRule =
            new ActivityTestRule<>(PresentationActivity.class);

    @Test
    public void testLoginSuccessful() {
        // open Login Activity
        onView(withId(R.id.btn_goto_login)).perform(click());
        onView(withId(R.id.title_login_activity)).check(matches(isDisplayed()));

        // login
        onView(withId(R.id.field_email_login)).perform(typeText("testuser@hotmail.com"));
        onView(withId(R.id.field_password_login)).perform(typeText("aaaaaa"));
        onView(withId(R.id.btn_login_login)).perform(click());

        // check GigList opens
        onView(withId(R.id.recycler_view_gigs)).check(matches(isDisplayed()));

        // then log Out
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.menu_logout)).perform(click());
        onView(withId(R.id.title_presentation_activity)).check(matches(isDisplayed()));
    }
}
