package de.goldendeveloper.votemanager.discord.commands;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.votemanager.Main;
import de.goldendeveloper.votemanager.MysqlConnection;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class Vote implements CommandInterface {

    public static final String addVoteModelID = "add-vote";
    public static final String addVoteModelTitle = "title";
    public static final String addVoteModelDescription = "description";

    @Override
    public CommandData commandData() {
        return Commands.slash("vote", "Erstellt eine Vote Nachricht!");
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.settingTable);
        String roleID = table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).getData().get(MysqlConnection.clmVoteRole).getAsString();
        if (!roleID.isBlank()) {
            Role role = e.getJDA().getRoleById(roleID);
            if (hasRole(role, e.getMember())) {
                TextInput title = TextInput.create(addVoteModelTitle, "Titel", TextInputStyle.SHORT)
                        .setPlaceholder("Abstimmung´s Title").setMinLength(5).setMaxLength(50).build();

                TextInput description = TextInput.create(addVoteModelDescription, "Beschreibung", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Abstimmung´s Beschreibung").setMinLength(15).setMaxLength(1000).build();

                Modal modal = Modal.create(addVoteModelID, "Hinzufügen eines Abstimmung´s eintrags!")
                        .addComponents(ActionRow.of(title), ActionRow.of(description)).build();

                e.replyModal(modal).queue();
            } else {
                e.reply("Du besitzt nicht genügend Rechte für diesen Befehl! (Dir fehlt die Rolle [" + role.getAsMention() + "])").queue();
            }
        } else {
            e.reply("ERROR: [404][Role in MysqlTable not Found] Lade den Bot neu auf deinen Discord Server ein!!!").queue();
        }
    }

    public boolean hasRole(Role role, Member m) {
        for (Role r : m.getRoles()) {
            if (r == role) {
                return true;
            }
        }
        return false;
    }
}
