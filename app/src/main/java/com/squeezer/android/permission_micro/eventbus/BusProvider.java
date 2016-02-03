package com.squeezer.android.permission_micro.eventbus;

import de.greenrobot.event.EventBus;

/**
 * Created by adnen on 1/18/16.
 */
public class BusProvider {

    private static final EventBus BUS = new EventBus();

    public static EventBus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
