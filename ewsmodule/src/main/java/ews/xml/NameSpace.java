package ews.xml;

/**
 * Created by marcprive on 05-25-15.
 */
public class NameSpace {
    private String URI = null;
    private String prefix = null;

    public NameSpace(String prefix, String URI) {
        this.URI=URI;
        this.prefix=prefix;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

