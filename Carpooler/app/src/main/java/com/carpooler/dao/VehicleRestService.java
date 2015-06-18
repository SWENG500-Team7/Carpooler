package com.carpooler.dao;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        String requestUrlString = mBaseUrl + mMenuUrl + "make?year=" + pYear;
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
        String requestUrlString = mBaseUrl + mMenuUrl + "model?year=" + pYear + "&make=" + pMake;
        XmlPullParser parser = vehicleServiceCall(requestUrlString);
        return parseSingleTag(parser, "text");
    }

    /**
     * Obtain XML from specified URL and put it in a parser
     * @param pRequestUrlString
     * @return
     */
    private static XmlPullParser vehicleServiceCall(String pRequestUrlString) {
        InputStream in = null;
        XmlPullParser parser = null;

        //Make the connection and get the XML
        HttpURLConnection urlConnection = null;
        try {
            URL requestUrl = new URL(pRequestUrlString);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            urlConnection.disconnect();
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            e.printStackTrace();
        }

        //Parse XML
        try {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            parser = parserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
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
}
