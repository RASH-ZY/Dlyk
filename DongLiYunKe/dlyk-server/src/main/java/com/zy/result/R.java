package com.zy.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 同意封装Web层向前端返回的结果
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class R {
    // 表示返回的结果码，比如200成功，500失败
    private int code;

    // 表示返回的结果信息，比如，用户登录状态失败，请求参数格式有误...
    private String msg;

    // 表示返回的结果数据，可能是一个对象，也可能是一个List集合...
    private Object data;

    /**
     * 建造者设计模式 @Builder
     * 将对象的 '构造过程' 与 '表示' 分离，使得对象的构造过程逐步完成，无需一次性提供所有参数
     * 主要目的是让对象的创建过程更加清晰、灵活和可控。
     *
     * @return
     */
    public static R OK() {
        return R.builder() //获取建造器
                .code(CodeEnum.OK.getCode()) //属性赋值 对象的表示过程
                .msg(CodeEnum.OK.getMsg()) //属性赋值
                .build(); //创建对象 对象的建造过程
    }

//    public static R OK(int code, String msg) {
//        return R.builder()
//                .code(code)
//                .msg(msg)
//                .build();
//    }

    public static R OK (Object data){
        return R.builder()
                .code(CodeEnum.OK.getCode())
                .msg(CodeEnum.OK.getMsg())
                .data(data)
                .build();
    }

    public static R OK(CodeEnum codeEnum) {
        return R.builder()
                .code(codeEnum.getCode())
                .msg(codeEnum.getMsg())
                .build();
    }

    public static R FAIL (){
        return R.builder()
                .code(CodeEnum.FAIL.getCode())
                .msg(CodeEnum.FAIL.getMsg())
                .build();
    }

    public static R FAIL (String msg){
        return R.builder()
                .code(CodeEnum.FAIL.getCode())
                .msg(msg)
                .build();
    }

    public static R FAIL (CodeEnum codeEnum){
        return R.builder()
                .code(codeEnum.getCode())
                .msg(codeEnum.getMsg())
                .build();
    }

}