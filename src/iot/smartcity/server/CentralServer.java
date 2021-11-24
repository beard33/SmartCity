package iot.smartcity.server;

import iot.smartcity.server.db.QueryHandler;
import iot.smartcity.sensors.*;
import org.eclipse.californium.core.CoapServer;

import java.util.ArrayList;

public class CentralServer extends CoapServer {

    private ArrayList<HumiditySensor> humiditySensors;
    private ArrayList<TemperatureSensor> tempSensors;
    private ArrayList<Pm10sensor> pm10Sensors;
    private ArrayList<Pm25sensor> pm25Sensors;
    private ArrayList<TrafficSensor> trafficSensors;
    private ArrayList<StoreSensor> storeSensors;
    private final QueryHandler handler;
    private final static String banner = "**********************\n" +
                                         "*   SERVER RUNNING   *\n" +
                                         "**********************\n";

    protected CentralServer(){
        this.humiditySensors = new ArrayList<>();
        this.tempSensors = new ArrayList<>();
        this.pm10Sensors = new ArrayList<>();
        this.pm25Sensors = new ArrayList<>();
        this.trafficSensors = new ArrayList<>();
        this.storeSensors = new ArrayList<>();
        this.handler = new QueryHandler();
    }

    protected void initServer(){
        handler.setConnection();
        getAirSensors();
        getStoreSensors();
        getTrafficSensors();
        addSensors();
        handler.closeConnection();
        this.start();
        System.out.println(banner);
    }

    private void addSensors(){
        for (HumiditySensor tmpS : humiditySensors) {
            this.add(tmpS);
        }
        for (TemperatureSensor tmpT : tempSensors){
            this.add(tmpT);
        }
        for (Pm10sensor tmpP10 : pm10Sensors){
            this.add(tmpP10);
        }
        for (Pm25sensor tmpP25 : pm25Sensors) {
            this.add(tmpP25);
        }
        for (TrafficSensor tmpTr : trafficSensors){
            this.add(tmpTr);
        }
        for (StoreSensor tmpStr : storeSensors){
            this.add(tmpStr);
        }
    }

    private void getAirSensors(){
        humiditySensors = handler.humiditySensorsList();
        tempSensors = handler.temperatureSensorsList();
        pm10Sensors = handler.Pm10SensorsList();
        pm25Sensors = handler.Pm25SensorsList();
    }

    private void getTrafficSensors(){
        trafficSensors = handler.trafficSensorsList();
    }

    private void getStoreSensors(){
        storeSensors = handler.storeSensorsList();
    }

}
