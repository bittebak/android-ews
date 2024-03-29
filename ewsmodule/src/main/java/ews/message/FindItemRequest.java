package ews.message;

import android.text.format.DateUtils;
import android.text.method.DateTimeKeyListener;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ews.microsoft.NameSpaces;
import ews.xml.NameSpace;

/**
 * Created by marcprive on 05-25-15.
 */
public class FindItemRequest extends EwsRequest{

    private final String Traversal = "Shallow";
    private final String BaseShape = "AllProperties";
    private String folderId = null;
    private Date startDate;
    private Date endDate;
    private String changeKey = null;

    @Override
    public void setBody() {
        try {
            //Move Body and Header to base class
//          <m:FindItemOperation Traversal="Shallow">
            serializer.startTag(NameSpaces.EwsMessagesNamespace, "FindItem");
            serializer.attribute("", "Traversal", "Shallow");
            //<m:ItemShape>
            serializer.startTag(NameSpaces.EwsMessagesNamespace, "ItemShape");
            //<t:BaseShape>AllProperties</t:BaseShape>
            serializer.startTag(NameSpaces.EwsTypesNamespace, "BaseShape");
            serializer.text("AllProperties");
            serializer.endTag(NameSpaces.EwsTypesNamespace, "BaseShape");
//          </m:ItemShape>
            serializer.endTag(NameSpaces.EwsMessagesNamespace, "ItemShape");

//          <m:CalendarView StartDate="2015-04-01T10:00:00Z" EndDate="2015-05-30T11:00:00Z"/>
            serializer.startTag(NameSpaces.EwsMessagesNamespace, "CalendarView");
            //serializer.attribute("", "StartDate", "2015-04-01T10:00:00Z");
            String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
            serializer.attribute("", "StartDate",currentDateandTime );
            //serializer.attribute("", "EndDate", "2015-05-30T11:00:00Z");
            Date twoWeeksFromNow = addDays(new Date(), 14);


            String twoWeeksString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(twoWeeksFromNow);
            serializer.attribute("", "EndDate", twoWeeksString);

            serializer.endTag(NameSpaces.EwsMessagesNamespace, "CalendarView");
//          <m:ParentFolderIds>
            serializer.startTag(NameSpaces.EwsMessagesNamespace, "ParentFolderIds");
//          <t:FolderId Id="AAMkAGQ4Mjg0NmQ3LTlhYzItNDJiYy04Zjg0LTIwNzJjZmFmZGQzNAAuAAAAAADrjl6d22hPT493R2/6RE3rAQBYjecIPnQmQKZ4xQ0F4fgkAAAAzhTDAAA="/>
            serializer.startTag(NameSpaces.EwsTypesNamespace, "FolderId");
            serializer.attribute("", "Id", "AAMkAGQ4Mjg0NmQ3LTlhYzItNDJiYy04Zjg0LTIwNzJjZmFmZGQzNAAuAAAAAADrjl6d22hPT493R2/6RE3rAQBYjecIPnQmQKZ4xQ0F4fgkAAAAzhTDAAA=");
            serializer.endTag(NameSpaces.EwsTypesNamespace, "FolderId");
//          </m:ParentFolderIds>
            serializer.endTag(NameSpaces.EwsMessagesNamespace, "ParentFolderIds");
//          </m:FindItemOperation>
            serializer.endTag(NameSpaces.EwsMessagesNamespace, "FindItem");


        } catch (IOException ignored) {
        }

    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }


}


