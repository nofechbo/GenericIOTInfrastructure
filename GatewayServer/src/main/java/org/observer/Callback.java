package org.observer;

import java.util.function.Consumer;

public class Callback<T> {
    private Dispatcher<T> dispatcher;
    private final Consumer<T> func;
    private final Runnable stopService;

    public Callback (Consumer<T> func, Runnable stopService) {
        dispatcher = null;
        this.func = func;
        this.stopService = stopService;
    }

    public void update(T data){
        func.accept(data);
    }
    public void unsubscribe(){
        dispatcher.unsubscribe(this);
    }

    public void setDispatcher(Dispatcher<T> dispatcher){
        this.dispatcher = dispatcher;
    }

    public void stopService() {
        stopService.run();
        dispatcher = null;
    }
}
