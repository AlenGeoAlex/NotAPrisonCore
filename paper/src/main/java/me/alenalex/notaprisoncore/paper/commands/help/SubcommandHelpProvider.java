package me.alenalex.notaprisoncore.paper.commands.help;

public class SubcommandHelpProvider {
    public final String subcommand;
    public final String args;
    public final String permission;
    public final String description;
    public final String[] aliases;

    private SubcommandHelpProvider(Builder builder) {
        subcommand = builder.subcommand;
        args = builder.args;
        permission = builder.permission;
        description = builder.description;
        aliases = builder.aliases;
    }

    public static Builder newBuilder(SubcommandHelpProvider copy) {
        Builder builder = new Builder();
        builder.subcommand = copy.getSubcommand();
        builder.args = copy.getArgs();
        builder.permission = copy.getPermission();
        builder.description = copy.getDescription();
        builder.aliases = copy.getAliases();
        return builder;
    }


    public String getSubcommand() {
        return subcommand;
    }

    public String getArgs() {
        return args;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }


    public static final class Builder {
        private String subcommand;
        private String args = "";
        private String permission;
        private String description;
        private String[] aliases;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withSubcommand(String subcommand) {
            this.subcommand = subcommand;
            return this;
        }

        public Builder withArgs(String args) {
            this.args = args;
            return this;
        }

        public Builder withPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public SubcommandHelpProvider build() {
            return new SubcommandHelpProvider(this);
        }
    }
}
