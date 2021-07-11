import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainClass {
    static Scanner scanner;
    static LoginClass logger;
    static MohamedMatcher matcher;
    static Random ran;



    //Methods
    public static void main(String[] args) {
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
                    try{Thread.sleep(25000);}catch (Exception e){}

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
        try{
           FileWriter writer = new FileWriter(new File("response.txt"), false);
           writer.write(Response);
           writer.close();

        }catch (Exception e){}

        try{
            if(! Response.contains("pending_requests_total\":0,"))
            {
                var Pattern = Response.split("pending_requests_total")[1];
                var UserId = matcher.Match(Pattern, "pk\":<match>,", false);
                var UserName = matcher.Match(Pattern, "username\":\"<match>\"", false);
                SendMessage(UserId, "Activated Successfully : @" + UserName);
                System.out.println("Activated Successfully : @" + UserName);
            }
            if (! Response.contains("username")){
                JOptionPane.showMessageDialog(null, "[ + ] Error At : " + new Date(System.currentTimeMillis()));
            }
            String[] v = Response.split("thread_id");
            for(String v1 : v){
                  if (v1.contains("Story Unavailable") && v1.contains("This story is hidden because")){
                    var user = matcher.Match(v1, "hidden because <match> has" , false).split("@")[1];
                    var Json = GetaccountJson(user);
                    var userid = matcher.Match(Json, "profilePage_<match>\"", false);
                    System.out.println("Json\n" + Json);
                    var username = matcher.Match(v1, "\"username\":\"<match>\",", false);
                    var senderuserid = matcher.Match(v1, "\"pk\":<match>,", false);

                    if (! userid.equals("")){


                        if (Follow(userid)){
                            System.out.println("Follow Requested");
                            System.out.println(String.format("Follow Requested to %s by %s", user, username));
                            SendMessage(senderuserid, String.format("I cannot download this story because @%s has a private account.\nSo, I request a follow request to @%s", user, user));
                        }
                        else
                            System.out.println("cannot follow : @" + user);
                    }
                }
                else if(v1.contains("video_versions") && !v1.contains("product_type\":\"igtv")){

                    var threadid = matcher.Match(v1, "\":\"<match>\",", false);
                    var username = matcher.Match(v1, "\"username\":\"<match>\",", false);
                    var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                    var itemtype = matcher.Match(v1, "\"item_type\":\"<match>\",", false);
                    var mediaid = matcher.Match(v1, "media_id\":<match>,", false);
                    var timestamp = matcher.Match(v1, "timestamp\":<match>," , false);
                    var clientcontext = matcher.Match(v1, "client_context\":\"<match>\"", false);
                    switch (itemtype){
                        case "story_share", "felix_share", "clip":
                        {
                            var url = matcher.Unescape(matcher.Match(v1.split("video_versions")[1], "\"url\":\"<match>\"" , false));
                            new VideoSend(logger.ApiCookie).Send(url, threadid, clientcontext, username);
                            if(itemtype.equals("story_share")){
                                var text = matcher.Unescape(matcher.Match(v1, "text\":\"<match>\"", false));
                                if (text != "" && text.contains("@")){
                                    var s = "The Mentions(text):\n\n" + text ;
                                    SendMessage(userid, s);
                                }
                            }
                            break;
                        }
                        case "media_share":{
                            var Json = GetMediaJson(mediaid);
                            var str = Json.split("video_versions");
                            ArrayList<String> UrlsList = new ArrayList<String>();

                            for (String h : str){
                                if(h.contains(".mp4") && h.contains("\"url\"")){
                                    var url = matcher.Unescape(matcher.Match(h, "url\":\"<match>\"", false));
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
                else if(v1.contains("product_type\":\"igtv")){
                    var text = "IGTV Can't be Downloaded.";
                    var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                    var username = matcher.Match(v1, "\"username\":\"<match>\",", false);
                    SendMessage(userid, text);
                    System.out.println("Trys To download IGTV: @" + username);
                }
                else if (v1.contains("\"item_type\":\"text\",")){
                    var text = matcher.Unescape(matcher.Match(v1, "text\":\"<match>\"", false));
                    if(text.contains("!find ")){
                        var username = matcher.Match(v1, "\"username\":\"<match>\",", false);
                        var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                        var country = text.split(" ")[1].split(":")[0];
                        var number = text.split(":")[1];
                        var rsp = new Requests().MakeGetRequest("http://146.148.112.105/caller/index.php/UserManagement/search_number?number=" + number + "&country_code=" + country.toUpperCase());

                        if(rsp.contains("name")){
                            var name = matcher.Unescape(matcher.Match(rsp, "name\":\"<match>\"", false));
                            if(name != null || name != ""){
                                var s = "The Handler Of : " + number + "\n\nIs : " + name;
                                SendMessage(userid, s);
                                System.out.println(number + " by @" + username);
                            }
                        }
                        else{
                            var s = "No Number Found For : " + number;
                            SendMessage(userid, s );
                        }

                    }
                }
                else if(v1.contains("\"item_type\":\"story_share\",")){
                    var text = matcher.Unescape(matcher.Match(v1, "text\":\"<match>\"", false));
                    var username = matcher.Match(v1, "\"username\":\"<match>\",", false);
                    var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                    if (text != "" && text.contains("@")){
                        var s = "The Mentions(text):\n\n" + text  ;
                        SendMessage(userid, s);
                        System.out.println("Stories by @" + username + "At : " + new Date(System.currentTimeMillis()));
                    }
                }
                else if(v1.contains("\"item_type\":\"profile\",")){
                    var threadid = matcher.Match(v1, "\":\"<match>\",", false);
                    var SenderUsername = matcher.Match(v1, "\"username\":\"<match>\",", false);
                    var SenderUserId = matcher.Match(v1, "\"pk\":<match>,", false);
                    var array = v1.split("\"item_type\":\"profile\",");
                    var username = matcher.Match(array[1], "\"username\":\"<match>\",", false);
                    var userid = matcher.Match(array[1], "\"pk\":<match>,", false);
                    var AccountJson = GetaccountJson(username);
                    var url = matcher.Match(AccountJson, "profile_pic_url_hd\":\"<match>\"", false);



                     if(AccountJson.contains("is_private\":true") && AccountJson.contains("followed_by_viewer\":false") && AccountJson.contains("requested_by_viewer\":false")){

                         if (Follow(userid) == true){
                             System.out.println("Follow Requested : @" + username);
                             SendMessage(SenderUserId, "Follow Requested : @" + username);
                         }

                         else{
                             System.out.println("Can't Request Follow : @" + username);
                             SendMessage(SenderUserId, "Can't Request Follow : @" + username);
                         }


                     }



                if(url != null || url != ""){
                    new PhotoSend(logger.ApiCookie).System(url, username, SenderUsername, threadid);
                }

                }
            }
        }
        catch (Exception e){

        }

    }

    static void SendMessage(String userid, String text){
        if(!text.contains("Activated Successfully")) text += logger.s;
        var context = "6794371" + matcher.GenerateRandom(12, "0987654321");
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("Cookie", logger.ApiCookie);
        var postdata = "recipient_users=[[" + userid + "]]&action=send_item&is_shh_mode=0&send_attribution=inbox_new_message&client_context=6794371" + context + "&text=" + text + "&device_id=android-8cd32a1ba6669cbe&mutation_token=6794371" + context + "&_uuid=" + UUID.randomUUID().toString() + "&offline_threading_id=6794371" + context;

        try{String Response = client.MakePostRequest("https://i.instagram.com/api/v1/direct_v2/threads/broadcast/text/", postdata.getBytes(StandardCharsets.UTF_8));}catch (Exception re){}
    }

    static String GetaccountJson(String username){

        Requests client = new Requests();
        client.AddHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        client.AddHeader("accept-language", "en-US,en;q=0.9");
        client.AddHeader("cookie", logger.Webcookie);
        client.AddHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36");
        String Response = client.MakeGetRequest("https://www.instagram.com/" + username + "/?__a=1");
        return Response;
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
