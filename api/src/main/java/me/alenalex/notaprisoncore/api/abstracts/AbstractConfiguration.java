package me.alenalex.notaprisoncore.api.abstracts;

import com.google.common.base.CaseFormat;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public abstract class AbstractConfiguration extends AbstractFileConfiguration {

    protected abstract Field[] getFields();
    protected abstract ConfigType configType();
    private Logger logger;
    private boolean shownWarnings = false;

    public AbstractConfiguration(Logger logger, File configFile, InputStream fileStream) {
        super(configFile, fileStream);
        this.logger = logger;
    }

    public AbstractConfiguration(Logger logger, File configFile) {
        super(configFile);
        this.logger = logger;
    }

    @Override
    public void load(){
        if(configDocument == null)
            throw new FailedConfigurationException(configType(), "Load method has been called before initialization", null);


        for (Field declaredField : getFields()) {
            declaredField.setAccessible(true);

            if(!AbstractConfigurationOption.class.isAssignableFrom(declaredField.getType())){
                continue;
            }

            String fieldName = declaredField.getName();
            String configName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, fieldName);
            Section section = configDocument.getSection(configName);
            Constructor<?> constructor = null;
            try {
                constructor = declaredField.getType().getConstructor(Section.class);
            } catch (NoSuchMethodException e) {
                throw new FailedConfigurationException(configType(), "Failed to resolve constructor for "+fieldName+" or config option "+configName+". Please provide a constructor with Section.class as the only parameter", e);
            }

            if(constructor == null){
                throw new FailedConfigurationException(configType(), "Failed to find constructor for "+fieldName+" or config option "+configName+". Please provide a constructor with Section.class as the only parameter", null);
            }

            Object instance = null;
            try {
                instance = constructor.newInstance(section);
                declaredField.set(this, instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new FailedConfigurationException(configType(), "Failed to create config option for "+fieldName+" or config option "+configName+".", e);
            }
            try {
                Method load = declaredField.getType().getMethod("load");
                load.invoke(instance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FailedConfigurationException(configType(), "Failed to resolve method load for "+fieldName+" or config option "+configName+".", e);
            }

            try {
                Method load = declaredField.getType().getMethod("load");
                load.invoke(instance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FailedConfigurationException(configType(), "Failed to resolve method load for "+fieldName+" or config option "+configName+".", e);
            }

            try {
                Method validate = declaredField.getType().getMethod("validate");
                AbstractConfigurationOption.ValidationResponse validateResponse = (AbstractConfigurationOption.ValidationResponse) validate.invoke(instance);

                if(validateResponse == null || validateResponse.equals(AbstractConfigurationOption.ValidationResponse.OKAY)){
                    continue;
                }

                String[] warnings = validateResponse.getWarnings();
                if(warnings != null && warnings.length >= 1){
                    this.logger.info("The configuration loader has few warnings related to the validity of configuration section "+configName+". Please resolve it.");
                    if(!shownWarnings){
                        this.logger.info("NOTE: Warnings are not destructive actions, The server will continue to load");
                        this.shownWarnings = true;
                    }
                    for (String warning : warnings) {
                        this.logger.warning(" - "+warning);
                    }
                }
                String[] errors = validateResponse.getErrors();
                if(errors != null && errors.length >= 1){
                    this.logger.info("The configuration load has identified few errors on the loading of the configuration "+configName+". Please resolve it. The plugin will be disabled!");
                    for (String error : errors) {
                        this.logger.severe(" - "+error);
                    }
                }
                if(validateResponse.getStatus() == AbstractConfigurationOption.ValidationResponse.Status.INVALID){
                    throw new FailedConfigurationException(configType(), "Failed to load the configuration. Please resolve the errors above", null);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new FailedConfigurationException(configType(), "Failed to resolve method load for "+fieldName+" or config option "+configName+".", e);
            }
        }
    }

}
