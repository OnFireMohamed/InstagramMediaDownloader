import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class MainClass {
    static Scanner scanner;
    static LoginClass logger;
    static MohamedMatcher matcher;
    static Random ran;
    //thread = 340282366841710300949128144870889549215
    public static void main(String[] args) {

        ran = new Random();
        matcher = new MohamedMatcher();
        logger = new LoginClass();
        scanner = new Scanner(System.in);
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
        Response = Response.replace("\": ", "\":");
        try{
           FileWriter writer = new FileWriter(new File("response.txt"), false);
           writer.write(Response);
           writer.close();

        }catch (Exception e){}
        ArrayList<String> users = new ArrayList<>();
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
                var username = matcher.Match(v1, "\"username\":\"<match>\",", false);
                if (users.contains(username) == false){
                    users.add(username);
                    if (v1.contains("Story Unavailable") && v1.contains("This story is hidden because")){
                        var user = matcher.Match(v1, "hidden because <match> has" , false).split("@")[1];
                        var Json = GetaccountJson(user, logger.Webcookie);
                        var userid = matcher.Match(Json, "profilePage_<match>\"", false);
                        var senderuserid = matcher.Match(v1, "\"pk\":<match>,", false);
                        if (! userid.equals("")){


                            if (Follow(userid)){
                                System.out.println(String.format("Follow Requested to %s by %s", user, username));
                                SendMessage(senderuserid, String.format("I cannot download this story because @%s has a private account.\nSo, I request a follow request to @%s", user, user));
                            }
                            else
                                SendMessage(senderuserid, String.format("I cannot download this story because @%s has a private account and I can't request follow to the account", user));
                                System.out.println("cannot follow : @" + user);
                        }

                    }
                    else if(v1.contains("video_versions") && !v1.contains("product_type\":\"igtv")){

                        var threadid = matcher.Match(v1, "\":\"<match>\",", false);
                        var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                        var itemtype = matcher.Match(v1, "\"item_type\":\"<match>\",", false);
                        var mediaid = matcher.Match(v1, "media_id\":<match>,", false);
                        var timestamp = matcher.Match(v1, "timestamp\":<match>," , false);
                        var clientcontext = matcher.Match(v1, "client_context\":\"<match>\"", false);
                        switch (itemtype){
                            case "story_share", "felix_share", "clip":
                            {
                                var url = matcher.Unescape(matcher.Match(v1.split("video_versions")[1], "\"url\":\"<match>\"" , false));
                                new VideoSend(logger.ApiCookie).Send(url, threadid, userid, username);
                                if(itemtype.equals("story_share")){
                                    var text = matcher.Unescape(matcher.Match(v1, "text\":\"<match>\"", false));

                                    if (text != "" && text.contains("@")){
                                        var splited = text.replace("\n", " ").split(" ");
                                        StringBuilder messageBuilder = new StringBuilder();
                                        for (var reader : splited) {
                                            if (reader.startsWith("@"))
                                                messageBuilder.append(String.format("%s\n", reader));
                                        }
                                        var s = "The Mentions:\n\n" + messageBuilder.toString();
                                        SendMessage(userid, s);
                                    }
                                }
                                break;
                            }
                            case "media_share":{
                                var Json = GetMediaJson(mediaid);
                                var str = Json.split("video_versions");
                                ArrayList<String> UrlsList = new ArrayList<String>();
                                if(! Json.contains("<!DOCTYPE html><html lang=\"en\" class=\"no-js logged-in client-root touch\">")){
                                    for (String h : str){
                                        if(h.contains(".mp4") && h.contains("\"url\"")){
                                            var url = matcher.Unescape(matcher.Match(h, "url\":\"<match>\"", false));
                                            if (url.contains(".mp4")){
                                                UrlsList.add(url);
                                            }
                                        }
                                    }
                                }
                                else {
                                    var url = matcher.Unescape(matcher.Match(v1.split("video_versions")[1], "url\":\"<match>\"", false));
                                    if (url.contains(".mp4")){
                                        UrlsList.add(url);
                                    }
                                }

                                for (String url : UrlsList) {
                                    new VideoSend(logger.ApiCookie).Send(url, threadid, userid, username);
                                    try {Thread.sleep(2000);}catch(Exception e){}
                                }

                                break;
                            }

                        }

                    }
                    else if(v1.contains("product_type\":\"igtv")){
                        var url = matcher.Unescape(matcher.Match(v1.split("video_versions")[1], "\"url\":\"<match>\"" , false));
                        var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                        largeVideosWork(url, userid);

                    }
                    else if (v1.contains("\"item_type\":\"text\",") || v1.contains("\"item_type\":\"link\",")){
                        var text = matcher.Unescape(matcher.Match(v1, "text\":\"<match>\"", false));
                        var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                        var threadid = matcher.Match(v1, "\":\"<match>\",", false);
                        var clientcontext = matcher.Match(v1, "client_context\":\"<match>\"", false);
                        if(text.contains("!find ")){

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
                        else if(text.contains("tiktok.com")){
                            String url = text;
                            if (url.contains("?")) {
                                url = url.split("\\?")[0];
                            }
                            if (!url.endsWith("/"))
                                url += "/";
                            String finalUrl = url;
                            new Thread(() -> {
                                var json = new Requests().MakeGetRequest("https://hamod.ga/api/tiktokWithoutWaterMark.php?u=" + finalUrl);
                                if (! json.contains("\"link\": null,")) {
                                    var video_url = matcher.Match(json, "link\": \"<match>\"", false);
                                    new VideoSend(logger.ApiCookie).Send(URLDecoder.decode(video_url), threadid, userid, username);
                                    System.out.println("downloads tiktok video : @" + username);
                                }
                                else {
                                    SendMessage(userid, "Check the url and try again");
                                }
                            }).start();

                        }
                        else if (text.contains("twitter.com")){
                            var url = text;
                            if (url.contains("?"))
                                url = url.split("\\?")[0];
                            Requests cclient = new Requests();
                            cclient.AddHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36");
                            cclient.AddHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                            cclient.AddHeader("Accept-Language", "en-US,en;q=0.9");
                            cclient.AddHeader("Content-Type", "application/x-www-form-urlencoded");
                            cclient.AddHeader("cache-control", "no-cache");
                            cclient.AddHeader("cookie", "_ga=GA1.2.1403882966.1629021684; _gid=GA1.2.457353459.1629021684; __gads=ID=3e04cf18524216d1-22a12989acc900f5:T=1629021684:RT=1629021684:S=ALNI_MYRfCQfLCkZFFG-KXL0xlmnfJyv-g; _gat_gtag_UA_105090590_3=1; XSRF-TOKEN=eyJpdiI6Im9lUUNMZlwvXC9RTVI1U3hCMUlWZGszdz09IiwidmFsdWUiOiJ1SlRpXC9VOXA0aHRUa0lXbXZRT1lSMmxQSVhFS2RMMzNENzAzY1Zmc0MyUzNGaU1cL1hLNmJTOTI5SUpKQklYM3dDcGN4Y28xbmR4YlpISmV3QjkyWlhRPT0iLCJtYWMiOiIxZTE3NDE0MWQ5MDFhMDJlMDViMGYzM2Q3NGJiY2JlNDQwMjNiNDUwZWYyYzRhZjRjYTQ2ODcwNjdhNTY3Zjk4In0%3D; laravel_session=eyJpdiI6ImpMMGVpXC91UENwY1VQdmtHQTdyeHJBPT0iLCJ2YWx1ZSI6IktaYTgyZUF1Sit6QXorT1VMZm9VWldzVElEbjFMVitFV1prdHlVUEROd0ZqU1FhcUNmcHhaRjVmejJGMHdCZit0T2tsVkdRS1k0Q05EK29TMWRxVEpnPT0iLCJtYWMiOiJmN2MxNTUxYjk1MWQwZjcxZmJhYjVkMDQ1YjUzY2ViOGIxMDM1ZDI5NGQ4YmUzY2I3NjZlODk1ZTNkNzkzN2IxIn0%3D; __atuvc=5%7C33; __atuvs=6118e5f3fc45479b004; __atssc=google%3B3");
                            String Responsee = cclient.MakePostRequest("https://www.savetweetvid.com/downloader", "url=" + url);
                            if (Responsee.contains(".mp4")) {
                                var video_url = matcher.Match(Responsee, "<a class=\"dropbox-saver\" href=\"<match>\"></a>", false);
                                System.out.println(video_url);
                                largeVideosWork(video_url, userid);
                                System.out.println("Downloads twitter video : @" + username);
                            }
                            else
                                SendMessage(userid, "Check the url and try again..");
                        }
                        else if (text.contains("youtube.com") || text.contains("youtu.be")) {
                            String url = text;
                            if (url.contains("?"))
                                url = url.split("\\?")[0];
                            var videoUrl = getYoutubeVideoUrl(url);
                            if (! videoUrl.equals("bad")) {
                                largeVideosWork(videoUrl, userid);
                            }
                            else
                                SendMessage(userid, "Check the url and try again");
                        }
                        else if (text.contains("pinterest.com") || text.contains("pin.it")) {
                            String url = text;
                            var vidUrl = getPinterestVideoUrl(url);
                            if (! vidUrl.equals("bad")) {
                                new VideoSend(logger.ApiCookie).Send(vidUrl, threadid, userid, username);
                                System.out.println("downlaods pinterest video : @" + username);
                            }
                            else {
                                SendMessage(userid, "Check the url and try again.");
                            }
                        }
                    }
                    else if(v1.contains("\"item_type\":\"story_share\",")){
                        var text = matcher.Unescape(matcher.Match(v1, "text\":\"<match>\"", false));
                        var userid = matcher.Match(v1, "\"pk\":<match>,", false);
                        if (text != "" && text.contains("@")){
                            var splited = text.replace("\n", " ").split(" ");
                            StringBuilder messageBuilder = new StringBuilder();
                            for (var reader : splited) {
                                if (reader.startsWith("@"))
                                    messageBuilder.append(String.format("%s\n", reader));
                            }
                            var s = "The Mentions:\n\n" + messageBuilder.toString();
                            SendMessage(userid, s);
                            System.out.println("Stories by @" + username + "At : " + new Date(System.currentTimeMillis()));
                        }

                    }
                    else if(v1.contains("\"item_type\":\"profile\",")){
                        var threadid = matcher.Match(v1, "\":\"<match>\",", false);
                        var SenderUsername = matcher.Match(v1, "\"username\":\"<match>\",", false);
                        var SenderUserId = matcher.Match(v1, "\"pk\":<match>,", false);
                        var array = v1.split("\"item_type\":\"profile\",");
                        var user_name = matcher.Match(array[1], "\"username\":\"<match>\",", false);
                        var userid = matcher.Match(array[1], "\"pk\":<match>,", false);
                        var AccountJson = GetaccountJson(user_name, logger.Webcookie);
                        var url = matcher.Match(AccountJson, "profile_pic_url_hd\":\"<match>\"", false);
                        if(AccountJson.contains("is_private\":true") && AccountJson.contains("followed_by_viewer\":false") && AccountJson.contains("requested_by_viewer\":false")){

                            if (Follow(userid) == true){
                                System.out.println("Follow Requested : @" + user_name);
                                SendMessage(SenderUserId, "Follow Requested : @" + user_name);
                            }

                            else{
                                System.out.println("Can't Request Follow : @" + user_name);
                                SendMessage(SenderUserId, "Can't Request Follow : @" + user_name);
                            }
                        }
                        if(url != null || url != ""){
                            new PhotoSend(logger.ApiCookie).System(url, user_name, SenderUsername, threadid);
                        }

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
    static String GetaccountJson(String username, String cookie){

        Requests client = new Requests();
        client.AddHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        client.AddHeader("accept-language", "en-US,en;q=0.9");
        client.AddHeader("cookie", cookie);
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

    public static void largeVideosWork(String url, String user_id) {
        try{

            new Thread(() -> {
                SendMessage(user_id, "Wait until I finish video download url preparation..");
                try {
                    HttpPostMultipart multipart = new HttpPostMultipart("https://api.anonfiles.com/upload", "utf-8");
                    multipart.addFilePart("file", new URL(url).openStream().readAllBytes());
                    String val = multipart.send();
                    if (val.contains("\"status\":true")) {
                        var download_url = "https://shahidturk.xyz/find/?url=" + new MohamedMatcher().Match(val, "full\":\"<match>\"", false);
                        var text = "The video is more than a minute long, So you can download it from this url : \n\n" + download_url + "\n\n Notice : open the url from safari or another browser - Not Instagram Browser -";
                        SendMessage(user_id, text);
                    }
                    else
                        System.out.println("CCC");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }
        catch (Exception e) {

        }

    }
    private static String getYoutubeVideoUrl(String URL) {
        var returnVal = "bad";
        try {
            List<String> infoList = new ArrayList<String>();
            var videoJson = new Requests().MakeGetRequest("https://api.snappea.com/v1/video/details?url=" + URL);
            if (videoJson.contains("statusDescription\":\"success")) {
                for (String reader : videoJson.split("formatExt")) {
                    if (reader.contains("\"mime\":\"video\"")) {
                        var quality = (matcher.Match(reader, "formatAlias\":\"<match>\"", false)).toLowerCase().split("p")[0];
                        var url = matcher.Match(reader.split("\"urlList\":\\[")[1], "\"<match>\"", false);
                        if (url.length() > 2 && quality.length() > 2){
                            infoList.add(quality + "|" + url);
                        }
                    }
                }
                int[] qualityArray = new int[infoList.size()];
                for (int i = 0; i < infoList.size(); i += 1) {
                    qualityArray[i] = Integer.parseInt(infoList.get(i).split("\\|")[0]);
                }
                var highestQuality = Arrays.stream(qualityArray).max().getAsInt();
                for (var nReader : infoList) {
                    if (nReader.contains(String.valueOf(highestQuality))) {
                        returnVal = nReader.split("\\|")[1];
                        System.out.println(nReader.split("\\|")[0]);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }
    private static String getPinterestVideoUrl(String url) {
        Requests client = new Requests();
        client.AddHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        client.AddHeader("accept-language", "en-US,en;q=0.9");
        client.AddHeader("content-type", "application/x-www-form-urlencoded");
        client.AddHeader("cookie", "_ga=GA1.2.1006167198.1631170804; _gid=GA1.2.1540034248.1631170804; __gads=ID=eb5b29f75fdb7719-2260d78be2ca003c:T=1631170804:RT=1631170804:S=ALNI_MYTqdEASfkbObHPR8vrZdVgSGCLNw; _gat_gtag_UA_178031006_1=1");
        client.AddHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");

        String Response = client.MakePostRequest("https://pinterestvideodownloader.com/", "url=" + url);
        if (Response.contains("<video")) {
            var videoUrl = new MohamedMatcher().Match(Response, "<video src=\"<match>\"", false);
            return videoUrl;
        }
        else
            return "bad";
    }
}