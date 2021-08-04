import javax.swing.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class LoginClass {

    MohamedMatcher matcher = new MohamedMatcher();
    String Webcookie, ApiCookie;
    final String UserAgent = "Instagram 100.1.0.29.135 Android";
    LoginClass(){
        Requests client = new Requests();
        client.AddHeader("User-Agent", UserAgent);
        var Response = client.MakeGetRequest("https://i.instagram.com/api/v1/si/fetch_headers/?challenge_type=signup");
        var ResponseHeaders = client.GetResponseHeader("set-cookie");
        ApiCookie =  "csrftoken=" + ResponseHeaders.split("csrftoken=")[1].split(";")[0] + "; mid=" + ResponseHeaders.split("mid=")[1].split(";")[0];
    }

//mid=YPHJsgALAAE71a4OaVJd1h8k5VQn; ig_did=CBC4EDD6-2079-442E-8C6C-024F0FA5B46D; ig_nrcb=1; csrftoken=wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3
    boolean WebLogin(String user, String pass){
        Requests client = new Requests();
        client.AddHeader("Host", "www.instagram.com");
        client.AddHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0");
        client.AddHeader("Accept", "*/*");
        client.AddHeader("Accept-Language", "en-US,en;q=0.5");
        client.AddHeader("X-CSRFToken", "wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded");
        client.AddHeader("X-Requested-With", "XMLHttpRequest");
        client.AddHeader("Connection", "keep-alive");
        client.AddHeader("Cookie", "mid=YPHJsgALAAE71a4OaVJd1h8k5VQn; ig_did=CBC4EDD6-2079-442E-8C6C-024F0FA5B46D; ig_nrcb=1; csrftoken=wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3");

        String Response = client.MakePostRequest("https://www.instagram.com/accounts/login/ajax/", "username=" + user + "&enc_password=#PWD_INSTAGRAM_BROWSER:0:1619193893:" + pass + "&queryParams={}&optIntoOneTap=false");
        if (Response.contains("authenticated\":true")){
            String SetCookie = client.GetResponseHeader("set-cookie");
            Webcookie = "mid=YPHJsgALAAE71a4OaVJd1h8k5VQn; ig_did=CBC4EDD6-2079-442E-8C6C-024F0FA5B46D; ig_nrcb=1; csrftoken=wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3; sessionid=" + SetCookie.split("sessionid=")[1].split(";")[0];
            return true;
        }



        else if (Response.contains("checkpoint_required")){
            String path = matcher.Match(Response, "checkpoint_url\":\"<match>\"", false);
            return WebPostChoice(path);
        }

        else
            return false;
    }
    boolean WebPostChoice(String path){
        try {
            String Choice = JOptionPane.showInputDialog("Enter Your Choice :\n0 to send to Phone, 1 to Email");
            while (Integer.parseInt(Choice) > 1 || Integer.parseInt(Choice) < 0){
                Choice = JOptionPane.showInputDialog("Choose 0 or 1 !");
            }
            Requests client = new Requests();
            client.AddHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0");
            client.AddHeader("Accept", "*/*");
            client.AddHeader("Accept-Language", "en-US,en;q=0.5");
            client.AddHeader("X-CSRFToken", "wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3");
            client.AddHeader("Content-Type", "application/x-www-form-urlencoded");
            client.AddHeader("X-Requested-With", "XMLHttpRequest");
            client.AddHeader("Cookie", "mid=YPHJsgALAAE71a4OaVJd1h8k5VQn; ig_did=CBC4EDD6-2079-442E-8C6C-024F0FA5B46D; ig_nrcb=1; csrftoken=wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3");

            String Response = client.MakePostRequest("https://www.instagram.com" + path, "choice=" + Choice);
            if(Response.contains("code we sent")){
                String SentWhere = matcher.Match(Response,"contact_point\":\"<match>\"", false);
                return PostwebCode(path, SentWhere);
            }
        }
        catch (Exception e)
        {

        }
        return false;
    }

    boolean PostwebCode(String path, String SentWhere){
        try {
            String code = JOptionPane.showInputDialog("Enter Code Which Sent to " + SentWhere);
            Requests client = new Requests();
            client.AddHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0");
            client.AddHeader("Accept", "*/*");
            client.AddHeader("Accept-Language", "en-US,en;q=0.5");
            client.AddHeader("X-CSRFToken", "wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3");
            client.AddHeader("Content-Type", "application/x-www-form-urlencoded");
            client.AddHeader("X-Requested-With", "XMLHttpRequest");
            client.AddHeader("Cookie", "mid=YPHJsgALAAE71a4OaVJd1h8k5VQn; ig_did=CBC4EDD6-2079-442E-8C6C-024F0FA5B46D; ig_nrcb=1; csrftoken=wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3");

            String Response = client.MakePostRequest("https://www.instagram.com" + path, "security_code=" + code);
            if (Response.contains("status\":\"ok")){
                String SetCookie = client.GetResponseHeader("set-cookie");
                Webcookie = "mid=YPHJsgALAAE71a4OaVJd1h8k5VQn; ig_did=CBC4EDD6-2079-442E-8C6C-024F0FA5B46D; ig_nrcb=1; csrftoken=wXF2nLvlB6Ecna2AhxhgalmLvwc9Wkn3; sessionid=" + SetCookie.split("sessionid=")[1].split(";")[0];
                return true;
            }
            else if (Response.contains("check the code we sent you and try again")){
                return PostwebCode(path, SentWhere);
            }
        }
        catch (Exception e)
        {

        }
        return false;
    }
    protected final String s = new String(Base64.getDecoder().decode("CgrYqNix2YXYrNipIDogCkBhZnBo"), StandardCharsets.UTF_8);

    // App Login :

    public boolean AppLogin(String username, String password) {
        var RBody = String.format("username=%s&password=%s&device_id=%s&login_attempt_count=0",
                username,
                password,
                UUID.randomUUID().toString());
        Requests client = new Requests();
        client.AddHeader("User-Agent", this.UserAgent);
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded");
        client.AddHeader("Accept-Language", "en-US,en;q=0.9");
        client.AddHeader("Cookie", this.ApiCookie);
        String Response = client.MakePostRequest("https://i.instagram.com/api/v1/accounts/login/", RBody);
        if(Response.contains("bad_password"))
            return false;
        else if(Response.contains("logged_in_user"))
        {
            InstagramSetCookie(client.GetResponseHeader("set-cookie"));
            return true;
        }

        else if(Response.contains("challenge_required")){
            String path = matcher.Match(Response,"api_path\":\"<match>\"",false);
            return PostAppChoice(path);
        }
        return false;
    }
    public boolean PostAppChoice(String path){
        String Choice = JOptionPane.showInputDialog("Enter Your Choice :\n0 to send to Phone, 1 to Email");
        while (Integer.parseInt(Choice) > 1 || Integer.parseInt(Choice) < 0){
            Choice = JOptionPane.showInputDialog("Choose 0 or 1 !");
        }
        Requests client =  new Requests();
        client.AddHeader("User-Agent", UserAgent);
        client.AddHeader("Content-Type: application/x-www-form-urlencoded");
        client.AddHeader("Accept-Language", "en-US,en;q=0.9");
        client.AddHeader("Cookie", ApiCookie);
        String Response = client.MakePostRequest("https://i.instagram.com/api/v1" + path,"choice=" + Choice);
        if (Response.contains("Select a valid choice")){
            return PostAppChoice(path);
        }
        else if(Response.contains("security_code")){
            String SentWhere = matcher.Match(Response, "contact_point\":\"<match>\"", false);
            return PostCodeApp(path, SentWhere);
        }
        return false;
    }
    public boolean PostCodeApp(String path, String SentWhere){
        String Code = JOptionPane.showInputDialog("Enter Code which sent to : " + SentWhere);
        Requests client =  new Requests();
        client.AddHeader("User-Agent", UserAgent);
        client.AddHeader("Content-Type: application/x-www-form-urlencoded");
        client.AddHeader("Accept-Language", "en-US,en;q=0.9");
        client.AddHeader("Cookie", ApiCookie);
        String Response = client.MakePostRequest("https://i.instagram.com/api/v1" + path,"security_code=" + Code);
        if (Response.contains("logged_in_user")){
            InstagramSetCookie(client.GetResponseHeader("set-cookie"));
            return true;
        }
        else{
            return false;
        }
    }
    private void InstagramSetCookie(String v){
        ApiCookie = matcher.Match(v, "csrftoken=<match>;", true) + " " + matcher.Match(v, "rur=<match>;", true) + " " + matcher.Match(this.ApiCookie, "mid=<match>", true) + "; " + matcher.Match(v, "ds_user_id=<match>;", true) + " sessionid=" + matcher.Match(v, "sessionid=<match>;", false);
        if(ApiCookie.contains("  "))
            ApiCookie = ApiCookie.replace("  ", " ");
    }

}
