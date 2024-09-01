/*
 * Decompiled with CFR 0.151.
 */
package client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Settings {
    public double minValue() default 0.0;

    public double maxValue() default 0.0;

    public String name() default "";

    public String[] list() default {};
}

