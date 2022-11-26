package com.bocsoft.obss.shiro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
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
    @TableField(value = "USER_NAME")
    private String userName;

    @ApiModelProperty(value = "用户编号")
    @NotBlank(message = "用户编号不能为空")
    @MppMultiId
    private String userCode;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    @TableField(value = "PASS_WORD")
    private String passWord;

    @ApiModelProperty(value = "银行号")
    @NotBlank(message = "银行号不能为空")
    @TableField(value = "BANK_NO")
    @MppMultiId
    private String bankNo;

    private String salt;

    private String roles;

    private String perms;

    private Integer status;
}
