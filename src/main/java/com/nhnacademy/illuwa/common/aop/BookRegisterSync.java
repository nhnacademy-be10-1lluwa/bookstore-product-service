package com.nhnacademy.illuwa.common.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BookRegisterSync {
}