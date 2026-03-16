/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.script;

import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.script.ScriptReader;
import io.github.itzispyder.improperui.util.misc.Pair;

public class ScriptArgs {
    private String[] args;

    public ScriptArgs(String ... args) {
        this.args = args;
    }

    public Arg getAll() {
        return this.getAll(0);
    }

    public Arg getAll(int beginIndex) {
        String str = "";
        for (int i = beginIndex; i < this.args.length; ++i) {
            str = str.concat(this.args[i] + " ");
        }
        return new Arg(str.trim());
    }

    public Arg get(int index) {
        if (this.args.length == 0) {
            return new Arg("");
        }
        return new Arg(this.args[Math.min(Math.max(index, 0), this.args.length - 1)]);
    }

    public String getQuoteAndRemove(int beginIndex) {
        String all = this.getAll(beginIndex).toString();
        Pair<String, Integer> section = ScriptReader.firstSectionWithIndex(all, '\"', '\"');
        if (((String)section.left).isEmpty()) {
            this.args = all.split("\\s+");
            return all;
        }
        this.args = all.substring((Integer)section.right).trim().split("\\s+");
        return (String)section.left;
    }

    public String getQuoteAndRemove() {
        return this.getQuoteAndRemove(0);
    }

    public String getQuote(int beginIndex) {
        String all = this.getAll(beginIndex).toString();
        String quote = ScriptReader.firstSection(all, '\"');
        return quote.isEmpty() ? all : quote;
    }

    public String getQuote() {
        return this.getQuote(0);
    }

    public Arg first() {
        return this.get(0);
    }

    public Arg last() {
        return this.get(this.args.length - 1);
    }

    public boolean match(int index, String arg) {
        if (index < 0 || index >= this.args.length) {
            return false;
        }
        return this.get(index).toString().equalsIgnoreCase(arg);
    }

    public int getSize() {
        return this.args.length;
    }

    public boolean isEmpty() {
        return this.args.length == 0;
    }

    public String[] args() {
        return this.args;
    }

    public static class Arg {
        private final String arg;

        public Arg(String arg) {
            this.arg = arg;
        }

        public int toInt() {
            return (int)this.toDouble();
        }

        public long toLong() {
            return (long)this.toDouble();
        }

        public byte toByte() {
            return (byte)this.toDouble();
        }

        public short toShort() {
            return (short)this.toDouble();
        }

        public float toFloat() {
            return (float)this.toDouble();
        }

        public double toDouble() {
            return Double.parseDouble(this.arg.replaceAll("[^0-9-+e.]", ""));
        }

        public boolean toBool() {
            return Boolean.parseBoolean(this.arg);
        }

        public char toChar() {
            return this.arg.isEmpty() ? (char)' ' : this.arg.charAt(0);
        }

        public Color toColor() {
            return Color.parse(this.arg);
        }

        public String toString() {
            return this.arg;
        }

        public <T extends Enum<?>> T toEnum(Class<T> enumType) {
            return this.toEnum(enumType, null);
        }

        public <T extends Enum<?>> T toEnum(Class<T> enumType, T fallback) {
            String arg = this.arg.replace('-', '_');
            for (Enum constant : (Enum[])enumType.getEnumConstants()) {
                if (!arg.equalsIgnoreCase(constant.name())) continue;
                return (T)constant;
            }
            if (fallback == null) {
                throw new IllegalArgumentException("'%s' is not a value of %s".formatted(arg, enumType.getSimpleName()));
            }
            return fallback;
        }
    }
}

