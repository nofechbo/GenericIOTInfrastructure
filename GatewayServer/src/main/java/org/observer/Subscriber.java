package org.observer;


public class Subscriber {
    private final Callback<String> callback = new Callback<>(this::getUpdate, this::stopUpdates);
    private final StringBuilder result = new StringBuilder();

    private void getUpdate(String data) {
        result.append(data);
    }
    private void stopUpdates() {
        result.append("Stopped");
    }

    public void unregister(){
       callback.unsubscribe();
    }
    public void register(Publisher publisher) {
        publisher.registerCallback(callback);
    }

    public StringBuilder getResult() {
        return result;
    }
}
