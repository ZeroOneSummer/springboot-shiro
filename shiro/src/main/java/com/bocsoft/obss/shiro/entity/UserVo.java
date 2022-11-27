package com.bocsoft.obss.shiro.entity;

import com.bocsoft.obss.common.enums.PwdStatusEnum;
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
    private PwdStatusEnum pwdStatus;
}
