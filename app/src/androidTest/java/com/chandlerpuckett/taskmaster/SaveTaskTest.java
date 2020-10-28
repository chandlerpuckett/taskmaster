package com.chandlerpuckett.taskmaster;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SaveTaskTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void saveTaskTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.addTask), withText("Add Task"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.titleInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.textInputLayout2),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("Grocery Shopping"), closeSoftKeyboard());


        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_addTask), withText("Add Task"),
                        childAtPosition(
                                allOf(withId(R.id.myCoordinatorLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());

        pressBack();

        ViewInteraction textView = onView(
                allOf(withId(R.id.recTitle), withText("Grocery Shopping"),
                        withParent(allOf(withId(R.id.taskFrag),
                                withParent(withId(R.id.homeRecyclerView)))),
                        isDisplayed()));
        textView.check(matches(withText("Grocery Shopping")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.recState), withText("new"),
                        withParent(allOf(withId(R.id.taskFrag),
                                withParent(withId(R.id.homeRecyclerView)))),
                        isDisplayed()));
        textView3.check(matches(withText("new")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.recState), withText("new"),
                        withParent(allOf(withId(R.id.taskFrag),
                                withParent(withId(R.id.homeRecyclerView)))),
                        isDisplayed()));
        textView4.check(matches(withText("new")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
