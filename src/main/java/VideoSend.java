import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class VideoSend {
    private final String Cookie, _uid;
    private String url, userid;
    private String height = "540", width = "540";

    VideoSend(String Cookie){
        this._uid = new MohamedMatcher().GenerateRandom("46231100833".length(), "0987654321");
        this.Cookie = Cookie;
    }

    void Send(String url, String threadid, String userid, String Username){
        this.url = url;
        this.userid = userid;
        try {
            var time = "1619" + new MohamedMatcher().GenerateRandom(12, "0987654321");
            var uuid = UUID.randomUUID().toString();
            InputStream in = new URL(url).openStream();
            var bytes = in.readAllBytes();
            in.close();
            Upload(uuid, time);
            Upload1(time, uuid, bytes);
            Upload2(time, uuid);
            if(SendVideo(threadid, time)) {
                System.out.println("Video Saved by : " + Username + " At : "  + new Date(System.currentTimeMillis()));
            }
            else {
                MainClass.largeVideosWork(url, userid);
            }

        }catch (Exception f) {
            f.printStackTrace();
        }
    }
    void Send(String url, String threadid, String userid, String Username, String height, String width){
        this.url = url;
        this.userid = userid;
        // if ( height.length() > 1 ) {
        //     this.height = height;
        // }
        
        // if ( width.length() > 1 ) {
        //     this.width = width;
        // }
        try {
            // this.height = this.height.replace("}", "");
            // this.width = this.width.replace("}", "");
            var time = "1619" + new MohamedMatcher().GenerateRandom(12, "0987654321");
            var uuid = UUID.randomUUID().toString();
            InputStream in = new URL(url).openStream();
            var bytes = in.readAllBytes();
            in.close();
            Upload(uuid, time);
            Upload1(time, uuid, bytes);
            Upload2(time, uuid);
            if(SendVideo(threadid, time)) {
                System.out.println("Video Saved by : " + Username + " At : "  + new Date(System.currentTimeMillis()));
            }
            else {
                MainClass.largeVideosWork(url, userid);
            }

        }catch (Exception f) {

        }
    }

    private void Upload(String uuid, String Time){
        Requests client = new Requests();
        client.AddHeader("Segment-Start-Offset", "0");
        client.AddHeader("X-Instagram-Rupload-Params", "{\"upload_media_height\":\"" + height + "\",\"direct_v2\":\"1\",\"rotate\":\"0\",\"xsharing_user_ids\":\"[]\",\"upload_media_width\":\"" + width + "\",\"hflip\":\"false\",\"upload_id\":\"" + Time + "\",\"retry_context\":\"{\\\"num_step_auto_retry\\\":0,\\\"num_reupload\\\":0,\\\"num_step_manual_retry\\\":0}\",\"media_type\":\"2\"}");
        client.AddHeader("X_FB_VIDEO_WATERFALL_ID", uuid);
        client.AddHeader("Segment-Type", "3");
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", this.Cookie);
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("X-FB-HTTP-Engine", "Liger");

        String Response = client.MakeGetRequest("https://i.instagram.com/rupload_igvideo/" + uuid);
        System.out.print(Response + " | ");
    }



    private void Upload1(String Time, String uuid, byte[] videoBytes){
        Requests client = new Requests();
        client.AddHeader("Segment-Start-Offset", "0");
        client.AddHeader("X-Instagram-Rupload-Params", "{\"upload_media_height\":\"" + height + "\",\"direct_v2\":\"1\",\"rotate\":\"0\",\"xsharing_user_ids\":\"[]\",\"upload_media_width\":\"" + width + "\",\"hflip\":\"false\",\"upload_id\":\"" + Time + "\",\"retry_context\":\"{\\\"num_step_auto_retry\\\":0,\\\"num_reupload\\\":0,\\\"num_step_manual_retry\\\":0}\",\"media_type\":\"2\"}");
        client.AddHeader("Offset", "0");
        client.AddHeader("X-Entity-Name", uuid);
        client.AddHeader("X-Entity-Length", String.valueOf(videoBytes.length));
        client.AddHeader("Segment-Type", "3");
        client.AddHeader("X_FB_VIDEO_WATERFALL_ID", uuid);
        client.AddHeader("X-Entity-Type", "video/mp4");
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", this.Cookie);
        client.AddHeader("Content-Type", "application/octet-stream");
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("X-FB-HTTP-Engine", "Liger");

        String Response = client.MakePostRequest("https://i.instagram.com/rupload_igvideo/" + uuid, videoBytes);
        System.out.print(Response + " | ");


    }

    private void Upload2(String Time, String uuid){
        var postdata = "signed_body=SIGNATURE.{\"filter_type\":\"0\",\"timezone_offset\":\"28800\",\"_csrftoken\":\"" + new MohamedMatcher().Match(this.Cookie, "csrftoken=<match>;", false) + "\",\"source_type\":\"4\",\"video_result\":\"\",\"_uid\":\"" + this._uid + "\",\"device_id\":\"android-8cd32a1ba6669cbe\",\"_uuid\":\"" + UUID.randomUUID().toString() + "\",\"upload_id\":\"" + Time + "\",\"device\":{\"manufacturer\":\"Asus\",\"model\":\"ASUS_Z01QD\",\"android_version\":25,\"android_release\":\"7.1.2\"},\"audio_muted\":false,\"poster_frame_index\":0}";
        Requests client = new Requests();
        client.AddHeader("X-Bloks-Is-Panorama-Enabled", "true");
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", this.Cookie);
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("X-FB-HTTP-Engine", "Liger");
        client.AddHeader("Content-Length", String.valueOf(postdata.getBytes().length));

        String Response = client.MakePostRequest("https://i.instagram.com/api/v1/media/upload_finish/?video=1", postdata);
        System.out.print(Response + "\n");

    }

    private boolean SendVideo(String threadid,  String time){
        var ClientContext = new MohamedMatcher().GenerateRandom(19, "0987654321");
        String PostData = "action=send_item&is_shh_mode=0&thread_ids=[" + threadid + "]&send_attribution=direct_thread&client_context=" + ClientContext + "&_csrftoken=" + new MohamedMatcher().Match(this.Cookie, "csrftoken=<match>;", false) + "&video_result=&device_id=android-8cd32a1ba6669cbe&mutation_token=" + ClientContext + "&_uuid=" + UUID.randomUUID().toString() + "&upload_id=" + time + "&offline_threading_id=" + ClientContext;
        Requests client = new Requests();
        client.AddHeader("User-Agent", "Instagram 177.0.0.30.119 Android (25/7.1.2; 191dpi; 576x1024; Asus; ASUS_Z01QD; ASUS_Z01QD; intel; en_US; 276028020)");
        client.AddHeader("Accept-Language", "en-US");
        client.AddHeader("Cookie", this.Cookie);
        client.AddHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        client.AddHeader("Host", "i.instagram.com");
        client.AddHeader("X-FB-HTTP-Engine", "Liger");
        client.AddHeader("Content-Length", String.valueOf(PostData.getBytes().length));

        String Response = client.MakePostRequest("https://i.instagram.com/api/v1/direct_v2/threads/broadcast/configure_video/", PostData);
        System.out.println(Response);
        if(Response.contains("VideoSourceError\",\"status\":\"fail\""))
            return false;
        else if (Response.contains("\"status\":\"ok\""))
            return true;
        return true;

    }
}
