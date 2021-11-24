package iot.smartcity.server.db;

import iot.smartcity.sensors.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Server class to interact with the database. It selects all the
 * different kind of sensors and pass the result to the server.
 * The server then adds all the sensors making them operational
 */

public class QueryHandler {
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

    public ArrayList<StoreSensor> storeSensorsList() {
        ArrayList<StoreSensor> list = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_STORE_SENSORS);
            StoreSensor tmpSensor;

            while(rsSensors.next()) {
                tmpSensor = new StoreSensor(rsSensors.getString("name"), rsSensors.getInt("storeType"),
                        100, rsSensors.getInt("location"));
                list.add(tmpSensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<TrafficSensor> trafficSensorsList() {
        ArrayList<TrafficSensor> list = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_TRAFFIC_SENSORS);
            TrafficSensor tmpSensor;

            while (rsSensors.next()) {
                tmpSensor = new TrafficSensor(rsSensors.getString("name"),
                        rsSensors.getInt("node1"), rsSensors.getInt("node2"));
                list.add(tmpSensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<HumiditySensor> humiditySensorsList() {
        ArrayList<HumiditySensor> list = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_HUM_SENSORS);
            HumiditySensor tmpSensor;

            while (rsSensors.next()) {
                tmpSensor = new HumiditySensor(rsSensors.getString("name"));
                list.add(tmpSensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<TemperatureSensor> temperatureSensorsList() {
        ArrayList<TemperatureSensor> list = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_TEMP_SENSORS);
            TemperatureSensor tmpSensor;

            while (rsSensors.next()) {
                tmpSensor = new TemperatureSensor(rsSensors.getString("name"));
                list.add(tmpSensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Pm10sensor> Pm10SensorsList() {
        ArrayList<Pm10sensor> list = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_PM10_SENSORS);
            Pm10sensor tmpSensor;

            while (rsSensors.next()) {
                tmpSensor = new Pm10sensor(rsSensors.getString("name"));
                list.add(tmpSensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

   public ArrayList<Pm25sensor> Pm25SensorsList() {
        ArrayList<Pm25sensor> list = new ArrayList<>();

        try {
            ResultSet rsSensors = stm.executeQuery(SELECT_ALL_PM25_SENSORS);
            Pm25sensor tmpSensor;

            while (rsSensors.next()) {
                tmpSensor = new Pm25sensor(rsSensors.getString("name"));
                list.add(tmpSensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
