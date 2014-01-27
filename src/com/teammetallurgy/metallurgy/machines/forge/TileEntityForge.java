package com.teammetallurgy.metallurgy.machines.forge;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.teammetallurgy.metallurgy.machines.TileEntityMetallurgySided;

public class TileEntityForge extends TileEntityMetallurgySided implements IFluidHandler
{
    FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);

    public TileEntityForge()
    {
        super(2, new int[] { 0 }, new int[] { 0 }, new int[] { 1 });
    }

    @Override
    protected void writeCustomNBT(NBTTagCompound compound)
    {
        super.writeCustomNBT(compound);

        this.tank.writeToNBT(compound);
    }

    @Override
    protected void readCustomNBT(NBTTagCompound data)
    {
        super.readCustomNBT(data);

        this.tank = this.tank.readFromNBT(data);
    }

    @Override
    protected void processItem()
    {
        if (this.canProcessItem())
        {
            ItemStack itemstack = getSmeltingResult(this.itemStacks[0]);

            if (this.itemStacks[1] == null)
            {
                this.itemStacks[1] = itemstack.copy();
            }
            else if (this.itemStacks[1].isItemEqual(itemstack))
            {
                this.itemStacks[1].stackSize += itemstack.stackSize;
            }

            --this.itemStacks[0].stackSize;

            if (this.itemStacks[0].stackSize <= 0)
            {
                this.itemStacks[0] = null;
            }

            this.tank.drain(100, true);
            
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public boolean isBurning()
    {
        return this.tank.getFluidAmount() > 0;
    }

    @Override
    protected boolean canProcessItem()
    {
        if (this.itemStacks[0] == null)
        {
            return false;
        }
        else
        {
            if (this.tank.getFluidAmount() <= 0) return false;
            ItemStack itemstack = getSmeltingResult(this.itemStacks[0]);
            if (itemstack == null) return false;
            if (slotsAreEmtpty(1, 1)) return true;
            return canAcceptStackRange(1, 1, itemstack);
        }
    }

    @Override
    public String getInvName()
    {
        return "container.forge";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return (i != 1);
    }

    @Override
    protected ItemStack getSmeltingResult(ItemStack... itemStack)
    {
        return FurnaceRecipes.smelting().getSmeltingResult(itemStack[0]);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return this.tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(this.tank.getFluid())) { return null; }

        return this.tank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return this.tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return FluidRegistry.LAVA.equals(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[] { this.tank.getInfo() };
    }

    public FluidTank getTank()
    {
        return this.tank;
    }

}
