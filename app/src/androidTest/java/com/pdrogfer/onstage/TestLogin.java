package com.pdrogfer.onstage;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anyOf;

@RunWith(AndroidJUnit4.class)
public class TestLogin {

    @Rule
    public ActivityTestRule<LogInActivity> activityTestRule =
            new ActivityTestRule<>(LogInActivity.class);

    @Test
    public void testLoginFanSuccessful() {
        // log in
        onView(withId(R.id.field_email_login)).perform(typeText("testfan@hotmail.com"));
        onView(withId(R.id.field_password_login)).perform(typeText("aaaaaa"));
        onView(withId(R.id.btn_login_login)).perform(click());

        // check GigList opens
        onView(withId(R.id.recycler_view_gigs)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginMusicianSuccessful() {
        // login
        onView(withId(R.id.field_email_login)).perform(typeText("testmusician@hotmail.com"));
        onView(withId(R.id.field_password_login)).perform(typeText("aaaaaa"));
        onView(withId(R.id.btn_login_login)).perform(click());

        // check GigList opens
        onView(withId(R.id.recycler_view_gigs)).check(matches(isDisplayed()));
    }
}
