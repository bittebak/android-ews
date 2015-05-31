package ews.message;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ews.microsoft.NameSpaces;
import ews.types.CalendarItem;

/**
 * Created by marcprive on 05-31-15.
 */
public class FindItemResponse {
    private String responseCode;
    private List<CalendarItem> items = new ArrayList<>();
    XmlPullParser parser = Xml.newPullParser();
    private CalendarItem currentItem;
    private String currentElement;

    public void parse(InputStream inputStream) throws IOException {
        //
        try {
            initParser(inputStream);
            readFeed();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }

    }

    private void initParser(InputStream inputStream) throws XmlPullParserException {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(inputStream, null);

    }

    private void readFeed() throws XmlPullParserException, IOException {

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                Log.d("FindItemResponse","Start of document");
            } else if(eventType == XmlPullParser.START_TAG) {
                System.out.println("Start tag "+ parser.getName());
                handleStartEvent();
            } else if(eventType == XmlPullParser.END_TAG) {
                handleEndEvent();
                System.out.println("End tag "+parser.getName());
            } else if(eventType == XmlPullParser.TEXT) {
                handleTextEvent();
                System.out.println("Text "+parser.getText());
            }
            eventType = parser.next();
        }
        System.out.println("End document");

    }

    private void handleStartEvent() {
        currentElement = parser.getName();
        if(currentElement.equals( "CalendarItem"))
        {
            CalendarItem item = new CalendarItem();
            items.add(item);
            currentItem = item;
        }

    }

    private void handleTextEvent() {

        if( currentElement.equals("Subject")){
            currentItem.setSubject(parser.getText());
        }

        if(currentElement.equals("Start")){
            currentItem.setStart(
                    parseDate(
                            parser.getText()
                    )
            );
        }

        if(currentElement.equals("End")){
            currentItem.setEnd(
                    parseDate(
                            parser.getText()
                    )
            );
        }
    }

    private void handleEndEvent() {
        if(parser.getName().equals("CalendarItem")) {
            currentItem = null;
        }

    }

    private Date parseDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date result = null;
        try {
            result = formatter.parse("2015-04-01 12:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

}
