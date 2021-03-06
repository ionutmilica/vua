package vua.validation.validators;

import vua.validation.Validator;
import vua.validation.annotations.Max;

import java.util.Collection;

public class MaxValidator implements RuleValidator<Object> {

    private final Max annotation;

    public MaxValidator(Max annotation) {
        this.annotation = annotation;
    }

    @Override
    public void validate(Validator validator, String field, Object value) {
        int size = annotation.value(), sizeOf = 0;

        if (value == null) {
            return;
        }


        if (value instanceof Collection) {
            sizeOf = ((Collection) value).size();
        } else if (value instanceof String) {
            sizeOf = ((String) value).length();
        } else if (value instanceof Object[]) {
            sizeOf = ((Object[]) value).length;
        }

        if (sizeOf > size) {
            String message = String.format(annotation.message(), field, size);
            validator.addMessage(field, message);
        }
    }
}
