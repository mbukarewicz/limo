package com.mutunus.tutunus.dao.readers;

import au.com.bytecode.opencsv.CSVReader;
import com.mutunus.tutunus.structures.MTDate;
import com.mutunus.tutunus.structures.Quotation;
import com.mutunus.tutunus.structures.QuotationsImpl;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import verdelhan.ta4j.Tick;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class StooqCsvParser {

    // TODO: daty moga sie powtarzac
    // TODO: sanity czeki dla q
    public SortedMap<MTDate, Quotation> parseDailyCsv(final String csv) {
        // final String lines[] = csv.split("\\r?\\n");
        final List<String[]> lines = splitIntoLines(csv);

        final SortedMap<MTDate, Quotation> quotations = new TreeMap<MTDate, Quotation>();
        // int i = 0;
        for (final String[] split : lines) {
            final String date = split[0];
            final String open = split[1];
            final String high = split[2];
            final String low = split[3];
            final String close = split[4];
            String volume = "-1";
            if (split.length > 5) {
                volume = split[5];
            }
            final Quotation q = Quotation.newInstance(open, high, low, close, volume);

            final MTDate d = MTDate.parse(date);
            // final float o = Float.parseFloat(open);
            // final float h = Float.parseFloat(high);
            // final float l = Float.parseFloat(low);
            // final float c = Float.parseFloat(close);
            // final long vol = Long.parseLong(volume);
            //
            // Quotation q = null;
            // try {
            // q = new Quotation(open, high, low, close, vol);
            // } catch (final Exception e) {
            // q = createQ(o, h, l, c, vol, "?NAME?", d);// TODO: suppresses error in the data
            // }

            quotations.put(d, q);
        }

        return quotations;
    }

    // private static Quotation createQ(final float o,
    // final float _h,
    // final float _l,
    // final float c,
    // final long vol,
    // final String name,
    // final MTDate date) {
    // float l = _l;
    // float h = _h;
    //
    // if (l > o || l > c || l > h) {
    // l = Math.min(l, o);
    // l = Math.min(l, h);
    // l = Math.min(l, c);
    // System.out.println("LOW: " + date + " " + name + " " + o + " " + _h + " " + _l + " " + c);
    // }
    // if (h < o || h < c || h < l) {
    // h = Math.max(h, o);
    // h = Math.max(h, l);
    // h = Math.max(h, c);
    // System.out.println("HIGH: " + date + " " + name + " " + o + " " + _h + " " + _l + " " + c);
    // }
    //
    // final Quotation q = new Quotation(o, h, l, c, vol);
    //
    // return q;
    // }

    // public SortedMap<MTDate, Quotation> getQuotations(final String name, final MTDate from, final MTDate to) {
    // final String csv = reader.getDataForAsset(name, from, to);
    //
    // if (csv == null) {
    // return new TreeMap<MTDate, Quotation>();
    // }
    // final SortedMap<MTDate, Quotation> quotations = parseDailyCsv(csv);
    // return quotations;
    // }

    public QuotationsImpl parsePerAsset(final String asset, final String csv) {
        final List<String[]> data = splitIntoLines(csv);

        final QuotationsImpl quotations = new QuotationsImpl(asset, data.size());
        int i = 0;
        for (final String[] split : data) {
            final String _date = split[0];
            final String open = split[1];
            final String high = split[2];
            final String low = split[3];
            final String close = split[4];
            String volume = "-1";
            if (split.length == 6) {
                volume = split[5];
            }

            final Quotation q = Quotation.newInstance(open, high, low, close, volume);
            final MTDate date = MTDate.parse(_date);

            quotations.setData(i, date, q);
            i++;
        }
        return quotations;
    }

    private List<String[]> splitIntoLines(final String csv) {
        CSVReader reader = null;
        List<String[]> data = null;
        try {
            reader = new CSVReader(new StringReader(csv));
            data = reader.readAll();
            if (!data.isEmpty()) {
                data.remove(0); // remove header
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return data;
    }

    public List<Tick> extractTicks(String data) {
        final List<String[]> lines = splitIntoLines(data);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        List<Tick> result = new ArrayList<>(lines.size());
        int i = 0;
        for (final String[] split : lines) {
            final String _date = split[0];
            final String open = split[1];
            final String high = split[2];
            final String low = split[3];
            final String close = split[4];
            String volume = "0";
            if (split.length == 6) {
                volume = split[5];
            }

            DateTime dt = formatter.parseDateTime(_date);
            Tick tick = new Tick(dt, open, high, low, close, volume);
            result.add(tick);
            i++;
        }
        return result;
    }

}
