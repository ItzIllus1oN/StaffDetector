/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.math.animation;

import io.github.itzispyder.improperui.util.MathUtils;

public class Animator {
    private long start = System.currentTimeMillis();
    private long length;
    private boolean reversed;

    public Animator(long length) {
        this.length = length;
        this.reversed = false;
    }

    public double getProgress() {
        long pass = System.currentTimeMillis() - this.start;
        double rat = (double)pass / (double)this.length;
        return this.reversed ? 1.0 - rat : rat;
    }

    public double getProgressClamped() {
        return MathUtils.clamp(this.getProgress(), 0.0, 1.0);
    }

    public double getProgressReversed() {
        return 1.0 - this.getProgress();
    }

    public double getProgressClampedReversed() {
        return MathUtils.clamp(this.getProgressReversed(), 0.0, 1.0);
    }

    public boolean isFinished() {
        double p = this.getProgress();
        return this.reversed ? p <= 0.0 : p >= 1.0;
    }

    public void reverse() {
        this.reversed = !this.reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public void reset(long length) {
        this.start = System.currentTimeMillis();
        this.length = length;
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public static int transformColorOpacity(Animator animator, int hex) {
        int alpha = (int)(255.0 * animator.getProgressClamped());
        return alpha << 24 | hex & 0xFFFFFF;
    }
}

