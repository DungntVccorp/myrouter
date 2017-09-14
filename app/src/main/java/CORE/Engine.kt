package vivas.com.myrouter

import vivas.com.myrouter.Component.BaseComponent
import vivas.com.myrouter.Component.ComponentType

class Engine{


    init {
        println("INIT ENGINE")
    }

    private var listComponent  = java.util.Collections.synchronizedMap(LinkedHashMap<ComponentType, BaseComponent>())

    private object Holder {

        val INSTANCE = Engine()
    }

    companion object {
        val instance: Engine by lazy { Holder.INSTANCE }
    }


    fun start(){
        loadConfigComponent()
        startAllComponent()
        println("ENGINE STARTED")



    }

    fun addComponent(com : BaseComponent){
        this.listComponent.put(com.componentType(),com)
    }
    fun getComponent(type : ComponentType) : BaseComponent?{
        return this.listComponent[type]
    }


    private fun startAllComponent(){
        for (com in listComponent){
            com.value.loadConfig()
            com.value.start()
        }
    }
    fun resetAllComponent(){
        for (com in listComponent){
            com.value.reset()
        }
    }


}
