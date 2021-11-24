package iot.smartcity.sensors;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HumiditySensor extends Sensor {

    private static final String type = "HUM";
    private int humidityLevel;

    public HumiditySensor(String name) {
        super(type, name);
        Timer timer = new Timer();
        timer.schedule(new setHumidity(), 0, 5000);
    }

    private JSONObject generateJson(String level) throws JSONException{
        JSONObject ans = new JSONObject();
        ans.put("humidity", humidityLevel);
        ans.put("level", level);

        return ans;
    }

    public JSONObject getJson() throws JSONException {
        String level = defineHumidity();
        return generateJson(level);
    }

    private String defineHumidity() {
        if (humidityLevel < 0 || humidityLevel > 100) {
            return "ERROR";
        }
        if (humidityLevel < 50) {
            return "Low";
        } else if (humidityLevel < 70) {
            return "Medium";
        } else {
            return "High";
        }
    }

    private class setHumidity extends TimerTask {
        @Override
        public void run() {
            humidityLevel = new Random().nextInt(80-40)+40;
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            exchange.respond(CoAP.ResponseCode.CONTENT, getJson().toString(),
                    MediaTypeRegistry.APPLICATION_JSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
