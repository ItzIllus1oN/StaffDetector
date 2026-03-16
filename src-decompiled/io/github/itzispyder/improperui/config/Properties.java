/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.config;

import io.github.itzispyder.improperui.config.Paths;
import io.github.itzispyder.improperui.script.ScriptArgs;
import io.github.itzispyder.improperui.util.FileValidationUtils;
import io.github.itzispyder.improperui.util.misc.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class Properties {
    private final Map<String, Value> properties = new HashMap<String, Value>();

    public Value getProperty(Key key) {
        return this.properties.get(key.name());
    }

    public Value getProperty(String key) {
        return this.getProperty(new Key(key));
    }

    public void setProperty(Key key, Value value) {
        if (key != null && value != null) {
            this.properties.put(key.name(), value);
        }
    }

    public void setProperty(String key, String value) {
        this.setProperty(new Key(key), new Value(value));
    }

    public boolean hasProperty(Key key) {
        return this.properties.containsKey(key);
    }

    public boolean hasProperty(String key) {
        return this.hasProperty(new Key(key));
    }

    public void read(InputStream in) {
        try (InputStream inputStream = in;
             InputStreamReader isr = new InputStreamReader(in);
             BufferedReader br = new BufferedReader(isr);){
            String line;
            this.properties.clear();
            while ((line = br.readLine()) != null) {
                Pair<Key, Value> pair = this.callProperty(line);
                if (pair == null) continue;
                this.setProperty((Key)pair.left, (Value)pair.right);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(OutputStream out) {
        try (OutputStream outputStream = out;
             OutputStreamWriter osw = new OutputStreamWriter(out);
             BufferedWriter bw = new BufferedWriter(osw);){
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Value> entry : this.properties.entrySet()) {
                String line = "%s = %s".formatted(entry.getKey(), entry.getValue().getName());
                sb.append(line).append('\n');
            }
            bw.write(sb.toString());
            bw.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void read(String modId, String path) {
        path = Paths.getConfigs(modId) + (String)path;
        FileValidationUtils.validate(new File((String)path));
        try (FileInputStream fis = new FileInputStream((String)path);){
            this.read(fis);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(String modId, String path) {
        path = Paths.getConfigs(modId) + (String)path;
        File file = new File((String)path);
        FileValidationUtils.validate(file);
        try (FileOutputStream fos = new FileOutputStream((String)path);){
            this.write(fos);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Pair<Key, Value> callProperty(String line) {
        String regex = "\\s*((=>)|(->)|:|=)\\s*";
        String[] split = line.trim().split(regex);
        if (split.length < 2) {
            return null;
        }
        Key key = new Key(split[0]);
        Value val = new Value(line.substring(split[0].length()).replaceFirst(regex, ""));
        return Pair.of(key, val);
    }

    public record Key(String name) {
        public Key(String name) {
            this.name = name.trim().replaceAll("\\s+|_", "-").replaceAll("[^a-zA-Z0-9.-]", "");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key key = (Key)obj;
            return key.name().equals(this.name());
        }
    }

    public static class Value
    extends ScriptArgs {
        public Value(String name) {
            super(name.trim().split("\\s+"));
        }

        public String getName() {
            return this.getAll().toString();
        }

        @Override
        public String getQuote(int beginIndex) {
            String q = super.getQuote(beginIndex);
            return q.matches("\\\"+") ? "" : q;
        }

        @Override
        public String getQuoteAndRemove(int beginIndex) {
            String q = super.getQuoteAndRemove(beginIndex);
            return q.matches("\\\"+") ? "" : q;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Value)) {
                return false;
            }
            Value val = (Value)obj;
            return val.getName().equals(this.getName());
        }
    }
}

