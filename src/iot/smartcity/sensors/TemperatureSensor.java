package iot.smartcity.sensors;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureSensor extends Sensor{

    private final static double warningThreshold = 30;
    private final static double dangerThreshold = 37;
    private final static String type = "TMP";
    private int temperature;

    public TemperatureSensor(String name) {
        super(type, name);
        Timer timer = new Timer();
        timer.schedule(new setTemperature(), 0, 5000);
    }

    private JSONObject checkRisk() throws JSONException{
        if (temperature >= warningThreshold && temperature < dangerThreshold) {
            return generateJson("Warning");
        }
        if (temperature >= dangerThreshold) {
            System.out.print(LocalDateTime.now() + "\tDANGEROUS TEMPERATURE at " + getName());
            return generateJson("Danger");
        }

        return generateJson("OK");
    }

    private JSONObject generateJson(String risk) throws JSONException {
        JSONObject ans = new JSONObject();

        ans.put("temperature", temperature);
        ans.put("risk", risk);

        return ans;
    }

    private class setTemperature extends TimerTask {
        @Override
        public void run() {
            temperature = (new Random().nextInt(38-29)+29);
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            exchange.respond(CoAP.ResponseCode.CONTENT, checkRisk().toString(),
                    MediaTypeRegistry.APPLICATION_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
