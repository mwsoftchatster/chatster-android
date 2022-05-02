package nl.mwsoft.www.chatster.modelLayer.event.creators

class LastItemEvent {
    var isLastItem = false

    constructor() {}

    constructor(isLastItem: Boolean) {
        this.isLastItem = isLastItem
    }
}