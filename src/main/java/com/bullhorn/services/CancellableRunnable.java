package com.bullhorn.services;

public interface CancellableRunnable extends Runnable {
    public void cancel();
}
