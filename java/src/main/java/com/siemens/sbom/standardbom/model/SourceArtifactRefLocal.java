/*
 * Copyright (c) Siemens AG 2019-2024 ALL RIGHTS RESERVED
 */
package com.siemens.sbom.standardbom.model;

import java.util.regex.Pattern;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.cyclonedx.model.ExternalReference;

import com.siemens.sbom.standardbom.internal.FileProtocolHandler;


/**
 * A source artifact reference which explicitly refers to a downloaded copy of the source archive via a relative path.
 */
public class SourceArtifactRefLocal
    extends SourceArtifactRef
{
    public static final String SOURCE_ARCHIVE_LOCAL = SOURCE_ARCHIVE + " (local copy)";



    public SourceArtifactRefLocal()
    {
        this(new ExternalReference());
    }



    public SourceArtifactRefLocal(@Nonnull final ExternalReference pExternalReference)
    {
        super(pExternalReference);
        pExternalReference.setComment(SOURCE_ARCHIVE_LOCAL);
    }



    @CheckForNull
    public String getRelativePath()
    {
        return FileProtocolHandler.withoutFileProtocol(getCycloneDxRef().getUrl());
    }



    public static boolean isSourceReference(@Nullable final ExternalReference pExternalReference)
    {
        boolean result = pExternalReference != null
            && SourceArtifactRef.isSourceReference(pExternalReference)
            && SOURCE_ARCHIVE_LOCAL.equals(pExternalReference.getComment());
        return result;
    }



    public void setRelativePath(@Nullable final String pRelativePath)
    {
        String relativePath = pRelativePath;
        if (pRelativePath != null) {
            relativePath = pRelativePath.replaceAll(Pattern.quote("\\"), "/");
        }
        getCycloneDxRef().setUrl(FileProtocolHandler.ensureFileProtocol(relativePath));
        getCycloneDxRef().setComment(SOURCE_ARCHIVE_LOCAL);
    }
}
