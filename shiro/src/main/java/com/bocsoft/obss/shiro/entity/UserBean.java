package com.bocsoft.obss.shiro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
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

    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户名不能为空")
    @TableField(value = "USER_NAME")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @TableField(value = "PASS_WORD")
    private String password;

    @ApiModelProperty(value = "银行号")
    @NotBlank(message = "银行号不能为空")
    @TableField(value = "BANK_NO")
    private String bankNo;

    private String salt;

    private String roles;

    private String perms;

    private Integer status;
}
