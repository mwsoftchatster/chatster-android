package nl.mwsoft.www.chatster.modelLayer.event.chat

class SpyModeEvent {
    var isInSpyMode = false

    constructor() {}

    constructor(isInSpyMode: Boolean) {
        this.isInSpyMode = isInSpyMode
    }

}