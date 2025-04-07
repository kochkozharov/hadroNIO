package de.hhu.bsinfo.hadronio;

import de.hhu.bsinfo.hadronio.binding.UcxWorker;
import de.hhu.bsinfo.hadronio.util.CloseCallback;

public interface HadronioSelectableChannel {

    void select();

    int readyOps();

    UcxWorker getWorker();

    void setCloseCallback(final CloseCallback<HadronioSelectableChannel> callback);
}
