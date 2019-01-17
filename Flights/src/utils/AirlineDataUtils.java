package utils;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;

public class AirlineDataUtils {

    public static boolean isHeader(Text row) {
        String[] contents = row.toString().split(",");
        //equalsIgnoreCase不区分大小写
        return (contents.length > 0 && contents[0].equalsIgnoreCase("year"));
    }

    public static String getYear(String[] contents) {
        return contents[0];
    }

    public static String getMonth(String[] contents) {
        return StringUtils.leftPad(contents[1], 2, "0");
    }

    public static String getDateOfMonth(String[] contents) {
        return StringUtils.leftPad(contents[2], 2, "0");
    }

    public static String getDate(String[] contents) {
        StringBuilder builder = new StringBuilder("");
        builder.append(getMonth(contents)).append("/")
                .append(getDateOfMonth(contents)).append("/")
                .append(getYear(contents));
        return builder.toString();
    }

    public static String getDepartureDateTime(String[] contents) {
        StringBuilder builder = new StringBuilder("");
        builder.append(getYear(contents)).append(getMonth(contents))
                .append(getDateOfMonth(contents))
                .append(getDepartureTime(contents));
        return builder.toString();
    }

    public static String getArrivalDateTime(String[] contents) {
        StringBuilder builder = new StringBuilder("");
        builder.append(getYear(contents)).append(getMonth(contents))
                .append(getDateOfMonth(contents))
                .append(getArrivalTime(contents));
        return builder.toString();
    }

    public static String getDayOfTheWeek(String[] contents) {
        return contents[3];
    }

    public static String getDepartureTime(String[] contents) {
        return StringUtils.leftPad(contents[4], 4, "0");
    }

    public static String getScheduledDepartureTime(String[] contents) {
        return StringUtils.leftPad(contents[5], 4, "0");

    }

    public static String getArrivalTime(String[] contents) {
        return StringUtils.leftPad(contents[6], 4, "0"); //总长度为4，左侧填充0
    }

    public static String getScheduledArrivalTime(String[] contents) {
        return StringUtils.leftPad(contents[7], 4, "0");
    }

    public static String getUniqueCarrier(String[] contents) {
        return contents[8];
    }

    public static String getFlightNum(String[] contents) {
        return contents[9];
    }

    public static String getTailNum(String[] contents) {
        return contents[10];
    }

    public static String getElapsedTime(String[] contents) {
        return contents[11];
    }

    public static String getScheduledElapsedTime(String[] contents) {
        return contents[12];
    }

    public static String getAirTime(String[] contents) {
        return contents[13];
    }

    public static String getArrivalDelay(String[] contents) {
        return contents[14];
    }

    public static String getDepartureDelay(String[] contents) {
        return contents[15];
    }

    public static String getOrigin(String[] contents) {
        return contents[16];
    }

    public static String getDestination(String[] contents) {
        return contents[17];
    }

    public static String getDistance(String[] contents) {
        return contents[18];
    }

    public static String getTaxiIn(String[] contents) {
        return contents[19];
    }

    public static String getTaxiOut(String[] contents) {
        return contents[20];
    }

    public static String getCancelled(String[] contents) {
        return contents[21];
    }

    public static String getCancellationCode(String[] contents) {
        return contents[22];
    }

    public static String getDiverted(String[] contents) {
        return contents[23];
    }

    public static String getCarrierDelay(String[] contents) {
        return contents[24];
    }

    public static String getWeatherDelay(String[] contents) {
        return contents[25];
    }

    public static String getNASDelay(String[] contents) {
        return contents[26];
    }

    public static String getSecurityDelay(String[] contents) {
        return contents[27];
    }

    public static String getLateAircraftDelay(String[] contents) {
        return contents[28];
    }

