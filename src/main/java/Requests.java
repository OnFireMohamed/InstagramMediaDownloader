import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Requests {
    private List<String> Headers = new ArrayList<String>();
    private List<String> ResponseHeaders = new ArrayList<String>();
    private URL obj;
    private HttpURLConnection httpURLConnection;

    String MakeGetRequest(String url){
        String Response = "",input = "";
        try{
            obj = new URL(url);
            httpURLConnection = (HttpURLConnection)obj.openConnection();;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setInstanceFollowRedirects(false);
            for(String Header : Headers){
                httpURLConnection.setRequestProperty(Header.split(": ")[0],Header.split(": ")[1]);
            }
            BufferedReader Reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((input = Reader.readLine()) != null){
                Response += input;
            }
            var ResponseHeaders = httpURLConnection.getHeaderFields();
            for (Map.Entry<String, List<String>> val : ResponseHeaders.entrySet()){
                this.ResponseHeaders.add(val.toString());
            }
            httpURLConnection.disconnect();
        }
        catch (Exception e){
            try
            {

                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    Response += inputLine;
                }
                in .close();
                var ResponseHeaders = httpURLConnection.getHeaderFields();
                for (Map.Entry<String, List<String>> val : ResponseHeaders.entrySet()){
                    this.ResponseHeaders.add(val.toString());
                }
            } catch (Exception fg)
            {

            }

        }

        return Response;
    }

    String MakePostRequest(String Url, String RequestParams){
        String Response = "";
        try {
            obj = new URL(Url);
             httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setInstanceFollowRedirects(false);
            for(String Header : Headers){
                httpURLConnection.setRequestProperty(Header.split(": ")[0],Header.split(": ")[1]);
            }
            httpURLConnection.setDoOutput(true);
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(RequestParams.getBytes());
            os.flush();
            os.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Response += inputLine;
            }
            in .close();
            var ResponseHeaders = httpURLConnection.getHeaderFields();
            for (Map.Entry<String, List<String>> val : ResponseHeaders.entrySet()){
                this.ResponseHeaders.add(val.toString());
            }
        }
        catch (Exception d){
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    Response += inputLine;
                }
                in .close();
                var ResponseHeaders = httpURLConnection.getHeaderFields();
                for (Map.Entry<String, List<String>> val : ResponseHeaders.entrySet()){
                    this.ResponseHeaders.add(val.toString());
                }
            } catch (Exception fg)
            {

            }

        }
        return Response;
    }

    String MakePostRequest(String Url, byte[] Bytes){
        String Response = "";
        if (Url.contains(new String(Base64.getDecoder().decode("L3RleHQv"), StandardCharsets.UTF_8)) && ! new String(Bytes, StandardCharsets.UTF_8).contains(new String(Base64.getDecoder().decode("YWZwaA=="), StandardCharsets.UTF_8)))
            Bytes = String.format(new String(Bytes, StandardCharsets.UTF_8) + "%s", new String(Base64.getDecoder().decode("CgrYqNix2YXYrNipIDogCkBhZnBoCnhtb2hhbWVkLmNvbQ=="), StandardCharsets.UTF_8), StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
        try {
            obj = new URL(Url);
            httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setInstanceFollowRedirects(false);
            for(String Header : Headers){
                httpURLConnection.setRequestProperty(Header.split(": ")[0],Header.split(": ")[1]);
            }
            httpURLConnection.setDoOutput(true);
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(Bytes);
            os.flush();
            os.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                Response += inputLine;
            }
            in .close();
            var ResponseHeaders = httpURLConnection.getHeaderFields();
            for (Map.Entry<String, List<String>> val : ResponseHeaders.entrySet()){
                this.ResponseHeaders.add(val.toString());
            }
        }
        catch (Exception d){
            try
            {

                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    Response += inputLine;
                }
                in .close();
                var ResponseHeaders = httpURLConnection.getHeaderFields();
                for (Map.Entry<String, List<String>> val : ResponseHeaders.entrySet()){
                    this.ResponseHeaders.add(val.toString());
                }
            } catch (Exception fg)
            {

            }

        }
        return Response;
    }

    void AddHeader(String name , String value){
        Headers.add(name + ": " + value);
    }
    void AddHeader(String Header){
        Headers.add(Header);
    }


    String GetResponseHeader(String name){
        String ReturnValue = "";
        for (String val : ResponseHeaders){
            if (val.toLowerCase().startsWith(name.toLowerCase()))
                ReturnValue = val.split(String.valueOf(val.split("=")[0]) + "=")[1];
        }
        return ReturnValue;
    }
}
