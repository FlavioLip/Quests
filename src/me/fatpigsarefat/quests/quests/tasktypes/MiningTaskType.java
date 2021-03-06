package me.fatpigsarefat.quests.quests.tasktypes;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgressFile;
import me.fatpigsarefat.quests.player.questprogressfile.TaskProgress;
import me.fatpigsarefat.quests.quests.Quest;
import me.fatpigsarefat.quests.quests.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public final class MiningTaskType extends TaskType {

    public MiningTaskType() {
        // type, author, description
        super("blockbreak", "fatpigsarefat", "Break a set amount of blocks.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        QPlayer qPlayer = Quests.getPlayerManager().getPlayer(event.getPlayer().getUniqueId()); // get the qplayer so you can get their progress
        QuestProgressFile questProgressFile = qPlayer.getQuestProgressFile(); // the quest progress file stores progress about all quests and tasks

        for (Quest quest : super.getRegisteredQuests()) { // iterate through all quests which are registered to use this task type
            if (questProgressFile.hasStartedQuest(quest)) { // check if the player has actually started the quest before progressing it
                QuestProgress questProgress = questProgressFile.getQuestProgress(quest); // get their progress for the specific quest

                for (Task task : quest.getTasksOfType(super.getType())) { // get all tasks of this type
                    TaskProgress taskProgress = questProgress.getTaskProgress(task.getId()); // get the task progress and increment progress by 1

                    if (taskProgress.isCompleted()) { // dont need to increment a completed task
                        continue;
                    }

                    int brokenBlocksNeeded = (int) task.getConfigValue("amount"); // this will retrieve a value from the config under the key "value"

                    int progressBlocksBroken;
                    if (taskProgress.getProgress() == null) { // note: if the player has never progressed before, getProgress() will return null
                        progressBlocksBroken = 0;
                    } else {
                        progressBlocksBroken = (int) taskProgress.getProgress();
                    }

                    taskProgress.setProgress(progressBlocksBroken + 1); // the progress does not have to be an int, although must be serializable by the yaml provider

                    if (((int) taskProgress.getProgress()) >= brokenBlocksNeeded) { // completion statement, if true the task is complete
                        taskProgress.setCompleted(true);
                    }
                }
            }
        }
    }

}
