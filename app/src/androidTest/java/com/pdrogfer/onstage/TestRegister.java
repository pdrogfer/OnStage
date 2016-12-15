package com.pdrogfer.onstage;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestRegister {

    @Rule
    public ActivityTestRule<RegisterActivity> activityTestRule =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void testRegisterSuccessful() {
        // TODO: 29/11/2016 implement register and un-register tests
    }
}
