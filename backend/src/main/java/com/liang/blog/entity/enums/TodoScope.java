package com.liang.blog.entity.enums;

public enum TodoScope {
    DAILY,
    WEEKLY,
    MONTHLY;

    public static TodoScope fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("范围不能为空");
        }
        try {
            return TodoScope.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("不支持的任务范围: " + value);
        }
    }
}
