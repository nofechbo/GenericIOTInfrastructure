package org.plugandplay;

import org.command.Command;
import org.factory.Factory;
import org.parser.RPSParser;
import org.parser.StringParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import static java.lang.Thread.sleep;

public class PlugAndPlayTest {

    private PlugAndPlay plugAndPlay;
    public Factory<String, Map<String, String>, Command> factory;

    @BeforeEach
    public void setUp() throws ClassNotFoundException {
        factory = new Factory<>();
        plugAndPlay = new PlugAndPlay("/home/nofech/dev/java/projects/src/il/co/ilrd/command", factory);
    }


    @Test
    public void testStartAndStopCalls() {
        plugAndPlay.start();

        // Call stop
        plugAndPlay.stop();
    }

    @Test
    public void testLoadCommandFromJar() throws Exception {
        plugAndPlay.start();

        try {
            sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

          for (String key : factory.creators.keySet()){
             System.out.println(key);
         }

        RPSParser<Map<String, String>, String> parser = new StringParser();
        Map<String, String> requestArgs = parser.parse("org.RegCompany&companyName@Infinity");
        factory.create(requestArgs.get(null), requestArgs);

        plugAndPlay.stop();
    }
}
