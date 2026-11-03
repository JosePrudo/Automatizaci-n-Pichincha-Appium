package com.pichincha.automation.base;

import com.pichincha.automation.config.ConfigReader;
import io.appium.java_client.android.options.UiAutomator2Options;

public abstract class WebBaseTest extends BaseTest {

    @Override
    protected UiAutomator2Options createOptions() {
        UiAutomator2Options options = createBaseOptions();
        options.setCapability("browserName", ConfigReader.get("browserName", "Chrome"));
        return options;
    }
}
