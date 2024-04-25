package com.moutamid.daiptv.lisetenrs;

import com.moutamid.daiptv.models.ChannelsModel;

public interface ItemSelected {
    void selected(ChannelsModel model);
    void cancel();
}
