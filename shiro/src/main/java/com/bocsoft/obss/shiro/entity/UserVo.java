package com.bocsoft.obss.shiro.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {
    private String username;
    private String bankno;
    private String token;
}
