package com.luxof.lapisworks.blocks;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.blocks.entities.RitusEntity;
import com.luxof.lapisworks.chalk.MultiUseRitualExecutionState;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.getFacingWithRespectToDown;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class Ritus extends BlockWithEntity {
    private final boolean castAsStarter;
    public Ritus(boolean castAsStarter) {
        super(
            Settings.copy(Blocks.DEEPSLATE_TILES)
                .strength(2f, 4f)
                .mapColor(DyeColor.PURPLE)
                .pistonBehavior(PistonBehavior.NORMAL)
        );
        this.castAsStarter = castAsStarter;
        setDefaultState(
            getDefaultState()
                .with(POWERED, false)
                .with(ATTACHED, Direction.DOWN)
        );
    }

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final EnumProperty<Direction> ATTACHED = EnumProperty.of(
        "attached",
        Direction.class
    );
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        builder.add(ATTACHED);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
            .with(ATTACHED, ctx.getSide().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RitusEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        // checkType() makes me required to do an "unsafe cast" for whatever reason
        if (type == ModBlocks.RITUS_ENTITY_TYPE) {
            return (worldInner, pos, stateInner, ent) -> {
                ((RitusEntity)ent).tick(stateInner);
            };
        }
        else { return null; }
    }

    @SuppressWarnings("null")
    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        RitusEntity ritus = (RitusEntity)world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
        ritus.clearDisplay();

        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.SUCCESS;

        VAULT vault = ((GetVAULT)sp).grabVAULT();

        if (iotaHolder != null) {

            Iota iota = iotaHolder.readIota((ServerWorld)world);
            ritus.setTunedFrequency(iota);

            return ActionResult.SUCCESS;

        } else if (
            player.isCreative() ||
            // it drains from hands first
            // i also don't want it draining from trinkets if hand is full (no accidents pls)
            vault.drain(Mutables::isAmel, 1, true, Flags.PRESET_Hands) > 0 ||
            (stack.isEmpty() &&
            vault.drain(Mutables::isAmel, 1, true, Flags.PRESET_Equipped_Trinkets) > 0)
        ) {

            if (!(world instanceof ServerWorld sw)) return ActionResult.SUCCESS;
            Vec3d look = player.getRotationVector();

            // fuck you. (unbraces your curly-brace-indented programming language)
            if (ritus.addRitual(new MultiUseRitualExecutionState(
                    pos,
                    getFacingWithRespectToDown(look, ritus.getAttachedTo()),
                    new CastingImage(),
                    castAsStarter ? player.getUuid() : null,
                    HexAPI.instance().getColorizer(player),
                    ritus.getTunedFrequency(sw),
                    pos,
                    List.of()
            )))
                if (!player.isCreative())
                    if (Mutables.isAmel(stack))
                        stack.decrement(1);
                    else
                        // don't worry about it
                        vault.drain(
                            Mutables::isAmel,
                            1,
                            false,
                            Flags.PRESET_UpToHands
                        );
            else
                return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
