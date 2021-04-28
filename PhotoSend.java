import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class PhotoSend {
    private String Cookie;
    private final String UserAgent = "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)";

    PhotoSend(String Cookie){
        this.Cookie = Cookie;
    }

    void System(String Url, String time, String username, String senderusername, String threadid){
        try {
            InputStream in = new URL(Url).openStream();
            var bytes = in.readAllBytes();
            Upload(bytes, time);
            Send(threadid, time);
            DeleteChat(threadid);
            System.out.println("Avatr of : @" + username + " has been saved by : @" + senderusername + " At : " + new Date(System.currentTimeMillis()));
        }
        catch (Exception e){

        }

    }

    void Upload(byte[] photoBytes, String time){
        var path = time + "_0_" + new MohamedMatcher().GenerateRandom(10, "0987654321");
        Requests client = new Requests();
        client.AddHeader("User-Agent", this.UserAgent);
        client.AddHeader("Content-Type", "application/octet-stream");
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("X_FB_PHOTO_WATERFALL_ID", UUID.randomUUID().toString());
        client.AddHeader("X-Entity-Type", "image/jpeg");
        client.AddHeader("Offset", "0");
        client.AddHeader("X-Instagram-Rupload-Params", " {\"retry_context\":\"{\\\"num_step_auto_retry\\\":0,\\\"num_reupload\\\":0,\\\"num_step_manual_retry\\\":0}\",\"media_type\":\"1\",\"upload_id\":\"" + time + "\",\"xsharing_user_ids\":\"[]\",\"image_compression\":\"{\\\"lib_name\\\":\\\"moz\\\",\\\"lib_version\\\":\\\"3.1.m\\\",\\\"quality\\\":\\\"0\\\"}\"}");
        client.AddHeader("X-Entity-Name", path);
        client.AddHeader("Cookie", this.Cookie);
        client.AddHeader("X-Entity-Length", String.valueOf(photoBytes.length));
        client.AddHeader("Accept-Language", "en-US");
        String Response = client.MakePostRequest("https://i.instagram.com/rupload_igphoto/" + path, photoBytes);
    }

    void Send(String threadid, String time){
        Requests client = new Requests();
        client.AddHeader("User-Agent", this.UserAgent);
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Cookie", this.Cookie);
        client.AddHeader("Accept-Language", "en-US");
        String Response = client.MakePostRequest("https://i.instagram.com/api/v1/direct_v2/threads/broadcast/configure_photo/", "action=send_item&is_shh_mode=0&thread_ids=[" + threadid + "]&send_attribution=inbox&_uuid=" + UUID.randomUUID().toString() + "&allow_full_aspect_ratio=true&upload_id=" + time);
    }

    void DeleteChat(String id){
        Requests client = new Requests();
        client.AddHeader("User-Agent", this.UserAgent);
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded");
        client.AddHeader("Cookie", this.Cookie);
        String Response = client.MakePostRequest("https://i.instagram.com/api/v1/direct_v2/threads/" + id + "/hide/", "");
    }
}
