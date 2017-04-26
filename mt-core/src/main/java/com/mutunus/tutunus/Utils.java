package com.mutunus.tutunus;

import com.mutunus.tutunus.structures.MTException;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

public class Utils {

    public static final String UTF_8 = "UTF-8";
    // private static final DateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    public static String getFileContent(final String filename) {

        final File file = new File(filename);
        try {
            final String result = FileUtils.readFileToString(file, UTF_8);
            return result;
        } catch (final Exception e) {
            throw new RuntimeException("Failed to read data from '" + file.getAbsolutePath() + "', reason: " + e, e);
        }
    }

    public static String getUrlContentAsString(final String url, final String encoding) {
        final byte[] bytes = getUrlContent(url).array();
        final InputStream is = new ByteArrayInputStream(bytes);
        try {
            return asString(is, encoding);
        } catch (final IOException e) {
            throw new MTException(e);
        }
    }

    public static String asString(final InputStream is, final String encoding) throws IOException {
        if (encoding == null) {
            return Streams.asString(is, UTF_8);
        } else {
            return Streams.asString(is, encoding);
        }
    }

    public static ByteBuffer getUrlContent(final String webAddres) {
        return getUrlContentInternal(webAddres, 0);
    }

    private static ByteBuffer getUrlContentInternal(final String webAddres, final int repeat) {
        InputStream in = null;
        try {
            LOG.info("Reading '" + webAddres + "'");
            in = new URL(webAddres).openStream();
            return copy(in);
        } catch (final MalformedURLException e) {
            throw new MTException(e);
        } catch (final IOException e) {
            // TODO: max_repeats = xxx;
            if (repeat < 2) {
                return getUrlContentInternal(webAddres, repeat + 1);
            }
            throw new MTException(e);
        } finally {
            Utils.quietClose(in);
        }
    }

    public static List<String> getZipContent(final ByteBuffer data) {
        final ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(data.array()));

        final List<String> list = new ArrayList<String>();
        final DataInputStream in = new DataInputStream(input);
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));

        try {
            final String entryName = input.getNextEntry().getName();// zip.getEntry("a_all.prn");
            LOG.fine("Reading '" + entryName + "' entry from the zip file.");

            String strLine;
            while ((strLine = br.readLine()) != null) {
                list.add(strLine.trim());
            }
            quietClose(input);
        } catch (final IOException e) {
            throw new MTException(e);
        }

        return list;
    }

    public static void quietClose(final Closeable os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (final IOException e) {
        }
    }

    public static ByteBuffer getBinaryContent(final File file) {
        if (!file.exists() || !file.isFile()) {
            throw new MTException("Path '" + file.getAbsolutePath() + "' is invalid.");
        }

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            return copy(in);
        } catch (final IOException e) {
            throw new MTException("Problem reading file '" + file.getAbsolutePath() + "'", e);
        } finally {
            Utils.quietClose(in);
        }
    }

    public static ByteBuffer getBinaryData(final String url) {
        InputStream is = null;
        try {
            final URL toDl = new URL(url);
            is = toDl.openConnection().getInputStream();

            return copy(is);
        } catch (final Exception e) {
            throw new MTException("Problem reading URL '" + url + "'", e);
        } finally {
            Utils.quietClose(is);
        }
    }

    public static ByteBuffer getBinaryData(final File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return copy(is);
        } catch (final IOException e) {
            throw new MTException("Problem reading file '" + file + "'", e);
        } finally {
            Utils.quietClose(is);
        }
    }

    private static ByteBuffer copy(final InputStream is) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Streams.copy(is, out, false);
        return ByteBuffer.wrap(out.toByteArray());
    }

    public static void assertNotEmpty(final String key, final String value) {
        assertNotNull(key, value);

        if (value.trim().length() == 0) {
            throw new MTException("Can not use empty value for '" + key + "'");
        }
    }

    public static void assertNotNull(final String key, final Object value) {
        if (value == null) {
            throw new NullPointerException("'" + key + "' can not be null.");
        }
    }

    public static void setTimeZoneToWarsaw() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));
    }

    public static String encodeURL(final String name) {
        try {
            final String htmlEscape = URLEncoder.encode(name, UTF_8);
            return htmlEscape;
        } catch (final Exception e) {
            return name;
        }
    }

}
