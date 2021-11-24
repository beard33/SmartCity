package iot.smartcity.sensors;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class  Pm10sensor extends Sensor {

	private final static String type = "PM10";
	private final static int warningThreshold= 40;
	private final static int dangerThreshold = 80;
	private double pmLevel;

	public Pm10sensor(String name) {
		super(type, name);
		this.setObservable(true);
		this.getAttributes().setObservable();
		Timer timer = new Timer();
		timer.schedule(new levelChanged(this), 0, 5000);
	}

	private JSONObject checkRisk() throws JSONException{
		
		if (pmLevel >= warningThreshold && pmLevel < dangerThreshold) {
			return generateJson("Warning");
		}
		if (pmLevel >= dangerThreshold) {
			System.out.print(LocalDateTime.now() + "\tDANGEROUS PM10 at " + getName());
			return generateJson("Danger");
		}
		
		return generateJson("OK");
	}
	
	
	private JSONObject generateJson(String risk) throws JSONException {
		
		JSONObject ans = new JSONObject();
		
		ans.put("PM10", pmLevel);
		ans.put("risk", risk);

		return ans;
	}

	private class levelChanged extends TimerTask {
		private final CoapResource mcoapRes;
		public levelChanged(CoapResource coapRes){
			mcoapRes = coapRes;
		}
		@Override
		public void run() {
			pmLevel = new Random().nextInt(85-30)+30;
			mcoapRes.changed();
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
