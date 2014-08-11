package com.smartbear.soapui.plugins.search;

import com.eviware.soapui.actions.Prefs;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.plugins.auto.PluginPrefs;
import com.eviware.soapui.support.components.SimpleForm;
import com.eviware.soapui.support.types.StringToStringMap;

/*
 * Copyright 2004-2014 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 *
*/
@PluginPrefs
public class SearchPluginPrefs implements Prefs {

    public static final String NUMBER_OF_HITS_SETTING = SearchPluginPrefs.class.getName() + "@number_of_hits";

    private static final String NUMBER_OF_HITS = "Number of hits to display";
    private SimpleForm form = new SimpleForm();

    @Override
    public SimpleForm getForm() {
        form.appendTextField(NUMBER_OF_HITS, NUMBER_OF_HITS);
        return form;
    }

    @Override
    public void setFormValues(Settings settings) {
        form.setComponentValue(NUMBER_OF_HITS, settings.getString(NUMBER_OF_HITS_SETTING, ""));
    }

    @Override
    public void getFormValues(Settings settings) {
        settings.setString(NUMBER_OF_HITS_SETTING, form.getComponentValue(NUMBER_OF_HITS));
    }

    @Override
    public void storeValues(StringToStringMap stringToStringMap, Settings settings) {

    }

    public @Override
    StringToStringMap getValues(Settings settings) {
        return null;
    }

    public @Override
    String getTitle() {
        return "Search Plugin";
    }
}
