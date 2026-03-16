/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.util.misc;

public class Pair<L, R> {
    public L left;
    public R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<L, R>(left, right);
    }

    public L getLeft() {
        return this.left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public boolean hasLeft() {
        return this.left != null;
    }

    public R getRight() {
        return this.right;
    }

    public void setRight(R right) {
        this.right = right;
    }

    public boolean hasRight() {
        return this.right != null;
    }
}

