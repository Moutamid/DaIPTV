package com.moutamid.daiptv.lisetenrs;

import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

public interface ItemSelected {
    void selected(ChannelsFilmsModel model);
    void selectedSeries(ChannelsSeriesModel model);
    void cancel();
}
