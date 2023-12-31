package me.alenalex.notaprisoncore.paper.task;

import me.alenalex.notaprisoncore.message.models.HeartbeatMessage;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractTask;

public class HeartbeatTask extends AbstractTask {

    private boolean firstTime;

    public HeartbeatTask() {
        this.firstTime = true;
        this.run();
    }

    @Override
    public void run() {
        try {
            getPluginInstance().getMessageService().getHeartbeatService().sendMessage(new HeartbeatMessage.OnlineAnnouncementRequest(firstTime));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        firstTime = false;
    }
}
