package com.moutamid.daiptv.lisetenrs;

import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;

public interface ItemSelectedHome {
    void selected(ChannelsModel model);
    void cancel();
}
