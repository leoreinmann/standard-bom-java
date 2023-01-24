/*
 * Copyright (c) Siemens AG 2019-2022 ALL RIGHTS RESERVED
 */
package com.siemens.sbom.standardbom.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Dependency;
import org.cyclonedx.model.ExternalReference;
import org.cyclonedx.model.Metadata;
import org.cyclonedx.model.Tool;

import com.siemens.sbom.standardbom.internal.PropertyProcessor;
import com.siemens.sbom.standardbom.internal.VersionUtil;


/**
 * Main DTO for the complete "Standard BOM" JSON structure.
 * <p><a href="https://sbom.siemens.io/v2/format.html">Format Description</a></p>
 */
public class StandardBom
{
    /**
     * Namespace of our component custom properties as per <!-- @formatter:off --><a
     * href="https://github.com/CycloneDX/cyclonedx-property-taxonomy">https://github.com/CycloneDX/cyclonedx-property-taxonomy</a>
     * <!-- @formatter:on -->
     */
    public static final String CUSTOM_PROPERTY_NAMESPACE = "siemens";

    private final PropertyProcessor metaPropProc;

    private final Bom cycloneDxSbom;



    public StandardBom()
    {
        this(new Bom());
        getMetadata().addTool(getStandardBomDescriptor());
    }



    public StandardBom(@Nonnull final Bom pCycloneDxSbom)
    {
        cycloneDxSbom = Objects.requireNonNull(pCycloneDxSbom, "SBOM must not be null");
        metaPropProc = new PropertyProcessor(() -> getMetadata().getProperties(), p -> getMetadata().addProperty(p));
    }



    private Tool getStandardBomDescriptor()
    {
        final Tool result = new Tool();
        result.setVendor("Siemens AG");
        result.setName("standard-bom");
        result.setVersion(VersionUtil.getFormatVersion());
        final ExternalReference website = new ExternalReference();
        website.setType(ExternalReference.Type.WEBSITE);
        website.setUrl(VersionUtil.getWebsite());
        result.setExternalReferences(Collections.singletonList(website));
        return result;
    }



    @Nonnull
    public Date getTimestamp()
    {
        Date timestamp = getMetadata().getTimestamp();
        if (timestamp == null) {
            timestamp = new Date(0);
        }
        return timestamp;
    }



    public void setTimestamp(@Nonnull final Date pTimestamp)
    {
        getMetadata().setTimestamp(Objects.requireNonNull(pTimestamp, "timestamp cannot be null"));
    }



    public Metadata getMetadata()
    {
        Metadata result = cycloneDxSbom.getMetadata();
        if (result == null) {
            result = new Metadata();
            cycloneDxSbom.setMetadata(result);
        }
        return result;
    }



    @Nonnull
    public List<BomEntry> getComponents()
    {
        if (cycloneDxSbom.getComponents() == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(
            cycloneDxSbom.getComponents().stream().map(BomEntry::new).collect(Collectors.toList()));
    }



    public void addComponent(@Nonnull final BomEntry pBomEntry)
    {
        cycloneDxSbom.addComponent(pBomEntry.getCycloneDxComponent());
    }



    @Nonnull
    public List<Dependency> getDependencies()
    {
        List<Dependency> result = cycloneDxSbom.getDependencies();
        if (result == null) {
            result = new ArrayList<>();
            cycloneDxSbom.setDependencies(result);
        }
        return result;
    }



    public void addDependency(@Nonnull final Dependency pDependency)
    {
        cycloneDxSbom.addDependency(Objects.requireNonNull(pDependency));
    }



    @Nonnull
    public List<ExternalComponent> getExternalComponents()
    {
        final List<ExternalComponent> result = new ArrayList<>();
        if (cycloneDxSbom.getExternalReferences() != null) {
            for (ExternalReference extRef : cycloneDxSbom.getExternalReferences()) {
                if (ExternalComponent.isExternalComponent(extRef)) {
                    result.add(new ExternalComponent(extRef));
                }
            }
        }
        return Collections.unmodifiableList(result);
    }



    public void addExternalComponent(@Nonnull final ExternalComponent pExternalComponent)
    {
        cycloneDxSbom.addExternalReference(pExternalComponent.getCycloneDxRef());
    }



    public void setProfile(@Nullable final String pProfile)
    {
        metaPropProc.set(CustomProperty.PROFILE, pProfile);
    }



    @CheckForNull
    public String getProfile()
    {
        return metaPropProc.get(CustomProperty.PROFILE);
    }



    @Nonnull
    public Bom getCycloneDxBom()
    {
        return cycloneDxSbom;
    }
}
