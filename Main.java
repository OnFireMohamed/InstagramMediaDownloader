import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static Scanner scanner;
    static LoginClass logger;
    static MohamedMatcher matcher;
    static Random ran;
    //Methods
    public static void main(String[] args){
        ran = new Random();
        matcher= new MohamedMatcher();
        logger = new LoginClass();
        scanner = new Scanner(System.in);

        if (logger.ApiCookie != null || logger.ApiCookie != ""){

            System.out.print("Enter Username : ");
            var user = scanner.nextLine();
            System.out.print("Enter Password : ");
            var pass = scanner.nextLine();
            if(logger.AppLogin(user, pass) && logger.WebLogin(user, pass)){
                System.out.println("[ + ] Logged In");
                while (true){
                    CheckDirect();
                    try{Thread.sleep(RandomSleep());}catch (Exception e){}

                }
            }
            else{
                System.out.println("[ + ] Error in login");
                scanner.nextLine();
            }
        }

    }

    static int RandomSleep(){
        int num = ran.nextInt(120000);

        if(num <= 60000){
            return RandomSleep();
        }
        else
            return num;
    }
    static void CheckDirect(){
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", logger.ApiCookie);
        String Response = client.MakeGetRequest("https://i.instagram.com/api/v1/direct_v2/inbox/?visual_message_return_type=unseen&persistentBadging=true&use_unified_inbox=true");
        if(! Response.contains("pending_requests_total\": 0,"))
        {
           var Pattern = Response.split("pending_requests_total")[1];
           var UserId = matcher.Match(Pattern, "pk\": <match>,", false);
           var UserName = matcher.Match(Pattern, "username\": \"<match>\"", false);
           SendMessage(UserId, UserName);
        }
        if (Response.contains("item_type\": \"profile\"")){
            var array = Response.split("item_type\": \"profile\"");
            var timestamp = matcher.Match(array[0], "timestamp\": <match>,", false);
            var username = matcher.Match(array[1], "username\": \"<match>\",", false);
            var senderusername = matcher.Match(array[1], "thread_title\": \"<match>\"", false);
            var threadid = matcher.Match(array[0], "thread_id\": \"<match>\"", false);
            var url = GetUrlPic(username);
            if(url != null || url != ""){
                new PhotoSend(logger.ApiCookie).System(url, timestamp, username, senderusername, threadid);
            }
        }
        if (! Response.contains("username")){
            System.out.println("[ + ] Error At : " + new Date(System.currentTimeMillis()));
        }

        String[] v = Response.split("thread_id");
        for(String v1 : v){
            if(v1.contains("video_versions")){
                var threadid = matcher.Match(v1, "\": \"<match>\",", false);
                var username = matcher.Match(v1, "\"username\": \"<match>\",", false);
                var userid = matcher.Match(v1, "\"pk\": <match>,", false);
                var itemtype = matcher.Match(v1, "\"item_type\": \"<match>\",", false);
                var mediaid = matcher.Match(v1, "media_id\": <match>,", false);
                var timestamp = matcher.Match(v1, "timestamp\": <match>," , false);
                var clientcontext = matcher.Match(v1, "client_context\": \"<match>\"", false);
                switch (itemtype){
                    case "story_share", "felix_share", "clip":
                    {
                        var url = matcher.Match(v1.split("video_versions")[1], "\"url\": \"<match>\"" , false).replace("\\u0026", "&");
                        new VideoSend(logger.ApiCookie).Send(url, threadid, clientcontext, username);
                        break;
                    }
                    case "media_share":{
                        var Json = GetMediaJson(mediaid);
                        var str = Json.split("video_versions");
                        ArrayList<String> UrlsList = new ArrayList<String>();

                        for (String h : str){
                            if(h.contains(".mp4") && h.contains("\"url\"")){
                                var url = matcher.Match(h, "url\":\"<match>\"", false).replace("\\u0026", "&");
                                if (url.contains(".mp4")){
                                    UrlsList.add(url);
                                }
                            }
                        }
                        for (String url : UrlsList) {
                            new VideoSend(logger.ApiCookie).Send(url, threadid, clientcontext, username);
                            try {Thread.sleep(2000);}catch(Exception e){}
                        }

                        break;
                    }
                }
            }
        }
    }

    static void SendMessage(String userid, String username){
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", logger.ApiCookie);

        String Response = client.MakePostRequest("https://i.instagram.com/api/v1/direct_v2/threads/broadcast/text/","recipient_users=[[" + userid + "]]&action=send_item&is_shh_mode=0&send_attribution=inbox_search&text=Activated Succefully : @" + username);
        System.out.println("Activated : @" + username);
    }

    static String GetUrlPic(String username){

        Requests client = new Requests();
        client.AddHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        client.AddHeader("accept-language", "en-US,en;q=0.9");
        client.AddHeader("cookie", logger.Webcookie);
        client.AddHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36");
        String Response = client.MakeGetRequest("https://www.instagram.com/" + username + "/?__a=1");
        return matcher.Match(Response, "profile_pic_url_hd\":\"<match>\"", false);
    }

    static String GetMediaJson(String MediaId){
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", logger.ApiCookie);
        String Response = client.MakeGetRequest("https://i.instagram.com/api/v1/media/" + MediaId + "/info/");
        return Response;
    }

}