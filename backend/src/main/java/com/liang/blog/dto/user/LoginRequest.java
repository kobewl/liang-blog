package com.liang.blog.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "账号或邮箱不能为空")
    private String accountOrEmail;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需要在6~100之间")
    private String password;

    public String getAccountOrEmail() {
        return accountOrEmail;
    }

    public void setAccountOrEmail(String accountOrEmail) {
        this.accountOrEmail = accountOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
