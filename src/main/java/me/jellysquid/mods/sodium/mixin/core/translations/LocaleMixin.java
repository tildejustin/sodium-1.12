package me.jellysquid.mods.sodium.mixin.core.translations;

import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.List;

@Mixin(Locale.class)
public abstract class LocaleMixin {
    @Inject(method = "loadLocaleData(Ljava/util/List;)V", at = @At("HEAD"))
    private void onInit(List<IResource> resourceList, CallbackInfo ci) {
        try (InputStream stream = SodiumClientMod.class.getResourceAsStream("/assets/sodium/" + resourceList.get(0).getResourceLocation().resourcePath)) {
            stream.read();
        } catch (IOException | NullPointerException e) {
            return;
        }
        resourceList.add(new IResource() {
            @Override
            public ResourceLocation getResourceLocation() {
                throw new UnsupportedOperationException();
            }

            @Override
            public InputStream getInputStream() {
                return SodiumClientMod.class.getResourceAsStream("/assets/sodium/" + resourceList.get(0).getResourceLocation().resourcePath);
            }

            @Override
            public boolean hasMetadata() {
                throw new UnsupportedOperationException();
            }

            @Nullable
            @Override
            public <T extends IMetadataSection> T getMetadata(String p_110526_0_) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getResourcePackName() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void close() {
                throw new UnsupportedOperationException();
            }
        });
    }
}
