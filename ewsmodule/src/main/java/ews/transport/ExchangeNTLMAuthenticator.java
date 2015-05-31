package ews.transport;

        import java.net.Authenticator;
        import java.net.PasswordAuthentication;


public class ExchangeNTLMAuthenticator  extends Authenticator
{
    private String _domainName;
    private String _userName;
    private String _passWord;

    //Constructor
    public ExchangeNTLMAuthenticator(){}

    public ExchangeNTLMAuthenticator(String domainName,String userName,String passWord){

        _domainName = domainName;
        _userName = userName;
        _passWord = passWord;
    }

    @Override
    public  PasswordAuthentication getPasswordAuthentication() {
        // Remember to include the NT domain in the username
        return new PasswordAuthentication(_domainName + "\\" + _userName, _passWord.toCharArray());
    }

}


