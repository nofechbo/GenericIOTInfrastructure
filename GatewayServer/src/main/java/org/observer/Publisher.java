package org.observer;

public class Publisher {
    private final Dispatcher<String> dispatcher = new Dispatcher<>();

    public void registerCallback(Callback<String> callback) {
        dispatcher.subscribe(callback);
    }
    public void unregisterCallback(Callback<String> callback) {
        dispatcher.unsubscribe(callback);
    }

    public void notifyAllSub(String data){
        dispatcher.publish(data);
    }

    public void stopService(){
        dispatcher.stopService();
    }
}
