package com.darkz.skintotem.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.YACLScreen;
import com.darkz.skintotem.client.SkinTotemClient;
import com.darkz.skintotem.yacl.YACLConfigurationScreen;
import com.darkz.skintotem.yacl.custom.category.rendering.RenderingCategoryTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.state.level.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {

	@Unique
	private static final String SUBMIT_HANDS_WITH_ITEMS = "submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V";

	@Unique
	private static boolean isRenderingCategoryOpen() {
		Minecraft client = Minecraft.getInstance();
		Screen currentScreen = client.gui.screen();

		if (YACLConfigurationScreen.notOpen(currentScreen)) {
			return false;
		}
		if (!(currentScreen instanceof YACLScreen yaclScreen)) {
			return false;
		}
		if (!(yaclScreen.tabManager.getCurrentTab() instanceof RenderingCategoryTab)) {
			return false;
		}
		return Minecraft.getInstance().player != null;
	}

	@Unique
	private static ItemStack getPreviewStack(ItemStack original, LocalPlayer player) {
		if (original.isEmpty() || !SkinTotemClient.canProcess(original)) {
			ItemStack totem = Items.TOTEM_OF_UNDYING.getDefaultInstance();
			totem.set(DataComponents.CUSTOM_NAME, player.getName());
			return totem;
		}
		return original;
	}

	/**
	 * Since 26.3 the item shown in hand is baked into the hands render state during extraction,
	 * so the preview stack has to be resolved into the matching {@link ItemStackRenderState} as well.
	 */
	@Unique
	private static void prepareRenderState(FirstPersonHandsAndItemsRenderState state, InteractionHand hand, ItemStack stack, LocalPlayer player) {
		boolean isMainHand = hand == InteractionHand.MAIN_HAND;
		boolean isMainHandRight = player.getMainArm() == HumanoidArm.RIGHT;
		ItemDisplayContext displayContext = isMainHand == isMainHandRight ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
		ItemStackRenderState renderState = isMainHand ? state.mainHandRenderState : state.offHandRenderState;

		Minecraft.getInstance().getItemModelResolver().updateForTopItem(renderState, stack, displayContext, player.level(), player, player.getId() + displayContext.ordinal());
	}

	@Inject(at = @At("HEAD"), method = SUBMIT_HANDS_WITH_ITEMS)
	private void createBoolean(float partialTicks, PoseStack matrixStack, SubmitNodeCollector queue, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci, @Share("mtd_bl") LocalBooleanRef ref) {
		boolean previewing = isRenderingCategoryOpen();
		ref.set(previewing);

		if (previewing) {
			state.handRenderSelection = FirstPersonHandsAndItemsRenderState.HandRenderSelection.RENDER_BOTH_HANDS;
		}
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/FirstPersonHandsAndItemsRenderer;submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
			),
			method = SUBMIT_HANDS_WITH_ITEMS
	)
	private void swapRenderingStack(FirstPersonHandsAndItemsRenderer instance, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack stack, float inverseArmHeight, PoseStack matrixStack, SubmitNodeCollector queue, int light, Operation<Void> original, @Share("mtd_bl") LocalBooleanRef ref) {
		LocalPlayer player = Minecraft.getInstance().player;
		ItemStack stackToRender = stack;

		if (ref.get() && player != null) {
			stackToRender = getPreviewStack(stack, player);
			if (stackToRender != stack) {
				prepareRenderState(state, hand, stackToRender, player);
			}
		}

		original.call(instance, playerState, state, partialTicks, xRot, hand, attack, stackToRender, inverseArmHeight, matrixStack, queue, light);
	}
}
