package nl.mwsoft.www.chatster.presenterLayer.invite;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

public class InvitePresenter {

    private NetworkLayer networkLayer;

    public InvitePresenter() {
    }

    public InvitePresenter(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

    public Observable<String> inviteUser(String userName, String inviteeName, String inviteeEmail){
        return networkLayer.inviteUser(userName, inviteeName, inviteeEmail);
    }
}
