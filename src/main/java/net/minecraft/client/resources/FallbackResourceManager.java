/*
 * Decompiled with CFR 0.151.
 */
package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager
implements IResourceManager {
    private static final Logger logger = LogManager.getLogger();
    protected final List<IResourcePack> resourcePacks = Lists.newArrayList();
    private final IMetadataSerializer frmMetadataSerializer;

    public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn) {
        this.frmMetadataSerializer = frmMetadataSerializerIn;
    }

    public void addResourcePack(IResourcePack resourcePack) {
        this.resourcePacks.add(resourcePack);
    }

    @Override
    public Set<String> getResourceDomains() {
        return null;
    }

    @Override
    public IResource getResource(ResourceLocation location) throws IOException {
        IResourcePack iresourcepack = null;
        ResourceLocation resourcelocation = FallbackResourceManager.getLocationMcmeta(location);
        for (int i = this.resourcePacks.size() - 1; i >= 0; --i) {
            IResourcePack iresourcepack1 = this.resourcePacks.get(i);
            if (iresourcepack == null && iresourcepack1.resourceExists(resourcelocation)) {
                iresourcepack = iresourcepack1;
            }
            if (!iresourcepack1.resourceExists(location)) continue;
            InputStream inputstream = null;
            if (iresourcepack != null) {
                inputstream = this.getInputStream(resourcelocation, iresourcepack);
            }
            return new SimpleResource(iresourcepack1.getPackName(), location, this.getInputStream(location, iresourcepack1), inputstream, this.frmMetadataSerializer);
        }
        throw new FileNotFoundException(location.toString());
    }

    protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
        InputStream inputstream = resourcePack.getInputStream(location);
        return logger.isDebugEnabled() ? new InputStreamLeakedResourceLogger(inputstream, location, resourcePack.getPackName()) : inputstream;
    }

    @Override
    public List<IResource> getAllResources(ResourceLocation location) throws IOException {
        ArrayList<IResource> list = Lists.newArrayList();
        ResourceLocation resourcelocation = FallbackResourceManager.getLocationMcmeta(location);
        for (IResourcePack iresourcepack : this.resourcePacks) {
            if (!iresourcepack.resourceExists(location)) continue;
            InputStream inputstream = iresourcepack.resourceExists(resourcelocation) ? this.getInputStream(resourcelocation, iresourcepack) : null;
            list.add(new SimpleResource(iresourcepack.getPackName(), location, this.getInputStream(location, iresourcepack), inputstream, this.frmMetadataSerializer));
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(location.toString());
        }
        return list;
    }

    static ResourceLocation getLocationMcmeta(ResourceLocation location) {
        return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
    }

    static class InputStreamLeakedResourceLogger
    extends InputStream {
        private final InputStream inputStream;
        private final String message;
        private boolean isClosed = false;

        public InputStreamLeakedResourceLogger(InputStream p_i46093_1_, ResourceLocation location, String resourcePack) {
            this.inputStream = p_i46093_1_;
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(bytearrayoutputstream));
            this.message = "Leaked resource: '" + location + "' loaded from pack: '" + resourcePack + "'\n" + bytearrayoutputstream.toString();
        }

        @Override
        public void close() throws IOException {
            this.inputStream.close();
            this.isClosed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.isClosed) {
                logger.warn(this.message);
            }
            super.finalize();
        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }
    }
}

