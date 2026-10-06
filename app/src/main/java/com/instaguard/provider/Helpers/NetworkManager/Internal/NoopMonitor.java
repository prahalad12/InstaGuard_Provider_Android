package com.instaguard.provider.Helpers.NetworkManager.Internal;

import com.instaguard.provider.Helpers.NetworkManager.Monitor;

public class NoopMonitor implements Monitor {
    @Override
    public void onStart() {
        //no-op
    }

    @Override
    public void onStop() {
        //no-op
    }
}
