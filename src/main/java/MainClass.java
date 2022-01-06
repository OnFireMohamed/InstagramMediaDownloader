import java.io.FileWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.swing.JOptionPane;

public class MainClass {
    static Scanner scanner;
    static LoginClass logger;
    static MohamedMatcher matcher;
    static Random ran;
    static int counter = 0;
    static String version = "v5";
    public static void main(String[] args) throws Exception{
        
        check();
        ran = new Random();
        matcher = new MohamedMatcher();
        logger = new LoginClass();
        scanner = new Scanner(System.in);
        System.out.print("Enter Username : ");
        var user = scanner.nextLine();
        System.out.print("Enter Password : ");
        var pass = scanner.nextLine();
        if(logger.AppLogin(user, pass) ){
            checkDirect cd = new checkDirect(logger.ApiCookie);
            System.out.println("[ + ] Logged In");
            while (true){
                cd.mainSystem();
                counter += 1;
                System.out.println("direct has been checked now for " + counter);
                try{Thread.sleep(30000);}catch (Exception e){}
            }
        }
        else{
            System.out.println("[ + ] Error in login");
            scanner.nextLine();
        }

    }
    static void check() {
        if ( ! new Requests().MakeGetRequest("https://pastebin.com/raw/A0FY1H7h").contains(version)) {
            JOptionPane.showMessageDialog(null, "New Version !.\nDownload it from github.com/OnFireMohamed/InstagramMediaDownloader", "By @afph", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    static int randomSleep(){
        int num = ran.nextInt(120000);

        if(num <= 60000){
            return randomSleep();
        }
        else
            return num;
    }
    static void SendMessage(String userid, String text){
        var context = "6794371" + matcher.GenerateRandom(12, "0987654321");
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("Cookie", logger.ApiCookie);
        var postdata = "recipient_users=[[" + userid + "]]&action=send_item&is_shh_mode=0&send_attribution=inbox_new_message&client_context=6794371" + context + "&text=" + (text + logger.s) + "&device_id=android-8cd32a1ba6669cbe&mutation_token=6794371" + context + "&_uuid=" + UUID.randomUUID().toString() + "&offline_threading_id=6794371" + context;

        try{String Response = client.MakePostRequest("https://i.instagram.com/api/v1/direct_v2/threads/broadcast/text/", postdata.getBytes(StandardCharsets.UTF_8));}catch (Exception re){}
        
    }
    static boolean Follow(String UserId){
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", logger.ApiCookie);
        String Response = client.MakePostRequest(String.format("https://i.instagram.com/api/v1/friendships/create/%s/", UserId), "");
        if (Response.contains("outgoing_request\":true"))
            return true;
        else
            return false;
    }
    static String makeGetRequest(String url){
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", logger.ApiCookie);
        String Response = client.MakeGetRequest(url);
        return Response;
    }

    public static void largeVideosWork(String url, String user_id) {
        try{

            new Thread(() -> {
                SendMessage(user_id, "Wait until I finish video download url preparation..");
                try {
                    HttpPostMultipart multipart = new HttpPostMultipart("https://api.anonfiles.com/upload", "utf-8");
                    multipart.addFilePart("file", new URL(url).openStream().readAllBytes());
                    String val = multipart.send();
                    System.out.println(val);
                    if (val.contains("\"status\":true")) {
                        var download_url = "https://mohamed-node-apis.herokuapp.com/api/anon-redirect/?url=" + new MohamedMatcher().Match(val, "full\":\"<match>\"", false);
                        var text = "The video is more than a minute long, So you can download it from this url : \n\n" + download_url + "\n\n Notice : open the url from safari or another browser - Not Instagram Browser -";
                        SendMessage(user_id, text);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }
        catch (Exception e) {

        }

    }
}