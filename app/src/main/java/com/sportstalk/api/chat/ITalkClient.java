package com.sportstalk.api.chat;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.common.IUserConfigurable;
import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Reaction;

public interface ITalkClient extends IUserConfigurable, ISportsTalkConfigurable {
    ApiResult sendCommand(String command, CommandOptions commandOptions);

    ApiResult sendReply(String message, String replyTo, CommandOptions commandOptions);

    ApiResult sendReaction(Reaction reaction, String reactToMessageId, CommandOptions commandOptions);

    ApiResult sendAdvertisement(AdvertisementOptions advertisementOptions);

    ApiResult sendGoal(String message, String img, GoalOptions goalOptions);
}
