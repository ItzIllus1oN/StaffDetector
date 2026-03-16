/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.math;

public class Dimensions {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final int widthX;
    public final int heightY;

    public Dimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widthX = x + width;
        this.heightY = y + height;
    }

    public boolean isOverlapping(Dimensions dim) {
        int tx = this.getX();
        int ty = this.getY();
        int tw = this.getWidth();
        int th = this.getHeight();
        int txTw = tx + tw;
        int tyTh = ty + th;
        int ox = dim.getX();
        int oy = dim.getY();
        int ow = dim.getWidth();
        int oh = dim.getHeight();
        int oxOw = ox + ow;
        int oyOh = oy + oh;
        boolean topLeft = txTw >= ox && txTw <= oxOw && tyTh >= oy && tyTh <= oyOh;
        boolean topRight = tx >= ox && tx <= oxOw && tyTh >= oy && tyTh <= oyOh;
        boolean bottomRight = tx >= ox && tx <= oxOw && ty >= oy && ty <= oyOh;
        boolean bottomLeft = txTw >= ox && txTw <= oxOw && ty >= oy && ty <= oyOh;
        return topLeft || topRight || bottomLeft || bottomRight;
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
    }

    public boolean contains(double x, double y) {
        return this.contains((int)x, (int)y);
    }

    public int getX() {
        return this.x;
    }

    public Dimensions withX(int x) {
        return new Dimensions(x, this.y, this.width, this.height);
    }

    public int getY() {
        return this.y;
    }

    public Dimensions withY(int y) {
        return new Dimensions(this.x, y, this.width, this.height);
    }

    public int getWidth() {
        return this.width;
    }

    public Dimensions withWidth(int width) {
        return new Dimensions(this.x, this.y, width, this.height);
    }

    public int getHeight() {
        return this.height;
    }

    public Dimensions withHeight(int height) {
        return new Dimensions(this.x, this.y, this.width, height);
    }
}

