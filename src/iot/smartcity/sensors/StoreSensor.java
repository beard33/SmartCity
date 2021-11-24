package iot.smartcity.sensors;

import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class StoreSensor extends Sensor {

	private final double sqMeters;
	private int pplCount = 0;
	private final int storeType;
	private final int location;
	private final static String type ="STO";
	private static final int MAXRES = 5;
	private final ArrayList<String> reservations;

	
	public StoreSensor(String name, int sType ,double sqM, int location) {
		super(type, name);
		this.sqMeters = sqM;
		this.storeType = sType;
		this.location = location;
		this.reservations = new ArrayList<>();
		Timer timer = new Timer();
		timer.schedule(new updateCount(), 0, 5000);
	}
	
	private double calculateDensity() {
		return (double)pplCount/sqMeters;
	}
	
	private int getRisk(double density) {
		if (density <= 0.25) {
			return 0;
		} else if (density <= 0.6) {
			return 1;
		}			
		return 2;
	}

	private JSONObject generateJson(int risk) throws JSONException {
		double tmpDensity = calculateDensity();

		JSONObject obj = new JSONObject();
		obj.put("storeType", storeType);
		obj.put("location", location);
		obj.put("density", tmpDensity);
		obj.put("risk", risk);
		
		return obj;
	}

	private JSONObject getJson() throws JSONException {
		double density = calculateDensity();
		int risk = getRisk(density);
		return generateJson(risk);
	}


	private JSONObject postJson(int result, String reservation, int queuePos, String more) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("result", result);
		obj.put("ID", reservation);
		obj.put("queuePosition", queuePos);
		obj.put("moreInfo", more);
		return obj;
	}

	private void personEnter(int entered) {
		if (calculateDensity() < 0.6 ) {
			pplCount += entered;
		}
	}
	private void personExit(int exited){
		if (pplCount > 0) {
			pplCount -= exited;
		}
	}

	private boolean reservationAvailable() {
		return reservations.size() < MAXRES;
	}
	private boolean alreadyReserved(String res) {return reservations.contains(res);}

	private class updateCount extends TimerTask {
		@Override
		public void run() {
			int count = new Random().nextInt(10);
			int inOut = new Random().nextInt();
			if (inOut % 2 == 0 && pplCount >= count){
				personExit(count);
			} else {
				if (calculateDensity() <= 0.6) {
					personEnter(count);
				}
			}
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

	@Override
	public void handlePOST(CoapExchange exchange) {
		String reservation = exchange.getRequestText();
		boolean ok = true;
		String IDPattern = "[a-zA-Z]{5}";
		int result = 1, queuePos = -1;
		String moreInfo = "";

		try {
			if (alreadyReserved(reservation)){
				moreInfo = "Already reserved";
				queuePos = reservations.indexOf(reservation);
				ok = false;
			}
			if (!Pattern.matches(IDPattern, reservation)){
				moreInfo = "ID not valid";
				ok = false;
			}
			if (getRisk(calculateDensity()) == 2){
				moreInfo = "Too crowded";
				ok = false;
			}
			if (!reservationAvailable()) {
				ok = false;
				moreInfo = "Max reservations reached";
			}
			if (ok) {
				moreInfo = "OK";
				result = 0;
				reservations.add(reservation);
				queuePos = reservations.size();
			}

			exchange.respond(CoAP.ResponseCode.CONTENT,
					postJson(result, reservation, queuePos, moreInfo).toString(),
					MediaTypeRegistry.APPLICATION_JSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
