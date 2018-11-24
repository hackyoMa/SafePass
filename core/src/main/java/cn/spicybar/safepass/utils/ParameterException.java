package cn.spicybar.safepass.utils;

import org.springframework.validation.BindingResult;

/**
 * 自定义参数异常
 *
 * @author hackyo
 * Created on 2017/12/4 12:35.
 */
public class ParameterException extends RuntimeException {

    private BindingResult bindingResult;

    public ParameterException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

}
