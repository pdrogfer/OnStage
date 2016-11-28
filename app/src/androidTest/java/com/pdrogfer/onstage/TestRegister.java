package com.pdrogfer.onstage;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.pdrogfer.onstage.ui.PresentationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestRegister {

    @Rule
    public ActivityTestRule<PresentationActivity> activityTestRule =
            new ActivityTestRule<>(PresentationActivity.class);

    @Test
    public void testRegisterSuccessful() {

    }
}
