package me.alenalex.notaprisoncore.api.abstracts;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbstractConfigurationOption {
    private final Section section;
    public abstract void load();
    protected Section getSection(){
        return section;
    }

    public ValidationResponse validate(){
        return null;
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ValidationResponse {

        public static final ValidationResponse OKAY = Builder.builder()
                .setNoWarning()
                .setNoErrors()
                .withStatus(Status.OK)
                .build();

        private final Status status;
        private final String[] warnings;
        private final String[] errors;

        private ValidationResponse(Builder builder) {
            status = builder.status;
            warnings = builder.warnings.toArray(new String[0]);
            errors = builder.errors.toArray(new String[0]);
        }

        public enum Status {
            OK,
            OKAY_WITH_WARNING,
            INVALID,
        }


        public static final class Builder {
            private Status status;
            private final List<String> warnings = new ArrayList<>();
            private final List<String> errors = new ArrayList<>();

            private Builder() {
            }

            public static Builder builder() {
                return new Builder();
            }

            public Builder withStatus(Status status) {
                this.status = status;
                return this;
            }

            public Builder setNoWarning(){
                this.warnings.clear();
                return this;
            }

            public Builder setNoErrors(){
                this.errors.clear();
                return this;
            }

            public Builder withWarnings(String... warning){
                this.warnings.addAll(Arrays.asList(warning));
                return this;
            }

            public Builder withErrors(String... error) {
                this.errors.addAll(Arrays.asList(error));
                return this;
            }

            public ValidationResponse build() {
                if(errors.size() >= 1)
                    this.status = Status.INVALID;
                else if(warnings.size() >= 1)
                    this.status = Status.OKAY_WITH_WARNING;
                else this.status = Status.OK;
                return new ValidationResponse(this);
            }
        }
    }


}
