package iot.smartcity.client.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to access the database from the client.
 * NB: the database from the client point of view is used
 * just as a source of knowledge (i.e. the client
 * does not retrieve sensors from the DB but just names)
 */

public class QueryHandlerClient {
    private static final String SELECT_ALL_STORE_SENSORS = "SELECT * FROM storeSensors;";
    private static final String SELECT_ALL_TRAFFIC_SENSORS = "SELECT * FROM trafficSensors;";
    private static final String SELECT_ALL_HUM_SENSORS = "SELECT * FROM airSensors WHERE TYPE = 'HUM';";
    private static final String SELECT_ALL_TEMP_SENSORS = "SELECT * FROM airSensors WHERE TYPE = 'TEMP';";
    private static final String SELECT_ALL_PM10_SENSORS = "SELECT * FROM airSensors WHERE TYPE = 'PM10';";
    private static final String SELECT_ALL_PM25_SENSORS = "SELECT * FROM airSensors WHERE TYPE = 'PM25';";

    private Connection connection;
    private Statement stm;

    public void setConnection() {
        String connectionString = "jdbc:sqlite:resources/smartCity.db";
        try {
            connection = DriverManager.getConnection(connectionString);
            stm = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> storeSensorsList() {
        Map<String, Integer> list = new HashMap<>();
        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_STORE_SENSORS);

            while(rsSensors.next()) {
                list.put(rsSensors.getString("name"), rsSensors.getInt("storetype"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<String> trafficSensorsList() {
        ArrayList<String> TrafficList = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_TRAFFIC_SENSORS);

            while (rsSensors.next()) {
                TrafficList.add(rsSensors.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return TrafficList;
    }

    public ArrayList<String> humiditySensorsList() {
        ArrayList<String> HumiditySensorList = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_HUM_SENSORS);

            while (rsSensors.next()) {
                HumiditySensorList.add(rsSensors.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return HumiditySensorList;
    }

    public ArrayList<String> temperatureSensorsList() {
        ArrayList<String> tmpSensorList= new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_TEMP_SENSORS);

            while (rsSensors.next()) {
                tmpSensorList.add(rsSensors.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmpSensorList;
    }

    public ArrayList<String> pm10SensorsList() {
        ArrayList<String> Pm10SensorList = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_PM10_SENSORS);

            while (rsSensors.next()) {
                Pm10SensorList.add(rsSensors.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Pm10SensorList;
    }

   public ArrayList<String> pm25SensorsList() {
        ArrayList<String> Pm25SensorList = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_PM25_SENSORS);

            while (rsSensors.next()) {
                Pm25SensorList.add(rsSensors.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Pm25SensorList;
    }

}
