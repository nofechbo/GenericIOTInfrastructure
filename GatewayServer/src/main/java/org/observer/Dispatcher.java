package org.observer;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher<T> {
    private final List<Callback<T>> callbacks = new ArrayList<>();

    public void publish(T data){
        for (Callback<T> callback : callbacks) {
            callback.update(data);
        }
    }
    public void subscribe(Callback<T> callback){
        callback.setDispatcher(this);
        callbacks.add(callback);
    }
    public void unsubscribe(Callback<T> callback){
        callbacks.remove(callback);
        callback.setDispatcher(null);
    }
    public void stopService(){
        for (Callback<T> callback : callbacks) {
            callback.stopService();
        }
        callbacks.clear();
    }
}
