package com.sportstalk.api.chat;

import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.common.IUserConfigurable;

public interface ITalkClient extends IUserConfigurable, ISportsTalkConfigurable {
    public ApiResult sendCommand(String command, CommandOptions commandOptions);
    public ApiResult sendReply(String message, String replyTo, CommandOptions commandOptions);
    public ApiResult sendReaction(Reaction reaction, String reactToMessageId, CommandOptions commandOptions);
    public ApiResult sendAdvertisement(AdvertisementOptions advertisementOptions);
    public ApiResult sendGoal(String message, String img, GoalOptions goalOptions);
}
