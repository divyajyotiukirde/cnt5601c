package processes;

import java.util.Collection;

public interface PeerObserver {

    public void updateOnPeerDownloadFinished();
    public void updateOnPeersChoke (Collection<Integer> chokedPeersIds);
    public void updateOnPeersUnchoke (Collection<Integer> unchokedPeersIds);
}
