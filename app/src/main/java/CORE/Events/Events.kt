package vivas.com.myrouter.Events

import vivas.com.myrouter.Component.BaseComponent
import vivas.com.myrouter.Component.ComponentType
import vivas.com.myrouter.loadEventConfig
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by dung.nt on 7/3/17.
 */

class Event{
    var autoRemoveWhenTriggerAction: Boolean = true
    var action : ((param : Any?) -> Unit)? = null

    constructor(autoRemoveWhenPostAction: Boolean, action: ((param: Any?) -> Unit)?) {
        this.autoRemoveWhenTriggerAction = autoRemoveWhenPostAction
        this.action = action
    }

    constructor(action: ((param: Any?) -> Unit)) {
        this.action = action
    }

}


class Events : BaseComponent{


    var eventMap = java.util.Collections.synchronizedMap(HashMap<String,ArrayList<Event>>())

    override fun priority(): Int {
        return 1
    }

    override fun loadConfig() {
        this.loadEventConfig()
    }

    override fun start() {

    }

    override fun reset() {
        eventMap.clear()
        this.loadEventConfig()
    }

    override fun componentType(): ComponentType {
        return ComponentType.EVENT
    }

    fun register(eventName : String,event : Event){
        var e = eventMap[eventName]
        if (e == null){
            e = ArrayList<Event>()
            e.add(event)
            eventMap[eventName] = e

        }else{
            if (e.contains(event) == false){
                e.add(event)
            }
        }
    }
    fun unRegister(eventName : String){
        this.eventMap.remove(eventName)
    }



    fun trigger(eventName : String, information : Any? = null){
        if (this.eventMap == null){

            return
        }

        var actionObjects = this.eventMap[eventName]

        if (actionObjects != null){
            for (i in (actionObjects.count() - 1) downTo 0){
                val e = actionObjects[i]
                if (e.action != null){
                    e.action!!(information)
                }
                if (e.autoRemoveWhenTriggerAction == true){
                    actionObjects.remove(e)
                }
            }
        }
    }
}