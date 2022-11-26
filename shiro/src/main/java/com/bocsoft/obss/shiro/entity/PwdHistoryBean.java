package com.bocsoft.obss.shiro.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("T_PASSWORD_HISTORY")
public class PwdHistoryBean implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userCode;

    private String passWord;

    private String salt;

    private String bankNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")  //格式化输出
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE_TIME) //格式化输入
    @TableField(fill = FieldFill.INSERT)    //自动填充
    private LocalDateTime createDate;
}
