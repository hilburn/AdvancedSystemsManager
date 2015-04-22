package advancedsystemsmanager.helpers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class HttpPost implements Callable<String>
{
    private static final String ENCODING = "UTF-8";
    private HashMap<String, String> postData;
    private String url;

    public HttpPost(String url)
    {
        postData = new HashMap<String, String>();
        this.url = url;
    }

    public void put(String key, String value)
    {
        try
        {
            this.postData.put(URLEncoder.encode(key, ENCODING), URLEncoder.encode(value, ENCODING).replaceAll("%00", ""));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public String getPost()
    {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : postData.entrySet())
            builder.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        if (postData.size() > 0)
            builder.deleteCharAt(builder.length() - 1);
        return new String(builder);
    }

    public String getContents()
    {
        try
        {
            URL url = new URL(this.url);

            URLConnection connection = url.openConnection();

            connection.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(this.getPost());
            wr.flush();
            wr.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (builder.length() > 0)
                    builder.append('\n');
                builder.append(line);
            }
            reader.close();
            return new String(builder);
        } catch (MalformedURLException e)
        {
            throw new IllegalArgumentException("Malformed link: " + e);
        } catch (IOException e)
        {
            throw new RuntimeException("Failed to fetch contents from link: " + e);
        }
    }

    @Override
    public String call() throws Exception
    {
        return getContents();
    }
}
