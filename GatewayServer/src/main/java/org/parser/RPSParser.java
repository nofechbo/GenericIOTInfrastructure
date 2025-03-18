package org.parser;

import com.google.gson.JsonObject;

public interface RPSParser <T, E> {

    public T parse(E str);

}
