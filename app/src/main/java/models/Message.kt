package models

import java.util.*


class Message {

    constructor() //empty for firebase

    constructor(messageText: String, disName: String){
        text = messageText
        name = disName
    }
    var text: String? = null
    var timestamp: String = Date().toString()
    var name: String? = null
}