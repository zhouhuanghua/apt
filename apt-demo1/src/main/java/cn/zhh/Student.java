package cn.zhh;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生类
 *
 * @author Zhou Huanghua
 */
@Data
public class Student implements Serializable {
    /** id */
    private Long id;
    /** 编号 */
    private String code;
    /** 姓名 */
    private String name;
    /** 性别 */
    private Byte sex;
    /** 创建时间 */
    private Date createTime;
    /** 最后更新时间 */
    private Date lastUpdateTime;
}
