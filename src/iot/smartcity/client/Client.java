package iot.smartcity.client;

import iot.smartcity.client.citygraph.GraphHelper;
import iot.smartcity.client.db.QueryHandlerClient;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Client extends CoapClient {

    private ArrayList<String> humiditySensors;
    private ArrayList<String> tempSensors;
    private ArrayList<String> pm10Sensors;
    private ArrayList<String> pm25Sensors;
    private ArrayList<String> trafficSensors;
    private Map<String, Integer> storeSensors;
    private final String ID;
    private int location;
    private final GraphHelper graphHl;
    private static final String BASEURI = "0.0.0.0:5683/";
    private static final String BANNER = "**********************\n" +
                                         "*  SmartCity CLIENT  *\n" +
                                         "**********************\n";

    public Client(int[][] cityMatrix, String ID){
        this.graphHl = new GraphHelper(cityMatrix);
        this.ID = ID;
        initClient();
    }

    private void initClient() {
        System.out.println(BANNER);
        QueryHandlerClient handler = new QueryHandlerClient();
        handler.setConnection();
        humiditySensors = handler.humiditySensorsList();
        tempSensors = handler.temperatureSensorsList();
        pm10Sensors = handler.pm10SensorsList();
        pm25Sensors = handler.pm25SensorsList();
        trafficSensors = handler.trafficSensorsList();
        storeSensors = handler.storeSensorsList();
        handler.closeConnection();
    }

    // Resource observer
    public void observePMResource(String res)  {
        setURI(BASEURI + res);
       observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                try {
                    JSONObject obj = new JSONObject(coapResponse.getResponseText());
                    if (obj.getString("risk").equals("Danger")) {
                        System.err.println("DANGER: ");
                        System.out.println("high PM level at " + res);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError() {
                System.err.println("ERROR on observing");
            }
        });
    }

    private void updateGraph() throws Exception {

        int nodeA, nodeB, weight;

        for (String trafficSensor : trafficSensors){
            setURI(BASEURI + trafficSensor);
            Request request = new Request(CoAP.Code.GET);
            CoapResponse response = advanced(request);
            JSONObject obj = new JSONObject(response.getResponseText());
            nodeA = obj.getInt("node1");
            nodeB = obj.getInt("node2");
            weight = obj.getInt("indexLevel");
            graphHl.setWeight(nodeA,nodeB,weight);
        }
    }

    /**
     * It allows to calculate best path to every store of type
     * storeType, dividing the results between low and medium
     * risk. It then returns result in a String built with bestStoreToString
     *
     * @param storeType 0-2, store type among the available ones
     * @return The string with representation of paths information
     * @throws Exception JSON exception
     */
    public String bestStore(int storeType) throws Exception {
        updateGraph();
        Map<String, Integer> lowRisk, mediumRisk;
        lowRisk = new HashMap<>();
        mediumRisk = new HashMap<>();
        graphHl.findPath(location);
        for (Map.Entry<String, Integer> storeSensor : storeSensors.entrySet()) {
            if (storeSensor.getValue() == storeType) {
                setURI("0.0.0.0:5683/" + storeSensor.getKey());
                Request request = new Request(CoAP.Code.GET);
                CoapResponse response = advanced(request);
                JSONObject obj = new JSONObject(response.getResponseText());
                if (obj.getInt("risk") < 1) {
                    lowRisk.put(storeSensor.getKey(), obj.getInt("location"));
                } else if (obj.getInt("risk") < 3) {
                    mediumRisk.put(storeSensor.getKey(), obj.getInt("location"));
                }
            }
        }
        return bestStoreToString(lowRisk, mediumRisk);
    }

    private String bestStoreToString(Map<String, Integer> lowRiskStores, Map<String, Integer> mediumRiskStores){
        StringBuilder bld = new StringBuilder();

        bld.append("\nLOW RISK STORES").append("\n").append("--------").append("\n");
        for (Map.Entry<String, Integer> lrStore : lowRiskStores.entrySet()) {
            bld.append(lrStore.getKey()).append(" | ");
            bld.append(graphHl.pathToString(lrStore.getValue()));
        }
        bld.append("\nMEDIUM RISK STORE").append("\n").append("--------").append("\n");
        for (Map.Entry<String, Integer> storeSensor : mediumRiskStores.entrySet()) {
            bld.append(storeSensor.getKey()).append(" | ");
            bld.append(graphHl.pathToString(storeSensor.getValue()));
        }
        return bld.toString();
    }

    // Place reservation
    public String reservePlaceInStore(String storeName) throws JSONException {
        if (!storeExists(storeName)){
            System.err.println("Please provide a valid store");
            return null;
        }
        JSONObject response = reservationPost(storeName, ID);
        return reservePlaceToString(response);
    }

    private String reservePlaceToString(JSONObject response) throws JSONException {
        StringBuilder builder = new StringBuilder();
        if (response.getInt("result") == 0){
            builder.append("Reservation made! ID ").
                    append(response.getString("ID"))
                    .append(" position: ").append(response.getString("queuePosition"));

        } else if (response.getInt("result") == 1) {
            builder.append("Reservation NOT possible: ").append(response.getString("moreInfo"));
        }
        return builder.toString();
    }

    // Check whether the provided store exists to be reserved
    private boolean storeExists(String store) {
        return (pm10Sensors.contains(store)     ||
                pm25Sensors.contains(store)     ||
                humiditySensors.contains(store) ||
                storeSensors.containsKey(store) ||
                tempSensors.contains(store));
    }

    private JSONObject reservationPost(String storeName, String ID) throws JSONException {
        setURI(BASEURI + storeName);
        Request post = new Request(CoAP.Code.POST);
        post.setPayload(ID);
        CoapResponse response = advanced(post);
        return new JSONObject(response.getResponseText());
    }

    /**
     * Method to get the average values for the environmental parameters
     * If one or more sensor measured a dangerous value it is printed
     * aside from the list and not considered for the average
     *
     * @return String with weather information
     */
    public String airCondition() throws JSONException {
        StringBuilder builder = new StringBuilder();
        Map<String, Integer> dangerTemp = new HashMap<>();
        Map<String, Integer> dangerPm10 = new HashMap<>();
        Map<String, Integer> dangerPm25 = new HashMap<>();
        Map<String, Integer> hum = new HashMap<>();
        double avgTemp = getTempAvg(dangerTemp);
        double avgPm10 = getPmAvg(dangerPm10, "PM10");
        double avgPm25 = getPmAvg(dangerPm25, "PM25");
        builder.append(sensorToString(avgPm10, dangerPm10, "PM10"))
        .append(sensorToString(avgPm25, dangerPm25, "PM25"))
        .append(sensorToString(avgTemp, dangerTemp, "TEMP"))
        .append(sensorToString(getHumidityAvg(), hum, "HUM"));
        return builder.toString();
    }

    private double getTempAvg(Map<String, Integer> danger) throws JSONException {
        int totalTmp = 0, count = 0;
        for (String tempSensor : tempSensors){
            setURI(BASEURI + tempSensor);
            Request request = new Request(CoAP.Code.GET);
            CoapResponse response = advanced(request);
            JSONObject obj = new JSONObject(response.getResponseText());
            if (obj.getString("risk").equals("Danger")){
                danger.put(tempSensor, obj.getInt("temperature"));
                continue;
            }
            totalTmp += obj.getInt("temperature");
            count ++;
        }
        if (count == 0) {
            return 0;
        }
        return Math.round(((totalTmp/(double) count)*100)/100);
    }

    private double getPmAvg(Map<String, Integer> danger, String type) throws JSONException {
        ArrayList<String> list = new ArrayList<>();
        String jsonKey = " ";
        if (type.equals("PM10")) {
            list = pm10Sensors;
            jsonKey = "PM10";
        }
        if (type.equals("PM25")){
            list = pm25Sensors;
            jsonKey = "PM25";
        }

        int totalPm10 = 0, count = 0;
        for (String sensor : list){
            setURI(BASEURI + sensor);
            Request request = new Request(CoAP.Code.GET);
            CoapResponse response = advanced(request);
            JSONObject obj = new JSONObject(response.getResponseText());
            if (obj.getString("risk").equals("Danger")){
                danger.put(sensor, obj.getInt(jsonKey));
                continue;
            }
            totalPm10 += obj.getInt(jsonKey);
            count ++;
        }
        if (count == 0) {
            return 0;
        }
        return Math.round(((totalPm10/(double) count)*100))/100.0;
    }

    private double getHumidityAvg() throws JSONException {
        int totalHum = 0, count = 0;
        for (String humSensor : humiditySensors){
            setURI(BASEURI + humSensor);
            Request request = new Request(CoAP.Code.GET);
            CoapResponse response = advanced(request);
            JSONObject obj = new JSONObject(response.getResponseText());
            totalHum += obj.getInt("humidity");
            count ++;
        }
        if (count == 0) {
            return 0;
        }
        return Math.round(((totalHum/(double) count)*100)/100);
    }

    private String sensorToString(double avg, Map<String, Integer> danger, String type) {
        StringBuilder builder = new StringBuilder();
        if (avg != 0) {
            builder.append("\n >> 2").append(type).append("\n-----\n").append("Average: "). append(avg);
        }
        if (!(danger.isEmpty())) {
            builder.append("\n\nDANGER\n");
            for (Map.Entry<String, Integer> dang : danger.entrySet() ) {
                builder.append(dang.getKey()).
                        append(" measured ").
                        append(dang.getValue()).append("\n");
            }
        } else {
            builder.append("\nNo danger measured\n\n");
        }
        return builder.toString();
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
