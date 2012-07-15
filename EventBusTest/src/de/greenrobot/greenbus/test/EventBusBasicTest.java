/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.greenbus.test;

import junit.framework.TestCase;
import android.app.Activity;
import android.util.Log;
import de.greenrobot.event.EventBus;

/**
 * @author Markus Junginger, greenrobot
 */
public class EventBusBasicTest extends TestCase {

    private EventBus eventBus;
    private String lastStringEvent;
    private int countStringEvent;
    private int countIntEvent;
    private int lastIntEvent;

    protected void setUp() throws Exception {
        super.setUp();
        eventBus = new EventBus();
    }

    public void testRegisterForEventClassAndPost() {
        // Use an activity to test real life performance
        TestActivity testActivity = new TestActivity();
        String event = "Hello";

        long start = System.currentTimeMillis();
        eventBus.register(testActivity, String.class);
        long time = System.currentTimeMillis() - start;
        Log.d(EventBus.TAG, "Registered for event class in " + time + "ms");

        eventBus.post(event);

        assertEquals(event, testActivity.lastStringEvent);
    }

    public void testRegisterAndPost() {
        // Use an activity to test real life performance
        TestActivity testActivity = new TestActivity();
        String event = "Hello";

        long start = System.currentTimeMillis();
        eventBus.register(testActivity);
        long time = System.currentTimeMillis() - start;
        Log.d(EventBus.TAG, "Registered in " + time + "ms");

        eventBus.post(event);

        assertEquals(event, testActivity.lastStringEvent);
    }

    public void testPostWithoutSubscriber() {
        eventBus.post("Hello");
    }

    public void testUnregisterWithoutRegister() {
        // Results in a warning without throwing
        eventBus.unregister(this);
        eventBus.unregister(this, String.class);
    }

    public void testRegisterTwice() {
        eventBus.register(this, String.class);
        try {
            eventBus.register(this, String.class);
            fail("Did not throw");
        } catch (RuntimeException expected) {
            // OK
        }
    }

    public void testPostWithTwoSubscriber() {
        EventBusBasicTest test2 = new EventBusBasicTest();
        eventBus.register(this, String.class);
        eventBus.register(test2, String.class);
        String event = "Hello";
        eventBus.post(event);
        assertEquals(event, lastStringEvent);
        assertEquals(event, test2.lastStringEvent);
    }

    public void testPostMultipleTimes() {
        eventBus.register(this, String.class);
        String event = "Hello";
        int count = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            eventBus.post(event);
        }
        long time = System.currentTimeMillis() - start;
        Log.d(EventBus.TAG, "Posted " + count + " events in " + time + "ms");
        assertEquals(event, lastStringEvent);
        assertEquals(count, countStringEvent);
    }

    public void testPostAfterUnregister() {
        eventBus.register(this, String.class);
        eventBus.unregister(this, String.class);
        eventBus.post("Hello");
        assertNull(lastStringEvent);
    }

    public void testPostAfterUnregisterForAllEventClasses() {
        eventBus.register(this, String.class);
        eventBus.unregister(this);
        eventBus.post("Hello");
        assertNull(lastStringEvent);
    }

    public void onEvent(String event) {
        lastStringEvent = event;
        countStringEvent++;
    }

    public void onEvent(int event) {
        lastIntEvent = event;
        countIntEvent++;
    }

    static class TestActivity extends Activity {

        public String lastStringEvent;

        public void onEvent(String event) {
            lastStringEvent = event;
        }

    }

}
