package com.datapath.telepath;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class WebInterface {

	public static String getUrl(String stringUrl){
		URL url;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException e) {
			return "MU";
		}
		URLConnection con;
		try {
			con = url.openConnection();
		} catch (IOException e) {
			return "CNOC";
		}
		Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
		Matcher m = p.matcher(con.getContentType());
		/* If Content-Type doesn't match this pre-conception, choose default and 
		 * hope for the best. */
		String charset = m.matches() ? m.group(1) : "ISO-8859-1";
		Reader r;
		try {
			r = new InputStreamReader(con.getInputStream(), charset);
		} catch (IOException e) {
			return "ISE";
		}
		StringBuilder buf = new StringBuilder();
		while (true) {
			int ch;
			try {
				ch = r.read();
			} catch (IOException e) {
				return "RE";
			}
			if (ch < 0)
				break;
			buf.append((char) ch);
		}
		String str = buf.toString();
		return str;
	}
	public static String postToUrl(String url, List<NameValuePair> parameters, boolean getResponse){
		// Create a new HttpClient and Post Header
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = parameters;

			//(new BasicNameValuePair("id", "12345")); example<--

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpClient.execute(httpPost);

			if (getResponse){
				try {
					HttpEntity entity = response.getEntity();		      

					Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
					Matcher m = p.matcher(entity.getContentType().getValue());
					/* If Content-Type doesn't match this pre-conception, choose default and 
					 * hope for the best. */
					String charset = m.matches() ? m.group(1) : "ISO-8859-1";
					Reader r;
					try {
						r = new InputStreamReader(entity.getContent(), charset);
					} catch (IOException e) {
						return "ISE";
					}
					StringBuilder buf = new StringBuilder();
					while (true) {
						int ch;
						try {
							ch = r.read();
						} catch (IOException e) {
							return "RE";
						}
						if (ch < 0)
							break;
						buf.append((char) ch);
					}
					String str = buf.toString();
					return str;

				} finally {
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return "failed";
	}
}
