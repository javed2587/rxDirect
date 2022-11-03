package com.ssa.cms.validator;

import com.ssa.cms.common.Constants;
import com.ssa.cms.model.ContactUs;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 *
 * @author Zubair
 */
@Component
public class ContactUsValidator implements Validator {

    private Pattern pattern;
    private Matcher matcher;

    @Override
    public boolean supports(Class<?> type) {
        return ContactUs.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ContactUs contactUs = (ContactUs) o;
        if (contactUs.getName() != null && contactUs.getName().trim().isEmpty()) {
            errors.rejectValue("name", "error.field.required");
        }
        if (contactUs.getEmail() != null && contactUs.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "error.field.required");
        }
        if (contactUs.getMessage() != null && contactUs.getMessage().trim().isEmpty()) {
            errors.rejectValue("message", "error.field.required");
        }
        if (!"".equals(contactUs.getEmail())) {
            pattern = Pattern.compile(Constants.EMAIL_PATTERN);
            matcher = pattern.matcher(contactUs.getEmail());
            if (!matcher.matches()) {
                errors.rejectValue("email", "error.invalid.Email");
            }
        }
    }

}
