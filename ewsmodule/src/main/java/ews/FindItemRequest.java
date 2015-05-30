package ews;

import android.text.method.DateTimeKeyListener;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import ews.microsoft.NameSpaces;
import ews.xml.NameSpace;

/**
 * Created by marcprive on 05-25-15.
 */
public class FindItemRequest {
    private final String RequestServerVersion = "Exchange2010_SP2";
    private final String Traversal = "Shallow";
    private final String BaseShape = "AllProperties";
    private String folderId = null;
    private Date startDate;
    private Date endDate;
    private String changeKey = null;

    public void write(Writer writer) {
        XmlSerializer serializer = Xml.newSerializer();

        try {
            serializer.setOutput(writer);

            //Move Body and Header to base class
            serializer.startTag(NameSpaces.EwsSoapNamespace, "Envelope");
            serializer.startTag(NameSpaces.EwsSoapNamespace, "Header");
            serializer.startTag(NameSpaces.EwsTypesNamespace, "RequestServerVersion");
            serializer.attribute("", "Version", "Exchange2010_SP2");
            serializer.endTag(NameSpaces.EwsTypesNamespace, "RequestServerVersion");
            serializer.endTag(NameSpaces.EwsSoapNamespace, "Header");


            serializer.startTag(NameSpaces.EwsSoapNamespace, "Body");

//          <m:FindItem Traversal="Shallow">
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
//            <m:CalendarView StartDate="2015-04-01T10:00:00Z" EndDate="2015-05-30T11:00:00Z"/>
            serializer.startTag(NameSpaces.EwsMessagesNamespace, "CalendarView");
            serializer.attribute("", "StartDate", "2015-04-01T10:00:00Z");
            serializer.attribute("", "EndDate", "2015-05-30T11:00:00Z");
            serializer.endTag(NameSpaces.EwsMessagesNamespace, "CalendarView");
//          <m:ParentFolderIds>
            serializer.startTag(NameSpaces.EwsMessagesNamespace, "ParentFolderIds");
//          <t:FolderId Id="AAMkAGQ4Mjg0NmQ3LTlhYzItNDJiYy04Zjg0LTIwNzJjZmFmZGQzNAAuAAAAAADrjl6d22hPT493R2/6RE3rAQBYjecIPnQmQKZ4xQ0F4fgkAAAAzhTDAAA="/>
            serializer.startTag(NameSpaces.EwsTypesNamespace, "FolderId");
            serializer.attribute("", "Id", "AAMkAGQ4Mjg0NmQ3LTlhYzItNDJiYy04Zjg0LTIwNzJjZmFmZGQzNAAuAAAAAADrjl6d22hPT493R2/6RE3rAQBYjecIPnQmQKZ4xQ0F4fgkAAAAzhTDAAA=");
            serializer.endTag(NameSpaces.EwsTypesNamespace, "FolderId");
//          </m:ParentFolderIds>
            serializer.endTag(NameSpaces.EwsMessagesNamespace, "ParentFolderIds");
//          </m:FindItem>
            serializer.endTag(NameSpaces.EwsMessagesNamespace, "FindItem");

            serializer.endTag(NameSpaces.EwsSoapNamespace, "Body");
            serializer.endTag(NameSpaces.EwsSoapNamespace, "Envelope");
            serializer.flush();
        } catch (IOException ignored) {
        }

    }


}


