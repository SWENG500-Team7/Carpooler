package com.carpooler.dao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Kevin on 6/17/2015.
 */
public class VehicleRestService {
    private static final String mBaseUrl = "http://www.fueleconomy.gov/ws/rest/";
    private static final String mMenuUrl = "vehicle/menu/";
    private static final String mMpgUrl = "ympg/shared/";

    /**
     * Get list of years from FuelEconomy.gov
     * @return
     */
    public static String[] getYears() {
        String requestUrlString = mBaseUrl + mMenuUrl + "year";
        XmlPullParser parser = vehicleServiceCall(requestUrlString);
        return parseSingleTag(parser, "text");
    }

    /**
     * Get list of makes based on years from FuelEconomy.gov
     * @param pYear
     * @return
     */
    public static String[] getMakes(String pYear) {
        String requestUrlString = mBaseUrl + mMenuUrl + "make?year=";
        try {
            requestUrlString += URLEncoder.encode(pYear, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        XmlPullParser parser = vehicleServiceCall(requestUrlString);
        return parseSingleTag(parser, "text");
    }

    /**
     * Get list of models based on makes and years from FuelEconomy.gov
     * @param pYear
     * @param pMake
     * @return
     */
    public static String[] getModels(String pYear, String pMake) {
        String requestUrlString = mBaseUrl + mMenuUrl + "model?year=";
        try {
            requestUrlString += URLEncoder.encode(pYear, "UTF-8") + "&make=" + URLEncoder.encode(pMake, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        XmlPullParser parser = vehicleServiceCall(requestUrlString);
        return parseSingleTag(parser, "text");
    }

    /**
     * Get mpg based on make and model from FuelEconomy.gov
     * @param pMake
     * @param pModel
     * @param pYear
     * @return
     */
    public static int getMPG(String pMake, String pModel, String pYear) {
        String requestUrlString = mBaseUrl + mMpgUrl + "vehicles?make=" + pMake + "&model=" + pModel;
        XmlPullParser parser = vehicleServiceCall(requestUrlString);
        String[] mpgs = parseSingleTagForYear(parser, pYear, "comb08");
        int sum = 0;
        int len = mpgs.length;
        if(len > 0) {
            for(int i = 0; i < len; i++) {
                sum += Integer.parseInt(mpgs[i]);
            }
            // calculate average mpg
            return sum/len;
        }
        if (sum==0){
            sum=25;
        }
        return sum;
    }

    /**
     * Obtain XML from specified URL and put it in a parser
     * @param pRequestUrlString
     * @return
     */
    private static XmlPullParser vehicleServiceCall(String pRequestUrlString) {
        InputStreamReader in = null;
        XmlPullParser parser = null;


        HttpURLConnection urlConnection = null;
        try {
            //Make the connection and get the XML
            URL requestUrl = new URL(pRequestUrlString);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            in = new InputStreamReader(urlConnection.getInputStream());

            //Ensure all data is read from connection
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data;
            while ((data = in.read()) > -1) {
                buffer.write(data);
            }
            in.close();

            //Make parser
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();

            //Put received stream in parser
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new ByteArrayInputStream(buffer.toByteArray()), null);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return parser;
    }

    /**
     * Get list of XML element data based on specified tag (e.g. get data from all "text" tags)
     * @param pParser
     * @param pTag
     * @return
     */
    private static String[] parseSingleTag(XmlPullParser pParser, String pTag) {
        ArrayList<String> itemList = new ArrayList<String>();

        try {
            int eventType = pParser.getEventType();

            //Move through document and collect data
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String item = null;

                //Get the value from every "text" tag
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        item = pParser.getName();
                        if (item.equals(pTag)) {
                            itemList.add(pParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }

                //Iterate through document
                pParser.next();
                eventType = pParser.getEventType();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] itemArray = itemList.toArray(new String[itemList.size()]);
        return itemArray;
    }

    /**
     * Get list of XML element data based on specified tag for a specified year
     * @param pParser
     * @param pYear
     * @param pTag
     * @return
     */
    private static String[] parseSingleTagForYear(XmlPullParser pParser, String pYear, String pTag) {
        ArrayList<String> itemList = new ArrayList<String>();
        if (pParser!=null) {
            String value = null;
            try {
                int eventType = pParser.getEventType();

                //Move through document and collect data
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String item = null;

                    //Get the value from pTag for when the value for "year" is pYear
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            item = pParser.getName();
                            if (item.equals(pTag)) {
                                value = pParser.nextText();
                            } else if (item.equals("year") && pParser.nextText().equals(pYear)) {
                                itemList.add(value);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }

                    //Iterate through document
                    pParser.next();
                    eventType = pParser.getEventType();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] itemArray = itemList.toArray(new String[itemList.size()]);
        return itemArray;
    }
}
