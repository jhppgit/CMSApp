package com.sds.cmssettings.domain;

import lombok.Data;

@Data
public class DocumentRequest {
    private Document document;
    private VersionLog versionLog;
    private DocumentVersion documentVersion;
}