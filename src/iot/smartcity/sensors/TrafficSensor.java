package iot.smartcity.sensors;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TrafficSensor extends Sensor {
	
	private final static String type = "TRA";
	private final int node1, node2;
	private int trafficLevel;

	public TrafficSensor(String name, int node1, int node2) {
		super(type, name);
		this.node1 = node1;
		this.node2 = node2;
		Timer timer = new Timer();
		timer.schedule(new setTrafficLevel(), 0, 5000);
	}

	private JSONObject generateJson(String level) throws JSONException {

		JSONObject ans = new JSONObject();
		ans.put("node1", node1);
		ans.put("node2", node2);
		ans.put("level", level);
		ans.put("indexLevel", trafficLevel);

		return ans;
	}

	public JSONObject getJson() throws JSONException{
		String level = defineTraffic();
		return generateJson(level);

	}

	private String defineTraffic() {
		switch (trafficLevel) {
			case 0 :
				return "Road closed";
			case 2:
				return "No traffic";
			case 4:
				return "Low traffic";
			case 6:
				return "Medium traffic";
			case 8:
				return "High traffic";
		}

		return "No information available";
	}

	private class setTrafficLevel extends TimerTask {
		@Override
		public void run() {
			trafficLevel = new Random().nextInt(5) * 2;
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
