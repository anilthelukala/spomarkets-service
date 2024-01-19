package com.spom.service.captcha;

import com.spom.service.error.ReCaptchaInvalidException;

public interface ICaptchaService {
    default void processResponse(final String response) throws ReCaptchaInvalidException {
    }

    default void processResponse(final String response, String action) throws ReCaptchaInvalidException {
    }

    String getReCaptchaSite();

    String getReCaptchaSecret();
}