    public static int parseMinutes(String minutes, int defaultValue) {
        try {
            return Integer.parseInt(minutes);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static boolean parseBoolean(String bool, boolean defaultValue) {
        try {
            int iBool = Integer.parseInt(bool);
            return (iBool == 1);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static String[] getSelectResultsPerRow(Text row) {
        String[] contents = row.toString().split(",");
        String[] outputArray = new String[10];
        outputArray[0] = AirlineDataUtils.getDate(contents);
        outputArray[1] = AirlineDataUtils.getDepartureTime(contents);
        outputArray[2] = AirlineDataUtils.getArrivalTime(contents);
        outputArray[3] = AirlineDataUtils.getOrigin(contents);
        outputArray[4] = AirlineDataUtils.getDestination(contents);
        outputArray[5] = AirlineDataUtils.getDistance(contents);
        outputArray[6] = AirlineDataUtils.getElapsedTime(contents);
        outputArray[7] = AirlineDataUtils.getScheduledElapsedTime(contents);
        outputArray[8] = AirlineDataUtils.getDepartureDelay(contents);
        outputArray[9] = AirlineDataUtils.getArrivalDelay(contents);
        return outputArray;
    }

    public static StringBuilder mergeStringArray(String[] array,
                                                 String seperator) {
        StringBuilder output = new StringBuilder("");
        if (array != null && array.length > 0) {
            output.append(array[0]);
            for (int i = 1; i < array.length; i++) {
                output.append(seperator).append(array[i]);
            }
        }
        return output;
    }

    public static boolean isAirportMasterFileHeader(String line) {
        return line.startsWith("\"iata\",\"airport\"");
    }

    public static String[] parseAirportMasterLine(String line) {

        String[] masterLine = new String[7];
        int index = line.lastIndexOf(",");
        masterLine[6] = line.substring(index + 1);
        line = line.substring(0, index);
        index = line.lastIndexOf(",");
        masterLine[5] = line.substring(index + 1);
        line = line.substring(0, index);
        String[] airportDetails = line.split("\",\"");
        for (int i = 0; i < airportDetails.length; i++) {
            masterLine[i] = airportDetails[i].replaceAll("\"", "");
        }
        return masterLine;
    }


    public static boolean isCarrierFileHeader(String line) {

        String header = "Code,Description";
        return line.startsWith(header);

    }

    public static String[] parseCarrierLine(String line) {

        String[] carrierDetails = line.split("\",\"");
        for (int i = 0; i < carrierDetails.length; i++) {
            carrierDetails[i] = carrierDetails[i].replaceAll("\"", "");
        }
        return carrierDetails;
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String testString = "1987,10,14,3,741,730,912,849,PS,1451,NA,91,79,NA,23,11,SAN,SFO,447,NA,NA,0,NA,0,NA,NA,NA,NA,NA";
        Text t = new Text(testString);
        String[] arr = AirlineDataUtils.getSelectResultsPerRow(t);
        System.out.println(AirlineDataUtils.mergeStringArray(arr, ",")
                .toString());

        String airportMaster = "\"06U\",\"Jackpot,Hayden \",\"Jackpot\",\"NV\",\"USA\",41.97602222,-114.6580911";
        String[] airlineDetails = AirlineDataUtils
                .parseAirportMasterLine(airportMaster);

        for (String a : airlineDetails) {
            System.out.println(a);
        }

        String airportMasterHead = "\"iata\",\"airport\",\"city\",\"state\",\"country\",\"lat\",\"long\"";
        System.out.println(AirlineDataUtils
                .isAirportMasterFileHeader(airportMasterHead));

        String carrierLine = "\"02Q\",\"Titan, Airways\"";
        String[] carrierLineDetails = AirlineDataUtils
                .parseCarrierLine(carrierLine);

        for (String a : carrierLineDetails) {
            System.out.println(a);
        }

        String carrierHeader = "Code,Description";
        System.out.println(AirlineDataUtils.isCarrierFileHeader(carrierHeader));

        System.out.println(AirlineDataUtils.getDepartureDateTime(testString
                .split(",")));
    }

}

