package com.moutamid.daiptv.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "tv")
public class XMLModel {
    @Element(name = "generator-info-name", required = false)
    private String generatorInfoName;

    @Element(name = "generator-info-url", required = false)
    private String generatorInfoUrl;

    @ElementList(inline = true, entry = "programme")
    private List<ProgrammeModel> programmeList;

    public String getGeneratorInfoName() {
        return generatorInfoName;
    }

    public String getGeneratorInfoUrl() {
        return generatorInfoUrl;
    }

    public List<ProgrammeModel> getProgrammeList() {
        return programmeList;
    }
}
