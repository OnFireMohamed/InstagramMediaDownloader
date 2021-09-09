import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class HttpPostMultipart {
    private final String boundary;
    private static final String LINE = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;

    public HttpPostMultipart(String requestURL, String charset) throws Exception {
        this.charset = charset;
        boundary = UUID.randomUUID().toString();
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }

    public void addFilePart(String fieldName, byte[] uploadFile)
            throws IOException {
        String fileName = String.format("%s.mp4", new MohamedMatcher().GenerateRandom(12));
        writer.append("--" + boundary).append(LINE);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE);
        writer.append("Content-Transfer-Encoding: binary").append(LINE);
        writer.append(LINE);
        writer.flush();

        outputStream.write(uploadFile);
        outputStream.flush();
        writer.append(LINE);
        writer.flush();
    }

    public String send()  {
        String response = "", input = "";
        writer.flush();
        writer.append("--" + boundary + "--").append(LINE);
        writer.close();
        try {
            BufferedReader Reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            while ((input = Reader.readLine()) != null){
                response += input;
            }
        }
        catch (Exception e) {
            try {
                BufferedReader Reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
                while ((input = Reader.readLine()) != null){
                    response += input;
                }
            }
            catch (Exception ee) {

            }

        }
        return response;
    }
}