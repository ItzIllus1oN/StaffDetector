/*
 * Decompiled with CFR 0.152.
 */
package io.github.itzispyder.improperui.render.math.animation;

import io.github.itzispyder.improperui.render.math.animation.Animator;
import java.util.function.BooleanSupplier;

public class PollingAnimator
extends Animator {
    private final BooleanSupplier poll;
    private boolean pollSuccess;

    public PollingAnimator(int length, BooleanSupplier poll) {
        super(length);
        boolean bool;
        this.poll = poll;
        this.pollSuccess = bool = poll.getAsBoolean();
        this.setReversed(!bool);
    }

    @Override
    public double getProgress() {
        this.poll();
        return super.getProgress();
    }

    public void poll() {
        if (this.poll.getAsBoolean() && !this.pollSuccess) {
            this.pollSuccess = true;
            this.setReversed(false);
            this.reset();
        } else if (!this.poll.getAsBoolean() && this.pollSuccess) {
            this.pollSuccess = false;
            this.setReversed(true);
            this.reset();
        }
    }
}

