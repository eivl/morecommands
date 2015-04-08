package com.mrnobody.morecommands.patch;

import java.util.Iterator;
import java.util.List;

import scala.actors.threadpool.Arrays;

import com.mrnobody.morecommands.command.CommandBase;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

/**
 * The patched class of {@link net.minecraft.command.ServerCommandManager} <br>
 * Patching this class is needed to use commands e.g. with a command block <br>
 * The vanilla command manager passes e.g. the command block as command sender,
 * this modified version will use players selected by a target selector
 * 
 * @author MrNobody98
 *
 */
public class ServerCommandManager extends net.minecraft.command.ServerCommandManager {
	@Override
    public int executeCommand(ICommandSender sender, String rawCommand)
    {
        rawCommand = rawCommand.trim();

        if (rawCommand.startsWith("/"))
        {
            rawCommand = rawCommand.substring(1);
        }

        String[] astring = rawCommand.split(" ");
        String s1 = astring[0];
        astring = dropFirstString(astring);
        ICommand icommand = (ICommand)this.getCommands().get(s1);
        int i = this.getUsernameIndex(icommand, astring);
        int j = 0;
        ChatComponentTranslation chatcomponenttranslation;

        if (icommand == null)
        {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.notFound", new Object[0]);
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        }
        else if (icommand.canCommandSenderUse(sender))
        {
            net.minecraftforge.event.CommandEvent event = new net.minecraftforge.event.CommandEvent(icommand, sender, astring);
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
            {
                if (event.exception != null)
                {
                    com.google.common.base.Throwables.propagateIfPossible(event.exception);
                }
                return 1;
            }
            
            if (icommand instanceof CommandBase) {
                if (astring.length > 0 && astring[astring.length - 1].startsWith("@"))
                {
                    List list = PlayerSelector.matchEntities(sender, astring[astring.length - 1], Entity.class);
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());
                    Iterator iterator = list.iterator();
                    astring = (String[]) Arrays.copyOfRange(astring, 0, astring.length - 1);

                    while (iterator.hasNext())
                    {
                        if (this.tryExecute((Entity)iterator.next(), astring, icommand, rawCommand)) ++j;
                    }
                }
                else
                {
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

                    if (this.tryExecute(sender, astring, icommand, rawCommand))
                    {
                        ++j;
                    }
                }
            }
            else {
                if (i > -1)
                {
                    List list = PlayerSelector.matchEntities(sender, astring[i], Entity.class);
                    String s2 = astring[i];
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        Entity entity = (Entity)iterator.next();
                        astring[i] = entity.getUniqueID().toString();

                        if (this.tryExecute(sender, astring, icommand, rawCommand))
                        {
                            ++j;
                        }
                    }

                    astring[i] = s2;
                }
                else
                {
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

                    if (this.tryExecute(sender, astring, icommand, rawCommand))
                    {
                        ++j;
                    }
                }
            }
        }
        else
        {
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        }

        sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, j);
        return j;
    }
    
    private static String[] dropFirstString(String[] input)
    {
        String[] astring1 = new String[input.length - 1];
        System.arraycopy(input, 1, astring1, 0, input.length - 1);
        return astring1;
    }
    
    private int getUsernameIndex(ICommand command, String[] args)
    {
        if (command == null)
        {
            return -1;
        }
        else
        {
            for (int i = 0; i < args.length; ++i)
            {
                if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i]))
                {
                    return i;
                }
            }

            return -1;
        }
    }
}
