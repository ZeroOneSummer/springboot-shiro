package com.bocsoft.obss.shiro.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {
    private String userCode;
    private String bankNo;
    private String token;
    private Integer pwdStatus;
}
