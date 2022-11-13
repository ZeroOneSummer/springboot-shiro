package com.bocsoft.obss.shiro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user")
public class UserBean implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "USER_NAME")
    private String username;

    @TableField(value = "PASS_WORD")
    private String password;

    private String salt;

    @TableField(value = "BANK_NO")
    private String bankNo;

    private String roles;

    private String perms;

    private Integer status;
}
