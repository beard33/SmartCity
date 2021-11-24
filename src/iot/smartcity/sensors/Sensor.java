package iot.smartcity.sensors;

import org.eclipse.californium.core.CoapResource;

/**
 * Abstract sensor class that is extended by every
 * sensor of the city.
 */

public abstract class Sensor extends CoapResource {

	private final String sensorType;
	
	public Sensor(String type, String name) {
		super(name);
		this.sensorType = type;
	}
	
	public String getType() {
		return sensorType;
	}
	
}
