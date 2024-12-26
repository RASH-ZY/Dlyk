package com.zy.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public enum DicEnum {
    APPELLATION("appellation"),
    NEEDLOAN("needLoan"),
    INTENTIONSTATE("intentionState"),
    PRODUCT("product"),
    CLUESTATE("clueState"),
    SOURCE("source"),
    ACTIVITY("activity")
    ;

    @Getter
    @Setter
    private String code;
}
