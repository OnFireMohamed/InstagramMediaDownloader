import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class checkDirect {
    private String cookie;
    private MohamedMatcher matcher;
    private Random ran;

    protected checkDirect(String cookie) {
        this.cookie = cookie;
        matcher = new MohamedMatcher();
        this.ran = new Random();
    }

    public void mainSystem() throws Exception{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://i.instagram.com/api/v1/direct_v2/inbox/?visual_message_return_type=unseen&thread_message_limit=1&use_unified_inbox=true")
                .header("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Accept-Language", "en-US")
                .header("Cookie", this.cookie)
                .build();
        Call call = client.newCall(request);
        var Response = call.execute().body().string();
        Response = Response.replace("\": ", "\":");
        try{
            FileWriter writer = new FileWriter(new File("response.txt"), false);
            writer.write(Response);
            writer.close();
        }catch (Exception e){}
        try {
            JSONObject obj = new JSONObject(Response);
            var totalPending = obj.getInt("pending_requests_total");
            if (totalPending > 0) {
                var pendingObj = obj.getJSONObject("most_recent_inviter");
                var username = pendingObj.get("username");
                var user_id = pendingObj.get("pk");
                MainClass.SendMessage(String.valueOf(user_id), "Activated Successfully : @" + username);
                System.out.println("Activated Successfully : @" + username);
            }
            JSONArray threads = obj.getJSONObject("inbox").getJSONArray("threads");
            for (int i = 0; i < threads.length(); i++) {
                try {
                    var thread = threads.getJSONObject(i);
                    var thread_id = thread.getString("thread_id");
                    var username = thread.getJSONObject("inviter").getString("username");
                    var user_id = String.valueOf(thread.getJSONObject("inviter").get("pk"));
                    var item = thread.getJSONArray("items").getJSONObject(0);
                    var item_type = item.getString("item_type");
                    if (item_type.equals("clip") || item_type.equals("media_share") || item_type.equals("felix_share") ) {
                        normalVideoWork(item, item_type, user_id, username, thread_id);
                    }
                    else if (item_type.equals("story_share")) {
                        storyWork(item, item_type, user_id, username, thread_id);
                    }
                    else if (item_type.equals("link") || item_type.equals("text")) {
                        var text = matcher.Match(item.toString(), "\"text\":\"<match>\"", false);
                        links_and_textWork(text, user_id, username, thread_id);
                    }
                    else if (item_type.equals("profile")) {
                        System.out.println(item_type);

                        JSONObject profile = item.getJSONObject("profile");
                        profileWork(profile, user_id, username, thread_id);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
        catch (Exception e) {

        }

    }
    private void normalVideoWork(JSONObject item, String item_type,String user_id, String username, String thread_id) throws Exception {
        if (item.toString().contains("video_versions")) {
            if (item_type.equals("clip")) {
                var url = matcher.Match(item.getJSONObject("clip").toString().split("video_versions")[1], "url\":\"<match>\"", false);
                if ( ! url.equals("")) {
                    new VideoSend(this.cookie).Send(url, thread_id, user_id, username);
                }
            }
            else if(item_type.equals("media_share") && item.toString().contains("carousel_media")) {
                var array = item.getJSONObject("media_share").getJSONArray("carousel_media");
                var video_id = item.getJSONObject("media_share").get("carousel_share_child_media_id");
                for (int i = 0; i < array.length(); i++) {
                    try {
                        var id = array.getJSONObject(i).get("id");
                        if (String.valueOf(id).equals(String.valueOf(video_id))) {
                            var url = array.getJSONObject(i).getJSONArray("video_versions").getJSONObject(0).get("url");
                            if ( ! url.equals("")) {
                                new VideoSend(this.cookie).Send(String.valueOf(url), thread_id, user_id, username);
                            }
                        }

                    }catch (Exception e){}
                }
            }
            else {
                var url = matcher.Match(item.toString().split("video_versions")[1], "url\":\"<match>\"", false);
                if ( ! url.equals("")) {
                    new VideoSend(this.cookie).Send(url, thread_id, user_id, username);
                }
            }

        }


    }

    private void storyWork(JSONObject item, String item_type, String user_id, String username, String thread_id) throws Exception{
        if ( item.toString().contains("video_versions") ) {
            normalVideoWork(item, item_type, user_id, username, thread_id);
        }
        var text = matcher.Match(item.toString(), "\"text\":\"<match>\"", false);
        if (text != "" && text.contains("@")){
            var splited = text.replace("\n", " ").split(" ");
            StringBuilder messageBuilder = new StringBuilder();
            for (var reader : splited) {
                if (reader.startsWith("@"))
                    messageBuilder.append(String.format("%s\n", reader));
            }
            var s = "The Mentions:\n\n" + messageBuilder.toString();
            MainClass.SendMessage(user_id, s);
        }
    }
    private void links_and_textWork(String text, String user_id, String username, String thread_id) throws Exception {
        if (text.contains("find ")) {
            try {
                text = text.replace(" ", "");
                var number = text.split(":")[1];
                var country = text.split("find")[1].split(":")[0];
                if (country.length() > 1 && number.length() > 1) {
                    var rsp = new Requests().MakeGetRequest("http://146.148.112.105/caller/index.php/UserManagement/search_number?number=" + number + "&country_code=" + country.toUpperCase());
                    if(rsp.contains("name")){
                        var name = matcher.Unescape(matcher.Match(rsp, "name\":\"<match>\"", false));
                        if(name != null || name != ""){
                            var s = "The Handler Of : " + number + "\n\nIs : " + name;
                            MainClass.SendMessage(user_id, s);
                            System.out.println(number + " by @" + username);
                        }
                    }
                    else{
                        var s = "No Number Found For : " + number;
                        MainClass.SendMessage(user_id, s);
                    }
                }
            }
            catch (Exception e) {

            }

        }
        else if (text.contains("tiktok.com")) {
            System.out.println("inside tiktok work");
            String url = text;
            if (url.contains("?")) {
                url = url.split("\\?")[0];
            }
            if (!url.endsWith("/"))
                url += "/";
            String finalUrl = url;
            new Thread(() -> {
                var token = matcher.Match(new Requests().MakeGetRequest("https://tiktok-downloader.online/"), "<meta name=\"_token_\" content=\"<match>\">", false);
                if (token != "" || ! token.equals("")) {
                    Requests client = new Requests();
                    client.AddHeader("token", token);
                    var jUrl = new JSONObject(client.MakeGetRequest("https://tiktok-downloader.online/api/v1/fetch?url=" + finalUrl)).get("url_nwm");
                    if (! jUrl.equals("")) {
                        new VideoSend(this.cookie).Send(URLDecoder.decode(String.valueOf(jUrl)), thread_id, user_id, username);
                    }
                }
                else {
                    MainClass.SendMessage(user_id, "Check the url and try again");
                }
            }).start();
        }
        else if (text.contains("twitter.com")) {
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
                MainClass.largeVideosWork(video_url, user_id);
                System.out.println("Downloads twitter video : @" + username);
            }
            else
                MainClass.SendMessage(user_id, "Check the url and try again..");
        }
        else if (text.contains("youtube.com") || text.contains("youtu.be")) {
            String url = text;
            if (url.contains("?"))
                url = url.split("\\?")[0];
            var videoUrl = getYoutubeVideoUrl(url);
            System.out.println(videoUrl);
            MainClass.largeVideosWork(videoUrl, user_id);
        }
        else if (text.contains("pinterest.com") || text.contains("pin.it")) {
            String url = text;
            var vidUrl = getPinterestVideoUrl(url);
            if (! vidUrl.equals("bad")) {
                new VideoSend(this.cookie).Send(vidUrl, thread_id, user_id, username);
                System.out.println("downlaods pinterest video : @" + username);
            }
            else {
                MainClass.SendMessage(user_id, "Check the url and try again.");
            }
        }
    }
    private String getYoutubeVideoUrl(String URL) throws Exception{
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
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }
    private String getPinterestVideoUrl(String url) throws Exception {
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
    private void profileWork(JSONObject item, String user_id, String username, String thread_id) throws Exception{
        var pk = String.valueOf(item.get("pk"));
        final String USERNAME = String.valueOf(item.get("username"));
        if (pk != "" || pk != null) {
            JSONObject info = new JSONObject(MainClass.makeGetRequest(String.format("https://i.instagram.com/api/v1/users/%s/info/", pk)));
            var array = info.getJSONObject("user").getJSONArray("hd_profile_pic_versions").toString();
            var url = matcher.Match(array, "url\":\"<match>\"", false);

            if (url != null || url != "") {
                new PhotoSend(this.cookie).System(url, username, USERNAME, thread_id);
            }
        }
    }
}