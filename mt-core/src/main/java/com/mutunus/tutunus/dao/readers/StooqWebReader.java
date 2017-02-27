package com.mutunus.tutunus.dao.readers;

import com.mutunus.tutunus.Utils;
import com.mutunus.tutunus.structures.MTDate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;


public class StooqWebReader {

    private static final String STOOQ = "http://stooq.pl";

    private final String urlCookies;
    private final String perAssetUrlTemplate;
    private final String perAssetUrlTemplateAll;
    private final String perDayUrlTemplate;
    private String template = "s=%s&i=d";

    private CookieStore cookieStore = null;

    public StooqWebReader() {
        this(STOOQ);
    }

    public StooqWebReader(final String hostUrl) {
        urlCookies = hostUrl + "/q/d/?s=wig20&c=0&d1=20110416&d2=20110415";
        perAssetUrlTemplate = hostUrl + "/q/d/l/?s=%s&d1=%d&d2=%d&i=d";
        perAssetUrlTemplateAll = hostUrl + "/q/d/l/?s=%s&i=d";
        perDayUrlTemplate = hostUrl + "/db/d/?d=%d&t=d";
    }

    public String getQuotationsForAsset(final String name) {
        return getQuotationsForAsset(name, null, null);
    }

    public String getQuotationsForAsset(final String name, final MTDate from, final MTDate to) {
        checkCookies();

        final String url = createUrl(name, from, to);
        final String data = getData(url);
        return data;
        // if (data == null) {
        // return null;
        // }
        //
        // final int indexOf = data.indexOf("Data,Otwarcie,Najw");
        // if (indexOf == -1) {
        // return null;
        // }
        //
        // return data.substring(indexOf);
    }

    private String createUrl(final String name, final MTDate d1, final MTDate d2) {
        final String nameEncoded = Utils.encodeURL(name);
        if (d1 == null || d2 == null) {
            final String format = String.format(template, nameEncoded);
            final String url2 = STOOQ + "/q/d/l/?" + format;
            final String url = String.format(perAssetUrlTemplateAll, nameEncoded);
            return url2;
        } else {
            final String url =
                    String.format(perAssetUrlTemplate, nameEncoded, d1.toIntYearMonthDay(), d2.toIntYearMonthDay());
            return url;
        }
    }

    public String getDailyQuotations(final MTDate day) {
        checkCookies();

        final String url = String.format(perDayUrlTemplate, day.toIntYearMonthDay());
        final String data = getData(url);
        return data;
    }

    private synchronized void checkCookies() {
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
            getData(urlCookies);
        }
    }

    private String getData(final String url) {
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpGet httpget = new HttpGet(url);

        final HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        try {
            final HttpResponse response = httpclient.execute(httpget, localContext);
            // System.out.println(response.getStatusLine());

            final HttpEntity entity = response.getEntity();

            final String content = readData(entity, httpget);
            return content;
        } catch (final Exception e) {
            System.out.println("Failed to load data from url: " + url);
            throw new RuntimeException(e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        // return null;
    }

    private String readData(final HttpEntity entity, final HttpGet httpget) {
        if (entity == null) {
            return null;
        }
        InputStream instream = null;
        try {
            instream = entity.getContent();
            final String stream = Utils.asString(instream, null);
            return stream;
        } catch (final IOException ex) {
        } catch (final RuntimeException ex) {
            httpget.abort();
        } finally {
            Utils.quietClose(instream);
        }
        return null;
    }

}
