package ews.message;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Writer;

import ews.microsoft.NameSpaces;

/**
 * Created by marcprive on 05-30-15.
 */
public abstract class EwsRequest {
    private final String RequestServerVersion = "Exchange2010_SP2";
    XmlSerializer serializer = Xml.newSerializer();

    protected abstract void setBody();

    public void write(Writer writer) {

        try {
            serializer.setOutput(writer);
            serializer.startTag(NameSpaces.EwsSoapNamespace, "Envelope");
            writeSoapHeader();
            serializer.startTag(NameSpaces.EwsSoapNamespace, "Body");
            setBody();
            serializer.endTag(NameSpaces.EwsSoapNamespace, "Body");
            serializer.endTag(NameSpaces.EwsSoapNamespace, "Envelope");
            serializer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     *
     */
    protected void writeSoapHeader() throws IOException {

        serializer.startTag(NameSpaces.EwsSoapNamespace, "Header");
        serializer.startTag(NameSpaces.EwsTypesNamespace, "RequestServerVersion");
        serializer.attribute("", "Version", "Exchange2010_SP2");
        serializer.endTag(NameSpaces.EwsTypesNamespace, "RequestServerVersion");
        serializer.endTag(NameSpaces.EwsSoapNamespace, "Header");
    }

}

